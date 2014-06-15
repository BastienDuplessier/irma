package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fr.utc.irma.GraphAgent.Force;
import fr.utc.irma.ontologies.Criteria;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.Result;
import fr.utc.irma.ontologies.ResultManager;
import android.graphics.Canvas;
import android.util.Log;

public class GraphContainer {
	public ArrayList<GraphCriteriaAgent> criterias = new ArrayList<GraphCriteriaAgent>();
	public ArrayList<GraphResultAgent> visibleRecipes = new ArrayList<GraphResultAgent>();
	private ArrayList<Result> allRecipes = new ArrayList<Result>();

	public ArrayList<Criteria> globalCriterias = new ArrayList<Criteria>();
	private GraphActivity activity;

	private void loadAllRecipes() {
		makeSureThereAreEnoughRecipeNodes();
	}

	public GraphContainer(GraphActivity a) {
		this.activity = a;
		loadAllRecipes();
		for (Result r : allRecipes)
			addRecipe(r);

	}

	public void updateCriteriaPosition() {
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
		awaitingLoading=0;
	}
	
	// 2 modes : load precise stuffs, or just global criterias
	
	
	
	int awaitingLoading=0;
	private void makeSureThereAreEnoughRecipeNodes(){
		if(awaitingLoading==0 && visibleRecipes.size()<30){
			awaitingLoading=30-visibleRecipes.size();
			ArrayList<Criteria> heyThatWouldBeNice = new ArrayList<Criteria>();
			for(GraphCriteriaAgent GCA : criterias)
				heyThatWouldBeNice.add(GCA.criteria);
			
			activity.getRM().asyncLoadWithCriterias(new ExecutableTask() {
				
				@Override
				public void execute(ArrayList<Result> results) {
					int maxLoad=awaitingLoading;
					for(Result r : results){
						if(maxLoad-->0)
							GraphContainer.this.addRecipe(r);
					}
					awaitingLoading=0;
				}
			}, this.globalCriterias, heyThatWouldBeNice);
		}
	}

	public void addRecipe(Result r) {
		visibleRecipes.add(new GraphResultAgent(r, this));
	}

	public void addCriteria(Criteria c) {
		criterias.add(new GraphCriteriaAgent(c, this));

		if (criterias.size() > 3)
			makeCriteriaGlobal(criterias.get(0));

		updateCriteriaPosition();
	}

	public void makeCriteriaGlobal(GraphCriteriaAgent a) {
		globalCriterias.add(a.criteria);
		activity.addGlobalCriteria(a.criteria);
		removeCriteria(a);
	}

	public void makeCriteriaLocal(Criteria a) {
		globalCriterias.remove(a);
		addCriteria(a);
		updateCriteriaPosition();
	}

	public void removeCriteria(GraphCriteriaAgent gca) {
		this.criterias.remove(gca);
		this.updateCriteriaPosition();
		activity.setSideBarToCriteriaDescription(new ArrayList<GraphCriteriaAgent>());
	}

	public void draw(Canvas canvas) {

		// Draw backgrounds
		for (GraphCriteriaAgent c : criterias)
			c.customDrawBefore(canvas);
		for (GraphResultAgent c : visibleRecipes)
			c.customDrawBefore(canvas);

		// Draw foreground
		for (GraphCriteriaAgent c : criterias)
			c.draw(canvas);
		for (GraphResultAgent c : visibleRecipes) {
			c.draw(canvas);
		}

	}

	public void tick() {
		// TODO : Criteria vs recipes
		for (GraphCriteriaAgent CA : criterias) {
			for (GraphResultAgent RA : visibleRecipes) {
				// Attract if there is a match
				Force Fe = CA.elasticForce(RA);
				Force Fg = CA.gravitationalForce(RA);
				
				if (RA.result.matchCriteria(CA.criteria)) {
					RA.accelerate(-700 * Fe.Fx, -700 * Fe.Fy);
				}else{
					RA.accelerate(-10 * Fg.Fx, -10 * Fg.Fy);
				}
				// Avoid collision
				RA.accelerate(-1 * Fg.Fx, -1 * Fg.Fy);
			}
		}

		// Recipe vs recipe
		for (int i = 0; i < visibleRecipes.size() - 1; i++) {
			for (int j = i + 1; j < visibleRecipes.size(); j++) {
				GraphResultAgent r1 = visibleRecipes.get(i);
				GraphResultAgent r2 = visibleRecipes.get(j);
				Force F = r1.gravitationalForce(r2);
				r1.accelerate(F.Fx, F.Fy);
				r2.accelerate(-F.Fx, -F.Fy);

			}
		}

		// Global Criterias
		for (Criteria crit : globalCriterias) {
			for (GraphResultAgent RA : visibleRecipes) {
			    if (!RA.result.matchCriteria(crit)) {
					RA.accelerate((RA.x - 0.5) / 100, (RA.y - 0.5) / 100);

				}
			}
		}

		// Recipes move
		for (GraphResultAgent RA : visibleRecipes) {
			RA.tick();
		}

		// CLear out of bound recipes
		for (int i = 0; i < visibleRecipes.size(); i++) {
			GraphResultAgent RA = visibleRecipes.get(i);
			if (RA.x < -0.5 || RA.x > 1.5 || RA.y < -0.5 || RA.y > 1.5) {
				visibleRecipes.remove(RA);
			}
		}
		
		//Check if we need new recipes
		makeSureThereAreEnoughRecipeNodes();

	}

	public ArrayList<GraphCriteriaAgent> getAllClicked(float clickedX,
			float clickedY) {
		ArrayList<GraphCriteriaAgent> allClicked = new ArrayList<GraphCriteriaAgent>();
		for (GraphCriteriaAgent GCA : criterias) {
			double dx = clickedX - GCA.x;
			double dy = clickedY - GCA.y;
			double d = Math.sqrt(dx * dx + dy * dy);
			if ((GCA.circleSize != null && d < GCA.circleSize)
					|| d < GCA.displayRadius) {
				allClicked.add(GCA);
			}
		}
		return allClicked;
	}

	public void clickOn(float clickedX, float clickedY, GraphView GV) {
		// When the graph is clicked, we look for Graph Criteria Agents with the
		// click inside there bounds;

		GV.descCriteria(getAllClicked(clickedX, clickedY));

	}

}
