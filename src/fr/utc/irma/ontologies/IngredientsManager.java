package fr.utc.irma.ontologies;

import java.util.ArrayList;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class IngredientsManager {


	private static final String PREFIX = "PREFIX irma: <http://www.w3.org/2014/06/irma#>" ;
	private OntologyQueryInterfaceConnector connector;

	public IngredientsManager(OntologyQueryInterfaceConnector connector) {
		this.connector = connector;
	}

	public ArrayList<Ingredient> getAll() {
		return this.fromSPARQL(PREFIX + " "
				+ "SELECT ?id ?name ?url ?imageUrl WHERE { "
				+ "?id a irma:Ingredient . "
				+ "?id irma:name ?name . "
				+ "?id irma:url ?url . "
				+ "?id irma:image_url ?imageUrl } ");

	}

	// Build Ingredients from SPARQL Query
	public ArrayList<Ingredient> fromSPARQL(String query) {
		ResultSet results = connector.executeSparql(query);
		return this.fromResultSet(results);
	}

	// Build Ingredients from ResultSet
	private ArrayList<Ingredient> fromResultSet(ResultSet inData) {
		ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
		
		while(inData.hasNext()) {
			QuerySolution row = inData.next();
			ingredients.add(new Ingredient(row));
		}

		return ingredients;
	}
}
