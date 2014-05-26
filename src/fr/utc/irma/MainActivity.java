package fr.utc.irma;

import java.io.IOException;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import fr.utc.irma.ontologies.Ingredient;
import fr.utc.irma.ontologies.IngredientsManager;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void startGraphActivity(View buttonClicked){
    	Intent i = new Intent(this, GraphActivity.class);
		//i.putExtra("id_oeuvre", clicked.id);
		this.startActivity(i);
    }
    
    // TODO: REMOVE - TESTS
    public void runBullshitQuery(View buttonClicked) {
    	// Collect underpants
    	
    	// ...
    	
    	// Profit    	
		try {
	    	OntologyQueryInterfaceConnector bite = new OntologyQueryInterfaceConnector(getAssets());
			IngredientsManager sac = new IngredientsManager(bite);
			Iterator<Ingredient> souche = sac.getAll().iterator();
			
			// Here comes the pain!!
			while(souche.hasNext())
				System.out.println(souche.next().getName());
		} catch (IOException e) {
			System.out.println("Haha, nobody cares");
		}
    }
}
