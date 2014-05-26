package fr.utc.irma;

import java.util.ArrayList;

import fr.utc.irma.GraphAgent.Force;
import android.graphics.Canvas;

public class GraphContainer {
	public ArrayList<GraphCriteriaAgent> criterias = new ArrayList<GraphCriteriaAgent>();
	public ArrayList<GraphRecipeAgent> recipes = new ArrayList<GraphRecipeAgent>();
	public ArrayList<Criteria> globals = new ArrayList<Criteria>();
	
	public GraphContainer() {
		for(int i=0; i<40; i++){
			addRecipe(new Recipe());
		}
		for(int i=0; i<3; i++){
			addCriteria(new Criteria(null, null));
		}
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
	
	public void addCriteria(Criteria c){
		criterias.add(new GraphCriteriaAgent(c));
	}
	
	public void makeCriteriaGlobal(GraphCriteriaAgent a){
		globals.add(a.criteria);
		criterias.remove(a);
	}
	
	public void draw(Canvas canvas){
		for(GraphCriteriaAgent c : criterias)
			c.draw(canvas);
		for(GraphRecipeAgent c : recipes){
			c.draw(canvas);
		}
		// TODO : Draw global criterias
		
	}
	public void tick(){
		// TODO : Criteria vs recipes
		for(GraphCriteriaAgent CA:criterias){
			for(GraphRecipeAgent RA : recipes){
				Force Fe = CA.elasticForce(RA, 0.01);
				RA.accelerate(-500*Fe.Fx,-500*Fe.Fy);
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
