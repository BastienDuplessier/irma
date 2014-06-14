package fr.utc.irma.ontologies;

import com.hp.hpl.jena.query.QuerySolution;

public class Recipe {
	
	private String id;
	private String name;
	private String url;
	private String imageUrl;
	private String description;
	
	
	public Recipe(QuerySolution row) {
		this.id = row.get("id").toString();
		this.name = row.get("name").toString();
		this.url = row.get("url").toString();
		this.imageUrl = row.get("imageUrl").toString();
		
		// Recipe specific
		this.description = row.get("description").toString();
	}
	
	public String toString() {
		return "" + this.id + " - " + this.name + " -- " + this.url + " -- " + this.imageUrl;
	}
	
	// Get Methods
	public String getId() { return this.id; }
	public String getName() { return this.name; }
	public String getImageUrl(){return this.imageUrl;}
	public String getUrl() { return this.url; }
	public String getDescription() { return this.description; }
}
