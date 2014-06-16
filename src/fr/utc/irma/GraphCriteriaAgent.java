package fr.utc.irma;

import java.util.ArrayList;
import java.util.HashSet;

import fr.utc.irma.GraphAgent.Force;
import fr.utc.irma.ontologies.Criteria;
import fr.utc.irma.ontologies.Result;
import fr.utc.irma.ontologies.ResultManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class GraphCriteriaAgent extends GraphAgent {
	public Criteria criteria;
	public Double circleSize=null;
	
	public GraphCriteriaAgent(Criteria c, GraphContainer gc) {
		super(gc);
		this.criteria=c;
		
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.CENTER);
		
		bigCirclesPaint.setColor(Color.RED);
		bigCirclesPaint.setAlpha(100);
		bigCirclesPaint.setStyle(Style.FILL);
	}
	
	public Paint textPaint = new Paint();
	public Paint bigCirclesPaint = new Paint();
	@Override
	public void initGraphics() {
		nodePaint.setColor(Color.RED);
		displayRadius=0.05f;
	}
	
	@Override
	public void customDrawAfter(Canvas canvas) {
		canvas.drawText(criteria.getName(), (float)( canvas.getWidth()*this.x),(float)( canvas.getHeight()*this.y), textPaint);
	}
	
	public boolean matchAgainstRecipeAgent(GraphResultAgent RA){
		return RA.result.matchCriteria(this.criteria);
	}
	
	
	public void attractOrRevulseResults(ArrayList<GraphResultAgent> visibleRecipes){
		for (GraphResultAgent RA : visibleRecipes) {
			// Attract if there is a match
			Force Fe = this.elasticForce(RA);
			Force Fg = this.gravitationalForce(RA);
			
			if (RA.result.matchCriteria(this.criteria)) {
				RA.accelerate(-700 * Fe.Fx, -700 * Fe.Fy);
			}else{
				RA.accelerate(-10 * Fg.Fx, -10 * Fg.Fy);
			}
			// Avoid collision
			RA.accelerate(-1 * Fg.Fx, -1 * Fg.Fy);
		}
	}
	
	// Background computing and drawing
	@Override
	public void customDrawBefore(Canvas canvas) {
		// Compute dist
		Double closestWrong=null, furthestRight = null;
		for(GraphResultAgent RA : this.gc.visibleRecipes){
			if(this.matchAgainstRecipeAgent(RA)){
				if(furthestRight==null || this.d2to(RA)>furthestRight)
					furthestRight=this.d2to(RA);
			}else{
				if(closestWrong==null || this.d2to(RA)<closestWrong)
					closestWrong=this.d2to(RA);
			}
		}
		if(closestWrong==null)
			closestWrong=furthestRight;
		// Do we draw a circle ?
		if(closestWrong!= null && furthestRight!=null && furthestRight<=closestWrong){
			furthestRight=Math.sqrt(furthestRight);
			circleSize=furthestRight;
			furthestRight+=0.05;
			canvas.drawOval(
					new RectF(
							(float)((float)canvas.getWidth()*(this.x-furthestRight)),
							(float)((float)canvas.getHeight()*(this.y-furthestRight)),
							(float)((float)canvas.getWidth()*(this.x+furthestRight)),
							(float)((float)canvas.getHeight()*(this.y+furthestRight)))
					,
					bigCirclesPaint);
		
		}else{
			circleSize=null;
		}
		
	}
	
	private HashSet<String> lastKnownMatchingRecipes = new HashSet<String>();
	private boolean loadingNewRecipes=false;
	
	public boolean needMoreRecipes(ArrayList<GraphResultAgent> visibleRecipes){
		if(loadingNewRecipes)
			return false;
		lastKnownMatchingRecipes.clear();
		for(GraphResultAgent GRA : visibleRecipes)
			if(GRA.result.matchCriteria(this.criteria))
				lastKnownMatchingRecipes.add(GRA.result.getId());
		return lastKnownMatchingRecipes.size()<10;
	}
	
	
	public void askForMoreResults(GraphContainer GC, ResultManager RM){
		loadingNewRecipes=true;
		nodePaint.setStyle(Style.STROKE);
		ArrayList<Criteria> me = new ArrayList<Criteria>();
		me.add(this.criteria);
		RM.asyncLoadWithCriterias(new ExecutableTask() {
			
			@Override
			public void execute(ArrayList<Result> results) {
				loadingNewRecipes = false;
				nodePaint.setStyle(Style.FILL_AND_STROKE);
				for(Result r:results)
					gc.addRecipe(r);
				
			}
		}, GC.globalCriterias, me, lastKnownMatchingRecipes.toArray());
		
	}
	
	
	

}
