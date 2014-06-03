package fr.utc.irma;

import fr.utc.irma.ontologies.Ingredient;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class GraphCriteriaAgent extends GraphAgent {
	public Ingredient criteria;
	private Paint textPaint = new Paint();
	
	public GraphCriteriaAgent(Ingredient c) {
		super();
		this.criteria=c;
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Align.CENTER);
	}
	
	@Override
	public void initGraphics() {
		nodePaint.setColor(Color.RED);
		displayRadius=40f;
	}
	
	@Override
	public void customDraw(Canvas canvas) {
		canvas.drawText(criteria.getName(), (float)( canvas.getWidth()*this.x),(float)( canvas.getHeight()*this.y), textPaint);
	}
	
	public boolean matchAgainstRecipe(GraphRecipeAgent RA){
		return true;//this.criteria.matchAgainstRecipe(RA.recipe);
	}
	

}
