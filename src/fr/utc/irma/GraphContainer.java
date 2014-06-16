package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import fr.utc.irma.GraphAgent.Force;
import fr.utc.irma.ontologies.Criteria;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.Result;
import fr.utc.irma.ontologies.ResultManager;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.TextView;

public class GraphContainer {
	public ArrayList<GraphCriteriaAgent> criterias = new ArrayList<GraphCriteriaAgent>();
	public ArrayList<GraphResultAgent> visibleRecipes = new ArrayList<GraphResultAgent>();
	private ArrayList<Result> allRecipes = new ArrayList<Result>();

	public ArrayList<Criteria> globalCriterias = new ArrayList<Criteria>();
	private GraphActivity activity;


	public GraphContainer(GraphActivity a) {
		this.activity = a;
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
	
	
	
	// 2 modes : load precise stuffs, or just global criterias
	
	/*
	
	public int isLoading = 0;
	public int notTooOften=0;
	private void makeSureThereAreEnoughRecipeNodes(){
			if(visibleRecipes.size()<40){
				ArrayList<Criteria> heyThatWouldBeNice = new ArrayList<Criteria>();
				for(GraphCriteriaAgent GCA : criterias){
					ArrayList<Criteria> justThisOne=new ArrayList<Criteria>();
					justThisOne.add(GCA.criteria);
					getMoreOfThis(justThisOne);
					heyThatWouldBeNice.add(GCA.criteria);
				}
				getMoreOfThis(heyThatWouldBeNice);
			}
		}
	}
	
	public void getMoreOfThis(ArrayList<Criteria>  heyThatWouldBeNice){
		
			isLoading++;
			ArrayList<String> noneed=new ArrayList<String>();
			for(GraphResultAgent gra : visibleRecipes)
				noneed.add(gra.result.getId());
			
			activity.getRM().asyncLoadWithCriterias(new ExecutableTask() {
				
				@Override
				public void execute(ArrayList<Result> results) {
					if(GraphContainer.this.isLoading>0)
						GraphContainer.this.isLoading--;
					
					int tenMax=10;
					for(Result r : results)
						if(tenMax-->0)
							GraphContainer.this.addRecipe(r);
				}
			}, this.globalCriterias, heyThatWouldBeNice, noneed.toArray());
		
	}
	*/

	public void addRecipe(Result r) {
		boolean notthere=true;
		for(GraphResultAgent gra:visibleRecipes)
			if(gra.result.getId().equals(r.getId()))
				notthere=false;
		if(visibleRecipes.size()<40 && notthere){
			visibleRecipes.add(new GraphResultAgent(r, this));
		}
	}
	public void killResult(GraphResultAgent r){
		visibleRecipes.remove(r);
	}
	
	public void addCriteria(Criteria c) {
		GraphCriteriaAgent ngca=new GraphCriteriaAgent(c, this);
		criterias.add(ngca);

		if (criterias.size() > 3)
			makeCriteriaGlobal(criterias.get(0));

		updateCriteriaPosition();
		
		ArrayList<GraphCriteriaAgent> justThisOne=new ArrayList<GraphCriteriaAgent>();
		justThisOne.add(ngca);
		this.activity.gV.descCriteria(justThisOne);
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
		
		for(GraphCriteriaAgent GCA:criterias)
			if(GCA.needMoreRecipes(visibleRecipes))
				GCA.askForMoreResults(this, activity.getRM());
		
		for (GraphCriteriaAgent GCA : criterias) 
			GCA.attractOrRevulseResults(this.visibleRecipes);
		
		for (int i = 0; i < visibleRecipes.size() - 1; i++) 
			for (int j = i + 1; j < visibleRecipes.size(); j++) 
				visibleRecipes.get(j).avoidCollistionWithResult(visibleRecipes.get(i));

		// Global Criterias
		for (Criteria crit : globalCriterias) 
			for (GraphResultAgent RA : visibleRecipes) 
			    if (!RA.result.matchCriteria(crit)) 
					RA.accelerate((RA.x - 0.5) / 100, (RA.y - 0.5) / 100);

		// Recipes move
		for (GraphResultAgent RA : visibleRecipes) 
			RA.tick();
		
		ArrayList<GraphResultAgent> toKill= new ArrayList<GraphResultAgent>();
		// CLear out of bound recipes
		for (GraphResultAgent GRA : visibleRecipes) 
			if(GRA.isOutOfBounds())
				toKill.add(GRA);
		
		for(GraphResultAgent GRA:toKill)
			killResult(GRA);
			
		
		//Check if we need new recipes
		//makeSureThereAreEnoughRecipeNodes();

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
		GV.descCriteria(getAllClicked(clickedX, clickedY));
	}

}
