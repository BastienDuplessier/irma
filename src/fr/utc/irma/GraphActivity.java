package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import fr.utc.irma.R.drawable;
import fr.utc.irma.ontologies.Ingredient;
import fr.utc.irma.ontologies.IngredientsManager;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.Recipe;
import fr.utc.irma.ontologies.RecipesManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.graph, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		ArrayList<Ingredient> startingCriterias;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_graph,
					container, false);
			
			TextView textView = (TextView)rootView.findViewById(R.id.textToJustifyFrag);
			
	    	//ajuster la taille du texte selon la taille d ecran
	        textView.setLineSpacing(0.5f, 1.3f);
	        textView.setTextSize(7f);
	        
			//preparer final pour fait appel textView dans le corps de fonction post
	        final TextView txtViewFinal = textView;
	        
	        //par méthode post, récupérer width correctement quand textView est bien créé
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
			try{
				startingCriterias = (ArrayList<Ingredient>)getActivity().getIntent().getSerializableExtra("choice");
			}catch(ClassCastException e){
				Toast.makeText(this.getActivity(), "Couldnt pass activity parameters to GraphActivity",Toast.LENGTH_SHORT).show();
			}
			for(Ingredient c:startingCriterias){
		        ((GraphView)getActivity().findViewById(R.id.graphDisplay)).getContainer().addCriteria(c);
			}
			super.onActivityCreated(savedInstanceState);
		}
	}
	
	RecipesManager RM ;
	LinearLayout sideBar ; 
	
	public void setSideBarToCriteriaDescription(Ingredient crit){
		
		if(sideBar ==null )
			sideBar=((LinearLayout)findViewById(R.id.GraphActivityRightSidebar));
		
		sideBar.removeAllViews();
		
		// Load ingredient descritor
		LayoutInflater li =(LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE); 
		View descFrag = li.inflate(
				R.layout.criteria_view,
				sideBar
				);
		// Fill ingredient descriptor
		((TextView)descFrag.findViewById(R.id.ingDescNameField)).setText(crit.getName());
		UrlImageViewHelper.setUrlDrawable(((ImageView)descFrag.findViewById(R.id.ingDescImg)), crit.getImageUrl(), drawable.courgette);
		if(RM==null){
			try {
				RM = new RecipesManager(new OntologyQueryInterfaceConnector(getAssets()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Load recipe list
		RM.asyncLoadAll(new ExecutableTask() {
			@Override
			public void execute(ArrayList<Recipe> recipes) {
				
				LayoutInflater li =(LayoutInflater) GraphActivity.this.getSystemService(
						GraphActivity.this.LAYOUT_INFLATER_SERVICE);
				// Fill recipe list
				for(Recipe r : recipes){ 
					View descFrag = li.inflate(
							R.layout.reciepe_quick_desc,
							sideBar
							);
					((TextView)descFrag.findViewById(R.id.recipeListText)).setText(r.getName());
					UrlImageViewHelper.setUrlDrawable((ImageView)descFrag.findViewById(R.id.recipeListImage),r.getImageUrl());
					
				}
			}
		});
		
		
		
	}

}
