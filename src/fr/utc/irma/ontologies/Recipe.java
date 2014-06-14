package fr.utc.irma.ontologies;

import com.hp.hpl.jena.query.QuerySolution;

public class Recipe {
	
	private String id;
	private String name;
	private String url;
	private String imageUrl;
	private String howMany;
	private String preparationTime;
	private String cookTime;
	private String difficulty;
	private String textIngredients;
	private String textRecipe;
	
	
	public Recipe(QuerySolution row) {
		this.id = row.get("id").toString();
		this.name = row.get("name").toString();
		this.url = row.get("url").toString();
		this.imageUrl = row.get("imageUrl").toString();
		
		// Recipe specific
		this.howMany = row.get("howMany").toString();
		this.preparationTime = row.get("preparationTime").toString();
		this.cookTime = row.get("cookTime").toString();
		this.difficulty = row.get("difficulty").toString();
		this.textIngredients = row.get("textIngredients").toString();
		this.textRecipe = row.get("textRecipe").toString();
	}
	
	public String toString() {
		return "" + this.id + " - " + this.name + " -- " + this.url + " -- " + this.imageUrl;
	}
	
	// Get Methods
	public String getId() { return this.id; }
	public String getName() { return this.name; }
	public String getImageUrl(){return this.imageUrl;}
	public String getUrl() { return this.url; }
	public String getHowMany() { return this.howMany; }
	public String getPreparationTime() { return this.preparationTime; }
	public String getCookTime() { return this.cookTime; }
	public String getDifficulty() { return this.difficulty; }
	public String getTextIngredients() { return this.textIngredients; }
	public String getTextRecipe() { return this.textRecipe; }
}
