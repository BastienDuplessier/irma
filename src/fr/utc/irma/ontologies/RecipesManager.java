package fr.utc.irma.ontologies;

import java.util.ArrayList;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import fr.utc.irma.ExecutableTask;

public class RecipesManager {


	private static final String PREFIX = "PREFIX irma: <http://www.w3.org/2014/06/irma#>" ;
	private OntologyQueryInterfaceConnector connector;

	public RecipesManager(OntologyQueryInterfaceConnector connector) {
		this.connector = connector;
	}

	public ArrayList<Recipe> getAll() {
		return this.fromSPARQL(PREFIX + " "
				+ "SELECT ?id ?name ?url ?imageUrl ?howMany ?preparationTime ?cookTime "
				+ "?difficulty ?textIngredients ?textRecipe WHERE { "
				+ "?id a irma:Recipe . "
				+ "?id irma:name ?name . "
				+ "?id irma:url ?url . "
				+ "?id irma:image_url ?imageUrl . "
				+ "?id irma:how_many ?howMany . "
				+ "?id irma:preparation_time ?preparationTime . "
				+ "?id irma:cook_time ?cookTime . "
				+ "?id irma:difficulty ?difficulty . "
				+ "?id irma:text_ingredients ?textIngredients . "
				+ "?id irma:text_recipe ?textRecipe } ");

	}

	// Build Recipes from SPARQL Query
	public ArrayList<Recipe> fromSPARQL(String query) {
		ResultSet results = connector.executeSparql(query);
		return this.fromResultSet(results);
	}

	// Build Recipes from ResultSet
	private ArrayList<Recipe> fromResultSet(ResultSet inData) {
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();

		while(inData.hasNext()) {
			QuerySolution row = inData.next();
			recipes.add(new Recipe(row));
		}

		return recipes;
	}

	public void asyncDo(ExecutableTask executableTask) {
		ArrayList<Recipe> recipes = null;
		// Load recipes
		//executableTask.execute(recipes);
	}
}
