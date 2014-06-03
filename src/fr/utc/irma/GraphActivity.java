package fr.utc.irma;

import java.util.ArrayList;

import javax.xml.datatype.Duration;

import fr.utc.irma.ontologies.Ingredient;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

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
		
		// Extras
		//Toast.makeText(this, getIntent().getExtras().getStringArray("choice") [0], Toast.LENGTH_SHORT).show();;
		
		
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
			//justify texte
	        MiseEnPage.justifyText(textView);
	        
	        
	        
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			startingCriterias = (ArrayList<Ingredient>)getActivity().getIntent().getSerializableExtra("choice");
	        
			for(Ingredient c:startingCriterias){
		        /*Button tmp = new Button(getActivity());
		        tmp.setText(c);*/
				Log.d("extrasStartIng", c.getName());
				Toast.makeText(getActivity(), c.getName(), Toast.LENGTH_SHORT).show();
		        ((GraphView)getActivity().findViewById(R.id.graphDisplay)).getContainer().addCriteria(c);
		        //((LinearLayout)container.findViewById(R.id.criterias)).addView(tmp);
			}
			super.onActivityCreated(savedInstanceState);
		}
	}

}
