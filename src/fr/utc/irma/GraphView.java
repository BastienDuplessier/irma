package fr.utc.irma;

import java.util.ArrayList;

import fr.utc.irma.ontologies.Ingredient;
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
		invalidate();
	}

	public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public GraphContainer getContainer(){
		return container;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		container.clickOn(event.getX()/this.getWidth(), event.getY()/this.getHeight(), this);
		return super.onTouchEvent(event);
	}
	
	public void descCriteria( ArrayList<GraphCriteriaAgent> clickedCriteria){
		((GraphActivity)this.getContext()).setSideBarToCriteriaDescription(clickedCriteria);
		
	}

}
