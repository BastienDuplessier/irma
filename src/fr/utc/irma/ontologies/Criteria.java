package fr.utc.irma.ontologies;

import java.io.Serializable;

import com.hp.hpl.jena.query.QuerySolution;

public class Criteria implements Serializable {
	
	private static final long serialVersionUID = 6894113962246182007L;
	
	private String id;
	private String name;
	private String url;
	private String imageUrl;
	public boolean optionnal = true;
	
	
	public Criteria(QuerySolution row) {
		this.id = row.get("id").toString();
		this.name = row.get("name") == null ? "" : row.get("name").toString();
		this.url = row.get("url") == null ? "" : row.get("url").toString();
		this.imageUrl = row.get("imageUrl") == null ? "" : row.get("imageUrl").toString();
	}
	
	public String toString() {
		return this.name;
	}
	
	// Get Methods
	public String getId() { return this.id; }
	public String getName() { return this.name; }
	public String getUrl() { return this.url; }
	public String getImageUrl() { return this.imageUrl; }
}
