package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fr.utc.irma.GraphAgent.Force;
import fr.utc.irma.ontologies.Ingredient;
import fr.utc.irma.ontologies.IngredientsManager;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.Recipe;
import fr.utc.irma.ontologies.RecipesManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

public class GraphContainer {
	public ArrayList<GraphCriteriaAgent> criterias = new ArrayList<GraphCriteriaAgent>();
	public ArrayList<GraphRecipeAgent> visibleRecipes = new ArrayList<GraphRecipeAgent>();
	public ArrayList<Ingredient> globalCriterias = new ArrayList<Ingredient>();
	
	private ArrayList<Recipe> allRecipes = new ArrayList<Recipe>();
	private Context a;
	
	private void loadAllRecipes(){
    	try {
    	    OntologyQueryInterfaceConnector OQIC = new OntologyQueryInterfaceConnector(a.getAssets());
    	    RecipesManager  rcpMng = new RecipesManager (OQIC);
    	    Iterator<Recipe> allIng = rcpMng.getAll().iterator();
    	    while(allIng.hasNext())
    	    	allRecipes.add(allIng.next());
    	} catch (IOException e) {
    	    Log.d("RecipeLoader","Haha, nobody cares");
    	}
    	//((DynamicTableView)findViewById(R.id.ingList)).updateDisplay();
    }
	
	public GraphContainer(Context a) {
		this.a=a;
		
		/*for(int i=0; i<2; i++){
			addCriteria(new Ingredient("ingredient", i==0?"recettePoulet":"recetteChocolat"));
		}*/
		loadAllRecipes();
		for(Recipe r: allRecipes)
			addRecipe(r);
		
	
		updateCriteriaPosition();
	}
	
	public void updateCriteriaPosition(){
		switch (criterias.size()) {
		case 1:
			criterias.get(0).setPosition(0.5, 0.5);
			break;
		case 2:
			criterias.get(0).setPosition(0.25, 0.5);
			criterias.get(1).setPosition(0.75, 0.5);
			break;
		case 3:
			criterias.get(0).setPosition(0.25, 0.25);
			criterias.get(1).setPosition(0.75, 0.25);
			criterias.get(2).setPosition(0.5, 0.75);
			break;
		

		}
	}
	
	public void addRecipe(Recipe r){
		visibleRecipes.add(new GraphRecipeAgent(r));
	}
	
	public void addCriteria(Ingredient c){
		criterias.add(new GraphCriteriaAgent(c));
		updateCriteriaPosition();
	}
	
	public void makeCriteriaGlobal(GraphCriteriaAgent a){
		globalCriterias.add(a.criteria);
		criterias.remove(a);
		updateCriteriaPosition();
	}
	
	public void draw(Canvas canvas){
		// TODO : Prepare background display (compute dists)
		
		
		// TODO :  Draw backgrounds around criterias to show matchnign items
		
		
		// Draw foreground
		for(GraphCriteriaAgent c : criterias)
			c.draw(canvas);
		for(GraphRecipeAgent c : visibleRecipes){
			c.draw(canvas);
		}
		
	}
	public void tick(){
		// TODO : Criteria vs recipes
		for(GraphCriteriaAgent CA:criterias){
			for(GraphRecipeAgent RA : visibleRecipes){
				// Attract if there is a match
				if(CA.matchAgainstRecipe(RA)){
					Force Fe = CA.elasticForce(RA, 0.01);
					RA.accelerate(-500*Fe.Fx,-500*Fe.Fy);
				}
				// Avoid collision
				Force Fg = CA.gravitationalForce(RA);
				RA.accelerate(-3*Fg.Fx,-3*Fg.Fy);
			}
		}
		
		
		// Recipe vs recipe
		for(int i=0; i<visibleRecipes.size()-1 ; i++){
			for(int j=i+1; j<visibleRecipes.size(); j++){
				GraphRecipeAgent r1 = visibleRecipes.get(i);
				GraphRecipeAgent r2 = visibleRecipes.get(j);
				Force F = r1.gravitationalForce(r2);
				r1.accelerate(F.Fx,F.Fy);
				r2.accelerate(-F.Fx,-F.Fy);
				
			}
		}
		// Recipes move
		for(GraphRecipeAgent RA:visibleRecipes){
			RA.tick();
		}
			
		
	}
	
	

}
