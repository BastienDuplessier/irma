package fr.utc.irma.ontologies;

import java.io.IOException;

import android.content.res.AssetManager;
import android.util.Log;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OntologyQueryInterfaceConnector {
	
	private static final String UNIQUE_ONTOLOGIES_DIRECTORY_NAME = "ontologies/";
	// UNIQUE FILENAMES FOR ONTOLOGIES
	private static final String IRMA_GENERIC = "irma_generic.n3";
	private static final String IRMA_SPECIFIC = "irma_cooking.n3";
    private static final String IRMA_DATA = "irma_data.n3";
    private static final String IRMA_DATA2 = "irma_data2.n3";

	private Model model = ModelFactory.createDefaultModel();

	private OntologyQueryInterfaceConnector(AssetManager assetManager) throws IOException {
		model.read(assetManager.open(UNIQUE_ONTOLOGIES_DIRECTORY_NAME + IRMA_GENERIC), null, "TURTLE");
		Log.d("OKLoad", "generic");
		model.read(assetManager.open(UNIQUE_ONTOLOGIES_DIRECTORY_NAME + IRMA_SPECIFIC), null, "TURTLE");
		Log.d("OKLoad", "specific");
        model.read(assetManager.open(UNIQUE_ONTOLOGIES_DIRECTORY_NAME + IRMA_DATA), null, "TURTLE");
		Log.d("OKLoad", "criterias");
        model.read(assetManager.open(UNIQUE_ONTOLOGIES_DIRECTORY_NAME + IRMA_DATA2), null, "TURTLE");
		Log.d("OKLoad", "recipes");
	}
	
	private static OntologyQueryInterfaceConnector singleton=null;
	public static OntologyQueryInterfaceConnector getOQIC(AssetManager AM){
		if(singleton==null)
			try {
				singleton=new OntologyQueryInterfaceConnector(AM);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return singleton;
	}

	public ResultSet executeSparql(String stringQuery) {
		Log.d("hardRequest", "Was asked to load : "+stringQuery);
		Query query = QueryFactory.create(stringQuery);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
		return queryExecution.execSelect();
	}
}
