package fr.utc.irma.ontologies;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import android.os.AsyncTask;
import arq.query;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import fr.utc.irma.ExecutableTask;
import fr.utc.irma.GraphCriteriaAgent;

public class ResultManager {


	private static final String PREFIX = "PREFIX irma: <http://www.w3.org/2014/06/irma#>" ;
	private OntologyQueryInterfaceConnector connector;

	public ResultManager(OntologyQueryInterfaceConnector connector) {
		this.connector = connector;
	}

	public ArrayList<Result> getAll() {
		return this.fromSPARQL(getAllQuery());

	}

	// Build Recipes from SPARQL Query
	public ArrayList<Result> fromSPARQL(String query) {
		ResultSet results = connector.executeSparql(query);
		return this.fromResultSet(results);
	}

	// Build Recipes from ResultSet
	private ArrayList<Result> fromResultSet(ResultSet inData) {
		Hashtable<String, Result> results = new Hashtable<String, Result>();

		while(inData.hasNext()) {
			QuerySolution row = inData.next();
			Result result = results.get(row.get("id").toString());
			if(result == null)
			    results.put(row.get("id").toString(), new Result(row));
			else
			    result.addCriteria(row);
		}

		return new ArrayList<Result>(results.values());
	}

	public void asyncLoad(final ExecutableTask executableTask, final String sparqlQuery) {
		new AsyncTask<Void, Integer, ArrayList<Result>>() {
			
            @Override
            protected ArrayList<Result> doInBackground(Void... params) {
                return fromSPARQL(sparqlQuery);
            }
            @Override
            protected void onPostExecute(ArrayList<Result> result) {
            	executableTask.execute(result);
			}
		}.execute();
	}
	
	public void asyncLoadWithCriterias(ExecutableTask executableTask,
			ArrayList<Criteria> globalCriterias,
			ArrayList<GraphCriteriaAgent> GCAs,
			boolean gcaIsOptionnal) {
		ArrayList<Criteria> toSearch=(ArrayList<Criteria>)globalCriterias.clone();
		for(GraphCriteriaAgent gca : GCAs){
			gca.criteria.optionnal=gcaIsOptionnal;
			toSearch.add(gca.criteria);
		}
	    String query = buildQuery(toSearch);
	    asyncLoad(executableTask, query);
	    
	    
	}

    private String buildQuery(ArrayList<Criteria> criterias) {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append(PREFIX);
        queryBuffer.append("SELECT ?id ?name ?url ?imageUrl ?description ?criteria WHERE { "
                + "?id a irma:Recipe . "
                + "?id irma:name ?name . "
                + "?id irma:url ?url . "
                + "?id irma:image_url ?imageUrl . "
                + "?id irma:description ?description . "
                + "?id irma:linked_to ?criteria . ");
        
        Iterator<Criteria> it = criterias.iterator();
        while(it.hasNext()) {
            Criteria criteria = it.next();
            if(criteria.optionnal) {
                queryBuffer.append("OPTIONAL { ?id irma:linked_to <");
                queryBuffer.append(criteria.getId());
                queryBuffer.append("> } . ");
            } else {
                queryBuffer.append("?id irma:linked_to <");
                queryBuffer.append(criteria.getId());
                queryBuffer.append("> . ");
            }
        }
        
        queryBuffer.append(" }");
        return queryBuffer.toString();
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
                + "?id irma:linked_to ?criteria }";
    }
}
