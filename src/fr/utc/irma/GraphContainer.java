package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fr.utc.irma.GraphAgent.Force;
import fr.utc.irma.ontologies.Ingredient;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.Recipe;
import fr.utc.irma.ontologies.RecipesManager;
import android.graphics.Canvas;
import android.util.Log;

public class GraphContainer {
	public ArrayList<GraphCriteriaAgent> criterias = new ArrayList<GraphCriteriaAgent>();
	public ArrayList<GraphRecipeAgent> visibleRecipes = new ArrayList<GraphRecipeAgent>();
	private ArrayList<Recipe> allRecipes = new ArrayList<Recipe>();

	public ArrayList<Ingredient> globalCriterias = new ArrayList<Ingredient>();
	private GraphActivity activity;

	private void loadAllRecipes() {
		try {
			OntologyQueryInterfaceConnector OQIC = new OntologyQueryInterfaceConnector(
					activity.getAssets());
			RecipesManager rcpMng = new RecipesManager(OQIC);
			Iterator<Recipe> allRec = rcpMng.getAll().iterator();
			while (allRec.hasNext())
				allRecipes.add(allRec.next());
		} catch (IOException e) {
			Log.d("RecipeLoader", "Haha, nobody cares");
		}
	}

	public GraphContainer(GraphActivity a) {
		this.activity = a;
		loadAllRecipes();
		for (Recipe r : allRecipes)
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
	}

	public void addRecipe(Recipe r) {
		visibleRecipes.add(new GraphRecipeAgent(r, this));
	}

	public void addCriteria(Ingredient c) {
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

	public void makeCriteriaLocal(Ingredient a) {
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
		for (GraphRecipeAgent c : visibleRecipes)
			c.customDrawBefore(canvas);

		// Draw foreground
		for (GraphCriteriaAgent c : criterias)
			c.draw(canvas);
		for (GraphRecipeAgent c : visibleRecipes) {
			c.draw(canvas);
		}

	}

	public void tick() {
		// TODO : Criteria vs recipes
		for (GraphCriteriaAgent CA : criterias) {
			for (GraphRecipeAgent RA : visibleRecipes) {
				// Attract if there is a match
				if (RA.recipe.matchCriteria(CA.criteria)) {
					Force Fe = CA.elasticForce(RA);
					RA.accelerate(-500 * Fe.Fx, -500 * Fe.Fy);
				}
				// Avoid collision
				Force Fg = CA.gravitationalForce(RA);
				RA.accelerate(-3 * Fg.Fx, -3 * Fg.Fy);
			}
		}

		// Recipe vs recipe
		for (int i = 0; i < visibleRecipes.size() - 1; i++) {
			for (int j = i + 1; j < visibleRecipes.size(); j++) {
				GraphRecipeAgent r1 = visibleRecipes.get(i);
				GraphRecipeAgent r2 = visibleRecipes.get(j);
				Force F = r1.gravitationalForce(r2);
				r1.accelerate(F.Fx, F.Fy);
				r2.accelerate(-F.Fx, -F.Fy);

			}
		}

		// Global Criterias
		for (Ingredient crit : globalCriterias) {
			for (GraphRecipeAgent RA : visibleRecipes) {
			    if (!RA.recipe.matchCriteria(crit)) {
					RA.accelerate((RA.x - 0.5) / 100, (RA.y - 0.5) / 100);

				}
			}
		}

		// Recipes move
		for (GraphRecipeAgent RA : visibleRecipes) {
			RA.tick();
		}

		// CLear out of bound recipes

		for (int i = 0; i < visibleRecipes.size(); i++) {
			GraphRecipeAgent RA = visibleRecipes.get(i);
			if (RA.x < -0.5 || RA.x > 1.5 || RA.y < -0.5 || RA.y > 1.5) {
				visibleRecipes.remove(RA);
			}
		}

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
