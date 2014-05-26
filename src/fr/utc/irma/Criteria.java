package fr.utc.irma;

public class Criteria {
	public String property;
	public String propertyValue;
	
	public Criteria(String property, String propertyValue) {
		this.property=property;
		this.propertyValue=propertyValue;
	}
	public boolean matchAgainstRecipe(Recipe r){
		return propertyValue.equals(r.url); 
	}

}
