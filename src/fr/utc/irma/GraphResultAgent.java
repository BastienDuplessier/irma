package fr.utc.irma;

import fr.utc.irma.ontologies.Result;

public class GraphResultAgent extends GraphAgent {
	public Result result;
	public GraphResultAgent(Result result, GraphContainer gc) {
		super(gc);
		this.result=result;
	}

}
