package fr.utc.irma;

import android.graphics.Color;

public class GraphCriteriaAgent extends GraphAgent {
	public Criteria criteria;
	
	public GraphCriteriaAgent(Criteria c) {
		super();
		this.criteria=c;
	}
	
	@Override
	public void initGraphics() {
		nodePaint.setColor(Color.RED);
		displayRadius=40f;
	}
	

}
