package fr.utc.irma;

import java.io.IOException;

import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
    	OntologyQueryInterfaceConnector bite;
		try {
			bite = new OntologyQueryInterfaceConnector(getAssets());
	    	bite.executeSparql();
		} catch (IOException e) {
			System.out.println("Haha, nobody cares");
		}
    }
}
