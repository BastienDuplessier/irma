package fr.utc.irma.ontologies;

import java.util.ArrayList;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class RecipesManager {


	private static final String PREFIX = "PREFIX irma: <http://www.w3.org/2014/06/irma#>" ;
	private OntologyQueryInterfaceConnector connector;

	public RecipesManager(OntologyQueryInterfaceConnector connector) {
		this.connector = connector;
	}

	public ArrayList<Recipe> getAll() {
		return this.fromSPARQL(PREFIX + " "
				+ "SELECT DISTINCT ?id ?name ?url ?imageUrl ?howMany ?preparationTime ?cookTime "
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
	
	// Build Recipes with filters
	public ArrayList<Recipe> getFiltered(Filter filter) {
	    String sparqlQuery = getSPARQLQuery(filter.toSPARQLFragment());
	    System.out.println(sparqlQuery);
	    return fromSPARQL(sparqlQuery);
	}

    private String getSPARQLQuery(String sparqlFragment) {
        return PREFIX + " "
                + "SELECT DISTINCT ?id ?name ?url ?imageUrl ?howMany ?preparationTime ?cookTime "
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
                + "?id irma:text_recipe ?textRecipe . "
                + sparqlFragment + "} ";
    }
}
