package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.sparql.function.library.min;

import fr.utc.irma.GraphAgent.Force;
import fr.utc.irma.ontologies.Ingredient;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.Recipe;
import fr.utc.irma.ontologies.RecipesManager;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.Toast;

public class GraphContainer {
	public ArrayList<GraphCriteriaAgent> criterias = new ArrayList<GraphCriteriaAgent>();
	public ArrayList<GraphRecipeAgent> visibleRecipes = new ArrayList<GraphRecipeAgent>();
	public ArrayList<Ingredient> globalCriterias = new ArrayList<Ingredient>();
	
	private ArrayList<Recipe> allRecipes = new ArrayList<Recipe>();
	private RecipesManager rcpMng;
	private Context a;
	
	private void loadAllRecipes(){
    	try {
    	    OntologyQueryInterfaceConnector OQIC = new OntologyQueryInterfaceConnector(a.getAssets());
    	    RecipesManager  rcpMng = new RecipesManager (OQIC);
    	    allRecipes = rcpMng.getAll();
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
		case 0:
			break;
		case 1:
			criterias.get(0).setPosition(0.5, 0.5);
			break;
		case 2:
			criterias.get(0).setPosition(0.25, 0.5);
			criterias.get(1).setPosition(0.75, 0.5);
			break;
		default:
			criterias.get(0).setPosition(0.25, 0.25);
			criterias.get(1).setPosition(0.75, 0.25);
			criterias.get(2).setPosition(0.5, 0.75);
			break;
		

		}
	}
	
	public void addRecipe(Recipe r){
		visibleRecipes.add(new GraphRecipeAgent(r, this));
	}
	
	public void addCriteria(Ingredient c){
		criterias.add(new GraphCriteriaAgent(c, this));
		updateCriteriaPosition();
	}
	
	public void makeCriteriaGlobal(GraphCriteriaAgent a){
		globalCriterias.add(a.criteria);
		criterias.remove(a);
		updateCriteriaPosition();
	}
	
	public void draw(Canvas canvas){
	
		// Draw backgrounds 
		for(GraphCriteriaAgent c : criterias)
			c.customDrawBefore(canvas);
		for(GraphRecipeAgent c : visibleRecipes)
			c.customDrawBefore(canvas);
		
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
				if(CA.matchAgainstRecipeAgent(RA)){
					Force Fe = CA.elasticForce(RA);
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
	
	public void clickOn(float clickedX, float clickedY, GraphView GV){
		// When the graph is clicked, we look for the closest Graph Criteria Agent with the click inside its bounds;
		
		double minDist=10000;
		Ingredient closestToClick=null;
		
		for(GraphCriteriaAgent GCA:criterias)
			if(GCA.circleSize!=null){
				double dx=clickedX-GCA.x;
				double dy=clickedY-GCA.y;
				double d=Math.sqrt(dx*dx + dy*dy);
				Log.d("Click", "at distance "+d+" of "+GCA.criteria.getName() + " whose circle size is "+GCA.circleSize);
				if(d<GCA.circleSize && d<minDist){
					minDist=d;
					closestToClick=GCA.criteria;
					Log.d("Click", closestToClick.getName() + " is now the closest");
				}
			}
		
		if(closestToClick!=null)
			GV.descCriteria(closestToClick);
			
		
	}
	
	

}
