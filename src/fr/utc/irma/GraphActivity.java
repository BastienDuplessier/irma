package fr.utc.irma;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import fr.utc.irma.R.id;
import fr.utc.irma.ontologies.Criteria;
import fr.utc.irma.ontologies.CriteriasManager;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.Result;
import fr.utc.irma.ontologies.ResultManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GraphActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.activity_graph);

		if(CM==null){
			initKBConnection();
		}
		
		try {
			((GraphActivity) this).globalCriterias = (LinearLayout)  findViewById(R.id.criterias);
			((GraphActivity) this).gV = ( GraphView) findViewById(R.id.graphDisplay);
			
			ArrayList<Criteria> startingCriterias = (ArrayList<Criteria>) this.getIntent().getSerializableExtra("choice");
			for (Criteria c : startingCriterias) {
				((GraphActivity) this).gV.getContainer().addCriteria(c);
			}
		} catch (ClassCastException e) {
			Log.e("intent decode param", "Failed");
		}
		
		

	}

	public GraphView gV;

	ResultManager RM;
	CriteriasManager CM;
	OntologyQueryInterfaceConnector OQIC;
	
	private void initKBConnection(){
		try {
			OQIC = new OntologyQueryInterfaceConnector(getAssets());
			RM = new ResultManager(OQIC);
			CM = new CriteriasManager(OQIC);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ResultManager getRM(){
		if(RM==null)
			initKBConnection();
		return RM;
	}
	
	public CriteriasManager getCM(){
		if(CM==null)
			initKBConnection();
		return CM;
	}
	
	LinearLayout sideBar;
	LayoutInflater li;

	private void sideBarInit() {
		if (sideBar == null)
			sideBar = ((LinearLayout) findViewById(R.id.GraphActivityRightSidebar));
		sideBar.removeAllViews();
		li = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);

	}

	public void setSideBarToResultDescription(Result result) {
		sideBarInit();
		View descFrag = li.inflate(R.layout.result_description, sideBar);

		descFrag.findViewById(id.resultLink).setTag(result);
		descFrag.findViewById(id.resultLink).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse(((Result) v.getTag()).getUrl())));
					}
				});

		((TextView) descFrag.findViewById(R.id.resultName)).setText(result
				.getName());
		
		if(result.getImageUrl().length()>0)
			UrlImageViewHelper.setUrlDrawable(
				((ImageView) descFrag.findViewById(R.id.resultImage)),
				result.getImageUrl());
		
		if(result.getDescription().length()>0)
			((WebView) descFrag.findViewById(R.id.resultHTMLDescription)).loadData(
				result.getDescription(), "text/html", null);
		
		// Criterias buttons
		for(String critID:result.getCriterias()){
			Criteria fullCritDesc=getCM().getCriteriaFromId(critID);
			if(fullCritDesc!=null){
				LinearLayout addC = new LinearLayout(this);
				addC.setOrientation(LinearLayout.HORIZONTAL);
				addC.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
							
				ImageView Cimage=new ImageView(this);
				Cimage.setLayoutParams(new LayoutParams(50, 50));
				UrlImageViewHelper.setUrlDrawable(Cimage, fullCritDesc.getImageUrl());
				addC.addView(Cimage);
				
				TextView Cname = new TextView(this);
				Cname.setText(fullCritDesc.getName());
				addC.addView(Cname);
				
				addC.setTag(fullCritDesc);
				addC.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						GraphActivity.this.gV.container.addCriteria((Criteria)arg0.getTag());
					}
				});
				
				sideBar.addView(addC);
			}else{
				TextView bug = new TextView(this);
				bug.setText(critID);
				sideBar.addView(bug);
			}
		}
		
	}

	public void setSideBarToCriteriaDescription(
			ArrayList<GraphCriteriaAgent> clickedCriterias) {
		sideBarInit();

		// Load ingredient descritor
		View descFrag = li.inflate(R.layout.criteria_view, sideBar);

		// Fill ingredient descriptor
		String names = "";
		for (GraphCriteriaAgent gca : clickedCriterias)
			if (names.equals(""))
				names += gca.criteria.getName();
			else
				names += " & " + gca.criteria.getName();
		((TextView) descFrag.findViewById(R.id.ingDescNameField))
				.setText(names);

		if (clickedCriterias.size() == 1)
			UrlImageViewHelper.setUrlDrawable(
					((ImageView) descFrag.findViewById(R.id.ingDescImg)),
					clickedCriterias.get(0).criteria.getImageUrl());

		// "Make global" buttons
		for (GraphCriteriaAgent gca : clickedCriterias) {
			Button makeGlobal = new Button(this);
			makeGlobal.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			makeGlobal.setText("Rendre " + gca.criteria.getName() + " global");
			makeGlobal.setTag(gca);
			makeGlobal.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					GraphActivity.this.gV.getContainer().makeCriteriaGlobal(
							(GraphCriteriaAgent) v.getTag());
					Toast.makeText(GraphActivity.this, "Vous pouvez rendre un critere global en le double clickant", Toast.LENGTH_LONG).show();

				}
			});
			sideBar.addView(makeGlobal);
		}

		// "Delete" buttons
		for (GraphCriteriaAgent gca : clickedCriterias) {
			Button deleteGCA = new Button(this);
			deleteGCA.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			deleteGCA.setText("Supprimer " + gca.criteria.getName());
			deleteGCA.setTag(gca);
			deleteGCA.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GraphActivity.this.gV.getContainer().removeCriteria(
							(GraphCriteriaAgent) v.getTag());
				}
			});
			sideBar.addView(deleteGCA);
		}

		// Load corresponding recipes in sidebar
		if (RM == null) {
			initKBConnection();
		}
		
		
		// Load recipe list
		RM.asyncLoadWithCriterias(new ExecutableTask() {
			@Override
			public void execute(ArrayList<Result> results) {

				LayoutInflater li = (LayoutInflater) GraphActivity.this
						.getSystemService(GraphActivity.this.LAYOUT_INFLATER_SERVICE);
				// Fill recipe list
				for (Result r : results) {
					View descFrag = li.inflate(R.layout.result_list_item,
							sideBar);

					((TextView) descFrag.findViewById(R.id.recipeListText))
							.setText(r.getName());
					UrlImageViewHelper.setUrlDrawable((ImageView) descFrag
							.findViewById(R.id.recipeListImage), r
							.getImageUrl());
					descFrag.setTag(r);

					descFrag.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							setSideBarToResultDescription((Result) v.getTag());
						}
					});
				}
			}
		}, (ArrayList<Criteria>)gV.container.globalCriterias, clickedCriterias);
	}

	LinearLayout globalCriterias;

	public void addGlobalCriteria(Criteria ing) {
		Button removeGlobal = new Button(this);
		removeGlobal.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		removeGlobal.setText(ing.getName());
		removeGlobal.setTag(ing);
		removeGlobal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				globalCriterias.removeView(v);
				GraphActivity.this.gV.getContainer().makeCriteriaLocal(
						(Criteria) v.getTag());
			}
		});
		globalCriterias.addView(removeGlobal);
	}

}
