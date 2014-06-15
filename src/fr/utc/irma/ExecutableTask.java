package fr.utc.irma;

import java.util.ArrayList;

import fr.utc.irma.ontologies.Result;

public interface ExecutableTask {
	public void execute(ArrayList<Result> results);
}
