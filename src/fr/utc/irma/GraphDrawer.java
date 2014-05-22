package fr.utc.irma;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class GraphDrawer extends View {
	Paint reciepeColor = new Paint();
	Paint filtersColor = new Paint();
	int backgroundColor = Color.rgb(0, 0, 0);
	private void init(){
		reciepeColor.setColor(Color.rgb(100, 200, 100));
		reciepeColor.setStyle(Style.FILL);
		filtersColor.setColor(Color.rgb(200, 100, 100));
		filtersColor.setStyle(Style.FILL);
	}
	
	public GraphDrawer(Context context) {
		super(context);
		init();
	}

	public GraphDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(backgroundColor);
		
		canvas.drawCircle(200, 200, 10, reciepeColor);
		invalidate();
	}

	public GraphDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

}
