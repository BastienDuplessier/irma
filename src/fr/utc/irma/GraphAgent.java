package fr.utc.irma;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class GraphAgent {
	public double x = 0, y = 0;
	private double speedX = 0, speedY = 0;

	public Paint nodePaint = new Paint();
	public float displayRadius = 0.01f;
	public GraphContainer gc;

	public GraphAgent(GraphContainer c) {
		this.gc = c;
		this.x = Math.random() * 0.2 - 0.1 + 0.5;
		this.y = Math.random() * 0.2 - 0.1 + 0.5;
		nodePaint.setColor(Color.BLACK);
		nodePaint.setAlpha(128);
		nodePaint.setStyle(Style.FILL);
		initGraphics();
	}

	public void initGraphics() {

	}

	public void tick() {
		x += speedX;
		y += speedY;
		speedX *= 0.99;
		speedY *= 0.99;

	}

	public void accelerate(double x, double y) {
		speedX += x;
		speedY += y;
		double goodSpd = 0.01;
		if (speedX * speedX + speedY * speedY > goodSpd * goodSpd) {
			double spdTooMuch = Math.sqrt(speedX * speedX + speedY * speedY)
					/ goodSpd;
			speedX /= spdTooMuch;
			speedY /= spdTooMuch;
		}
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void customDrawBefore(Canvas canvas) {

	}

	public void customDrawAfter(Canvas canvas) {

	}

	public void draw(Canvas canvas) {
		canvas.drawCircle((float) (canvas.getWidth() * this.x),
				(float) (canvas.getHeight() * this.y), canvas.getWidth()
						* displayRadius, nodePaint);
		customDrawAfter(canvas);
	}

	public Force elasticForce(GraphAgent GA) {
		double dx = this.x - GA.x;
		double dy = this.y - GA.y;
		double d2 = dx * dx + dy * dy;
		double d = Math.sqrt(d2);
		/*
		 * double ux=dx/d; double uy=dy/d;
		 */
		Force F = new Force();
		F.Fx = -0.00001 * d * dx;
		F.Fy = -0.00001 * d * dy;
		return F;
	}

	public Force gravitationalForce(GraphAgent GA) {
		double dx = this.x - GA.x;
		double dy = this.y - GA.y;
		double d2 = dx * dx + dy * dy + 0.01;
		double d = Math.sqrt(d2);
		double ux = dx / d;
		double uy = dy / d;
		Force F = new Force();
		F.Fx = 0.00001 / d2 * ux;
		F.Fy = 0.00001 / d2 * uy;
		return F;
	}

	public double d2to(GraphAgent GA) {
		double dx = this.x - GA.x;
		double dy = this.y - GA.y;
		return dx * dx + dy * dy + 0.001;
	}

	public class Force {
		public double Fx;
		public double Fy;
	}

}
