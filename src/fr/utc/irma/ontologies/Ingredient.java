package fr.utc.irma.ontologies;

import java.io.Serializable;

import com.hp.hpl.jena.query.QuerySolution;

public class Ingredient implements Serializable {
	
	private static final long serialVersionUID = 6894113962246182007L;
	
	private String id;
	private String name;
	private String url;
	private String imageUrl;
	
	
	public Ingredient(QuerySolution row) {
		this.id = row.get("id").toString();
		this.name = row.get("name").toString();
		this.url = row.get("url").toString();
		this.imageUrl = row.get("imageUrl").toString();
	}
	
	public String toString() {
		return this.name;
	}
	
	public boolean matchAgainstRecipe(Recipe R){
		return R.getName().toLowerCase().indexOf(name.toLowerCase())!=-1;
	}
	// Get Methods
	public String getId() { return this.id; }
	public String getName() { return this.name; }
	public String getUrl() { return this.url; }
	public String getImageUrl() { return this.imageUrl; }
}
