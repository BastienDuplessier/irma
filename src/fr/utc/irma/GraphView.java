package fr.utc.irma;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GraphView extends View {
	public int backgroundColor = Color.WHITE;
	GraphContainer container;
	
	private void init(){
		this.container=new GraphContainer((GraphActivity)this.getContext());
	}
	
	public GraphView(Context context) {
		super(context);
		init();
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(backgroundColor);
		container.tick();
		container.draw(canvas);
		
		// For 2bl click detection
		framesSinceLastClick++;
		
		// Trigger a refresh of the view
		invalidate();
	}

	public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public GraphContainer getContainer(){
		return container;
	}
	
	int framesSinceLastClick=100;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float rel_x=event.getX()/this.getWidth();
		float rel_y=event.getY()/this.getHeight();
		
		container.clickOn(rel_x, rel_y, this);
		
		if(framesSinceLastClick<10){
			
			for(GraphCriteriaAgent GCA : container.getAllClicked(rel_x, rel_y)){
				container.makeCriteriaGlobal(GCA);
			}
			
			
			framesSinceLastClick=100;
		}else{
			framesSinceLastClick=0;
		}
		
		return super.onTouchEvent(event);
	}
	
	public void descCriteria( ArrayList<GraphCriteriaAgent> clickedCriteria){
		((GraphActivity)this.getContext()).setSideBarToCriteriaDescription(clickedCriteria);
		
	}

}
