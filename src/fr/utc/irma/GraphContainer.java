package fr.utc.irma;

import java.util.ArrayList;

import fr.utc.irma.GraphAgent.Force;
import fr.utc.irma.ontologies.Ingredient;
import android.graphics.Canvas;

public class GraphContainer {
	public ArrayList<GraphCriteriaAgent> criterias = new ArrayList<GraphCriteriaAgent>();
	public ArrayList<GraphRecipeAgent> recipes = new ArrayList<GraphRecipeAgent>();
	public ArrayList<Ingredient> globals = new ArrayList<Ingredient>();
	
	public GraphContainer() {
		for(int i=0; i<40; i++){
			addRecipe(new Recipe(Math.random()>0.5?"recettePoulet":"recetteChocolat"));
		}
		/*for(int i=0; i<2; i++){
			addCriteria(new Ingredient("ingredient", i==0?"recettePoulet":"recetteChocolat"));
		}*/
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
		recipes.add(new GraphRecipeAgent(r));
	}
	
	public void addCriteria(Ingredient c){
		criterias.add(new GraphCriteriaAgent(c));
		updateCriteriaPosition();
	}
	
	public void makeCriteriaGlobal(GraphCriteriaAgent a){
		globals.add(a.criteria);
		criterias.remove(a);
		updateCriteriaPosition();
	}
	
	public void draw(Canvas canvas){
		// TODO : Prepare background display (compute dists)
		
		
		// TODO :  Draw backgrounds around criterias to show matchnign items
		
		
		// Draw foreground
		for(GraphCriteriaAgent c : criterias)
			c.draw(canvas);
		for(GraphRecipeAgent c : recipes){
			c.draw(canvas);
		}
		
	}
	public void tick(){
		// TODO : Criteria vs recipes
		for(GraphCriteriaAgent CA:criterias){
			for(GraphRecipeAgent RA : recipes){
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
		for(int i=0; i<recipes.size()-1 ; i++){
			for(int j=i+1; j<recipes.size(); j++){
				GraphRecipeAgent r1 = recipes.get(i);
				GraphRecipeAgent r2 = recipes.get(j);
				Force F = r1.gravitationalForce(r2);
				r1.accelerate(F.Fx,F.Fy);
				r2.accelerate(-F.Fx,-F.Fy);
				
			}
		}
		// Recipes move
		for(GraphRecipeAgent RA:recipes){
			RA.tick();
		}
			
		
	}
	
	

}
