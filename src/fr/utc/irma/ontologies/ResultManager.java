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


	private static final String PREFIX = "PREFIX irma: <http://www.w3.org/2014/06/irma#>"
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" ;
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

	public void asyncLoad(final ExecutableTask executableTask,
			final String hardSparql,
			final String easySparql) {
		new AsyncTask<Void, Integer, ArrayList<Result>>() {
			
            @Override
            protected ArrayList<Result> doInBackground(Void... params) {
            	
            	ArrayList<Result> trial = fromSPARQL(hardSparql);
            	if(trial.size()>0)
            		return trial;
            	else
            		return fromSPARQL(easySparql);
            }
            @Override
            protected void onPostExecute(ArrayList<Result> result) {
            	executableTask.execute(result);
			}
		}.execute();
	}
	
	public void asyncLoadWithCriterias(
			ExecutableTask executableTask,
			ArrayList<Criteria> iNeedDat,
			ArrayList<Criteria> wouldBeNice,
			Object[] noNoIds) {
		ArrayList<Criteria> toSearch=(ArrayList<Criteria>)iNeedDat.clone();
		
		ArrayList<String> nono = new ArrayList<String>();
		for(Object no : noNoIds)
			nono.add(no.toString());
		
		String easy = buildQuery(toSearch, nono);
		for(Criteria heyINeedThatToo : wouldBeNice){
			toSearch.add(heyINeedThatToo);
		}
	    String hard = buildQuery(toSearch, nono);
	    asyncLoad(executableTask, hard, easy);
	    
	    
	}
	
	
    private String buildQuery(ArrayList<Criteria> criterias, ArrayList<String> nonos) {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append(PREFIX);
        queryBuffer.append("SELECT ?id ?name ?url ?imageUrl ?description ?criteria WHERE { "
                + "?id a irma:Result . "
                + "?id irma:name ?name . "
                + "?id irma:url ?url . "
                + "?id irma:image_url ?imageUrl . "
                + "?id irma:description ?description . "
                + "?id irma:linked_to ?criteria . ");
        
        
        for(Criteria criteria:criterias)
            queryBuffer.append("?id irma:linked_to <"+criteria.getId()+">.");
        if(!nonos.isEmpty()){
        	queryBuffer.append("FILTER(");
        	
        	for(String no : nonos)
        		queryBuffer.append("?id != <"+no+"> && ");
        	
        	queryBuffer.append("xsd:true)");
        }
        
        queryBuffer.append(" } limit 200");
        return queryBuffer.toString();
    }

    public void asyncLoadAll(ExecutableTask executableTask) {
        asyncLoad(executableTask, getAllQuery(), getAllQuery());
    }

    private String getAllQuery() {
        return PREFIX + " "
                + "SELECT ?id ?name ?url ?imageUrl ?description ?criteria WHERE { "
                + "?id a irma:Result . "
                + "?id irma:name ?name . "
                + "?id irma:url ?url . "
                + "?id irma:image_url ?imageUrl . "
                + "?id irma:description ?description ."
                + "?id irma:linked_to ?criteria } limit 200";
    }
    
    
    
}
