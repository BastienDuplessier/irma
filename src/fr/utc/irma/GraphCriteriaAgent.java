package fr.utc.irma;

import fr.utc.irma.ontologies.Ingredient;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class GraphCriteriaAgent extends GraphAgent {
	public Ingredient criteria;
	public Double circleSize=null;
	
	public GraphCriteriaAgent(Ingredient c, GraphContainer gc) {
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
	
	public boolean matchAgainstRecipeAgent(GraphRecipeAgent RA){
		return RA.recipe.matchCriteria(this.criteria);
	}
	
	// Background computing and drawing
	@Override
	public void customDrawBefore(Canvas canvas) {
		// Compute dist
		Double closestWrong=null, furthestRight = null;
		for(GraphRecipeAgent RA : this.gc.visibleRecipes){
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
	

}
