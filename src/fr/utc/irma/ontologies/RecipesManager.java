package fr.utc.irma.ontologies;

import java.util.ArrayList;
import java.util.Hashtable;

import android.os.AsyncTask;

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
		return this.fromSPARQL(getAllQuery());

	}

	// Build Recipes from SPARQL Query
	public ArrayList<Recipe> fromSPARQL(String query) {
		ResultSet results = connector.executeSparql(query);
		return this.fromResultSet(results);
	}

	// Build Recipes from ResultSet
	private ArrayList<Recipe> fromResultSet(ResultSet inData) {
		Hashtable<String, Recipe> recipes = new Hashtable<String, Recipe>();

		while(inData.hasNext()) {
			QuerySolution row = inData.next();
			Recipe recipe = recipes.get(row.get("id").toString());
			if(recipe == null)
			    recipes.put(row.get("id").toString(), new Recipe(row));
			else
			    recipe.addCriteria(row);
		}

		return new ArrayList<Recipe>(recipes.values());
	}

	public void asyncLoad(final ExecutableTask executableTask, final String sparqlQuery) {
		new AsyncTask<Void, Integer, ArrayList<Recipe>>() {
			
            @Override
            protected ArrayList<Recipe> doInBackground(Void... params) {
                return fromSPARQL(sparqlQuery);
            }
            @Override
            protected void onPostExecute(ArrayList<Recipe> result) {
            	executableTask.execute(result);
			}
		}.execute();
	}

    public void asyncLoadAll(ExecutableTask executableTask) {
        asyncLoad(executableTask, getAllQuery());
    }

    private String getAllQuery() {
        return PREFIX + " "
                + "SELECT ?id ?name ?url ?imageUrl ?description ?criteria WHERE { "
                + "?id a irma:Recipe . "
                + "?id irma:name ?name . "
                + "?id irma:url ?url . "
                + "?id irma:image_url ?imageUrl . "
                + "?id irma:description ?description ."
                + "?id irma:linked_to ?criteria } ";
    }
}
