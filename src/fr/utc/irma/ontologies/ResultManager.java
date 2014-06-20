package fr.utc.irma.ontologies;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import arq.query;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import fr.utc.irma.ExecutableTask;
import fr.utc.irma.GraphCriteriaAgent;

public class ResultManager {
	
	public static ResultManager getRM(AssetManager AM){
		if (singleton==null)
			singleton=new ResultManager(OntologyQueryInterfaceConnector.getOQIC(AM));
		return singleton;
	}
	
	static boolean currentlyworking=false;
	public boolean asyncLoadWithCriterias(
			ExecutableTask executableTask,
			ArrayList<Criteria> iNeedDat,
			ArrayList<Criteria> wouldBeNice,
			Object[] noNoIds) {
		if(currentlyworking)
			return false;
		else
			currentlyworking=true;
		
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
	    
	    return true;
	}

	private static final String PREFIX = "PREFIX irma: <http://www.w3.org/2014/06/irma#>"
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" ;
	private OntologyQueryInterfaceConnector connector;

	private static ResultManager singleton = null; 
	
	
	private  ResultManager(OntologyQueryInterfaceConnector connector) {
		this.connector = connector;
	}

	// Build Recipes from SPARQL Query
	private ArrayList<Result> fromSPARQL(String query) {
		ResultSet results = connector.executeSparql(query);
		return this.fromResultSet(results);
	}

	// Build Recipes from ResultSet
	private ArrayList<Result> fromResultSet(ResultSet inData) {
	    ArrayList<Result> results = new ArrayList<Result>();

		while(inData.hasNext()) {
			Result result = new Result(inData.next()); 
		    results.add(result);
		    for(String critMatch:lastAsked)
		    	result.addCriteriaString("<"+critMatch+">");
			asyncLoadCriterias(result);
		}

		return results;
	}

	private void asyncLoadCriterias(final Result result) {
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ResultSet solution = loadResultCriteria(result);
                while(solution.hasNext())
                    result.addCriteria(solution.next());
                return null;
            }
            
        }.execute();
    }
	
	private ResultSet loadResultCriteria(Result result) {
	    return connector.executeSparql(buildQuery(result));
	}

    private String buildQuery(Result result) {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append(PREFIX);
        queryBuffer.append("SELECT ?criteria WHERE { <");
        queryBuffer.append(result.getId());
        queryBuffer.append("> irma:linked_to ?criteria . }");
        
        return queryBuffer.toString();
    }

    private void asyncLoad(final ExecutableTask executableTask,
			final String hardSparql,
			final String easySparql) {
		new AsyncTask<Void, Integer, ArrayList<Result>>() {
			
            @Override
            protected ArrayList<Result> doInBackground(Void... params) {
            	
            	ArrayList<Result> trial = fromSPARQL(hardSparql);

        	    Log.d("hardRequest", hardSparql+" we got "+trial.size()+" results ");
            	if(trial.size()==0)
            		trial=fromSPARQL(easySparql);
            	
        		return trial;
            }
            @Override
            protected void onPostExecute(ArrayList<Result> result) {
            	executableTask.execute(result);
            	currentlyworking=false;
			}
		}.execute();
	}
	
	
	
	private static ArrayList<String> lastAsked=new ArrayList<String>();
    private String buildQuery(ArrayList<Criteria> criterias, ArrayList<String> nonos) {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append(PREFIX);
        queryBuffer.append("SELECT ?id ?name ?url ?imageUrl  WHERE { "
                + "?id a irma:Result . "
                + "?id irma:name ?name . "
                + "?id irma:url ?url . "
                + "?id irma:image_url ?imageUrl . ");
        lastAsked=new ArrayList<String>();
        for(Criteria criteria:criterias){
            queryBuffer.append("?id irma:linked_to <"+criteria.getId()+">.");
            lastAsked.add(criteria.getId());
        }

        /*if(!nonos.isEmpty()){
        	queryBuffer.append("FILTER(");
        	
        	for(String no : nonos)
        		queryBuffer.append("?id != <"+no+"> && ");
        	
        	queryBuffer.append("xsd:true)");
        }*/
        queryBuffer.append(" } limit 10");
        return queryBuffer.toString();
    }

    
}
