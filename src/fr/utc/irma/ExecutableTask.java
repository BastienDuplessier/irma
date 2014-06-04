package fr.utc.irma;

import java.util.ArrayList;

import fr.utc.irma.ontologies.Recipe;

public interface ExecutableTask {
	public void execute(ArrayList<Recipe> recipes);
}
