package fr.utc.irma.ontologies;

import java.util.ArrayList;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class CriteriasManager {


	private static final String PREFIX = "PREFIX irma: <http://www.w3.org/2014/06/irma#>"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>" ;
	private OntologyQueryInterfaceConnector connector;

	public CriteriasManager(OntologyQueryInterfaceConnector connector) {
		this.connector = connector;
	}

	public ArrayList<Criteria> getAll() {
		return this.fromSPARQL(PREFIX + " "   
				+ "SELECT ?id ?name ?url ?imageUrl WHERE { "
				+ "?id a ?class . "
				+ "?class rdfs:subClassOf irma:Criteria . "
				+ "OPTIONAL { ?id irma:name ?name } . "
				+ "OPTIONAL { ?id irma:url ?url }  . "
				+ "OPTIONAL { ?id irma:image_url ?imageUrl } . } ");
	}

	// Build Ingredients from SPARQL Query
	public ArrayList<Criteria> fromSPARQL(String query) {
		ResultSet results = connector.executeSparql(query);
		return this.fromResultSet(results);
	}

	// Build Ingredients from ResultSet
	private ArrayList<Criteria> fromResultSet(ResultSet inData) {
		ArrayList<Criteria> criterias = new ArrayList<Criteria>();
		
		
		while(inData.hasNext()) {
			QuerySolution row = inData.next();
			criterias.add(new Criteria(row));
		}

		return criterias;
	}
}
