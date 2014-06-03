package fr.utc.irma;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {
	public int backgroundColor = Color.WHITE;
	GraphContainer container;
	private void init(){
		this.container=new GraphContainer();
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

}
