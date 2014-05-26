package fr.utc.irma;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class GraphAgent {
	public double x=0,y=0;
	private double speedX=0,speedY=0;
	
	public Paint nodePaint= new Paint();
	public  float displayRadius = 10f;
	public GraphAgent() {
		this.x=Math.random();
		this.y=Math.random();
		nodePaint.setColor(Color.BLACK);
		nodePaint.setAlpha(128);
		nodePaint.setStyle(Style.FILL);
		initGraphics();
	}
	public void initGraphics(){
		
	}
	public void tick(){
		x+=speedX;
		y+=speedY;
		speedX*=0.99;
		speedY*=0.99;
		
	}
	public void accelerate(double x, double y){
		speedX+=x;
		speedY+=y;
	}
	public void setPosition(double x, double y){
		this.x=x;
		this.y=y;
	}
	public void draw(Canvas canvas){
		canvas.drawCircle((float)( canvas.getWidth()*this.x),(float)( canvas.getHeight()*this.y),displayRadius,nodePaint);  
	}
	public Force gravitationalForce(GraphAgent GA){
		double dx= this.x-GA.x;
		double dy= this.y-GA.y;
		double d2=dx*dx + dy * dy+0.01;
		double d=Math.sqrt(d2);
		double ux=dx/d;
		double uy=dy/d;
		Force F=new Force();
		F.Fx=0.000001/d2*ux; 
		F.Fy=0.000001/d2*uy;
		return F;
	}
	
	public Force elasticForce(GraphAgent GA, double optimalLength){
		double dx= this.x-GA.x;
		double dy= this.y-GA.y;
		double d2=dx*dx + dy * dy;
		double d=Math.sqrt(d2);
		
		double dOffset=optimalLength*optimalLength-d2;
		double ux=dx/d;
		double uy=dy/d;
		Force F=new Force();
		F.Fx=0.000005*dOffset*ux; 
		F.Fy=0.000005*dOffset*uy;
		return F;
	}
	
	
	public class Force{
		public double Fx;
		public double Fy;
	}
	
}
