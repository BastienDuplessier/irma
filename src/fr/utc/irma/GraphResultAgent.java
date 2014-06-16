package fr.utc.irma;

import fr.utc.irma.GraphAgent.Force;
import fr.utc.irma.ontologies.Result;

public class GraphResultAgent extends GraphAgent {
	public Result result;
	public GraphResultAgent(Result result, GraphContainer gc) {
		super(gc);
		this.result=result;
	}
	
	public void avoidCollistionWithResult(GraphResultAgent GRA){
		Force F = this.gravitationalForce(GRA);
		this.accelerate(F.Fx, F.Fy);
		GRA.accelerate(-F.Fx, -F.Fy);
	}
	
	public boolean isOutOfBounds(){
		return (this.x < -0.5 || this.x > 1.5 || this.y < -0.5 || this.y > 1.5) ;
	}

}
