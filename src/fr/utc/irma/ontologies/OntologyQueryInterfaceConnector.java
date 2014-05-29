package fr.utc.irma.ontologies;

import java.io.IOException;

import android.content.res.AssetManager;

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
	// TODO: Put real data, not sample
	private static final String IRMA_DATA = "irma_data.n3";

	private Model model = ModelFactory.createDefaultModel();

	public OntologyQueryInterfaceConnector(AssetManager assetManager) throws IOException {
		model.read(assetManager.open(UNIQUE_ONTOLOGIES_DIRECTORY_NAME + IRMA_GENERIC), null, "TURTLE");
		model.read(assetManager.open(UNIQUE_ONTOLOGIES_DIRECTORY_NAME + IRMA_SPECIFIC), null, "TURTLE");
		model.read(assetManager.open(UNIQUE_ONTOLOGIES_DIRECTORY_NAME + IRMA_DATA), null, "TURTLE");
	}

	public ResultSet executeSparql(String stringQuery) {
		Query query = QueryFactory.create(stringQuery);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
		return queryExecution.execSelect();
	}
}
