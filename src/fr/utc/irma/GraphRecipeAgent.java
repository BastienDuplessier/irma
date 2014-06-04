package fr.utc.irma;

import fr.utc.irma.ontologies.Recipe;

public class GraphRecipeAgent extends GraphAgent {
	public Recipe recipe;
	public GraphRecipeAgent(Recipe recipe, GraphContainer gc) {
		super(gc);
		this.recipe=recipe;
	}

}
