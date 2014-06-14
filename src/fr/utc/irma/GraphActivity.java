package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import fr.utc.irma.R.id;
import fr.utc.irma.ontologies.Ingredient;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.Recipe;
import fr.utc.irma.ontologies.RecipesManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	public static class PlaceholderFragment extends Fragment {

		ArrayList<Ingredient> startingCriterias;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_graph,
					container, false);

			TextView textView = (TextView) rootView
					.findViewById(R.id.textToJustifyFrag);

			// ajuster la taille du texte selon la taille d ecran
			textView.setLineSpacing(0.5f, 1.3f);
			textView.setTextSize(7f);

			// preparer final pour fait appel textView dans le corps de fonction
			// post
			final TextView txtViewFinal = textView;

			// par méthode post, récupérer width correctement quand textView est
			// bien créé
			txtViewFinal.post(new Runnable() {
				@Override
				public void run() {
					MiseEnPage.justifyText(txtViewFinal);
				}
			});

			return rootView;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			try {
				startingCriterias = (ArrayList<Ingredient>) getActivity()
						.getIntent().getSerializableExtra("choice");
			} catch (ClassCastException e) {
				Toast.makeText(this.getActivity(),
						"Couldnt pass activity parameters to GraphActivity",
						Toast.LENGTH_SHORT).show();
			}
			for (Ingredient c : startingCriterias) {
				((GraphActivity) this.getActivity()).globalCriterias = ((LinearLayout) getActivity()
						.findViewById(R.id.criterias));
				((GraphActivity) this.getActivity()).gV = ((GraphView) getActivity()
						.findViewById(R.id.graphDisplay));
				((GraphActivity) this.getActivity()).gV.getContainer()
						.addCriteria(c);
			}
			super.onActivityCreated(savedInstanceState);
		}
	}

	public GraphView gV;

	RecipesManager RM;
	LinearLayout sideBar;
	LayoutInflater li;

	private void sideBarInit() {
		if (sideBar == null)
			sideBar = ((LinearLayout) findViewById(R.id.GraphActivityRightSidebar));
		sideBar.removeAllViews();
		li = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);

	}

	public void setSideBarToResultDescription(Recipe result) {
		sideBarInit();
		View descFrag = li.inflate(R.layout.result_description, sideBar);

		descFrag.findViewById(id.resultLink).setTag(result);
		descFrag.findViewById(id.resultLink).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse(((Recipe) v.getTag()).getUrl())));
					}
				});

		((TextView) descFrag.findViewById(R.id.resultName)).setText(result
				.getName());
		UrlImageViewHelper.setUrlDrawable(
				((ImageView) descFrag.findViewById(R.id.resultImage)),
				result.getImageUrl());
		((WebView) descFrag.findViewById(R.id.resultHTMLDescription)).loadData(
				"<b>Hello</b>", "text/html", null);

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
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			makeGlobal.setText("Rendre " + gca.criteria.getName() + " global");
			makeGlobal.setTag(gca);
			makeGlobal.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					GraphActivity.this.gV.getContainer().makeCriteriaGlobal(
							(GraphCriteriaAgent) v.getTag());

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
			try {
				RM = new RecipesManager(new OntologyQueryInterfaceConnector(
						getAssets()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Load recipe list
		RM.asyncLoadAll(new ExecutableTask() {
			@Override
			public void execute(ArrayList<Recipe> recipes) {

				LayoutInflater li = (LayoutInflater) GraphActivity.this
						.getSystemService(GraphActivity.this.LAYOUT_INFLATER_SERVICE);
				// Fill recipe list
				for (Recipe r : recipes) {
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
							setSideBarToResultDescription((Recipe) v.getTag());
						}
					});
				}
			}
		});
	}

	LinearLayout globalCriterias;

	public void addGlobalCriteria(Ingredient ing) {
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
						(Ingredient) v.getTag());
			}
		});
		globalCriterias.addView(removeGlobal);
	}

}
