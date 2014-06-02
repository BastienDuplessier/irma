package fr.utc.irma;

import java.io.IOException;
import java.util.Iterator;

import fr.utc.irma.ontologies.Filter;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.Recipe;
import fr.utc.irma.ontologies.RecipesManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //justify texte
        TextView textView = (TextView)findViewById(R.id.textToJustifyMain);
        Display display = this.getWindowManager().getDefaultDisplay();            
		DisplayMetrics dm = new DisplayMetrics();            
		display.getMetrics(dm);            
		double widthTier = dm.widthPixels/2.8;
		//ajuster la taille du texte selon la taille d ecran
		textView.setLineSpacing(0.5f, 1.3f);
		textView.setTextSize(8*(float)widthTier/320f);
        MiseEnPage.justifyText(textView);
    }
    
    public void startGraphActivity(View buttonClicked){
    	Intent i = new Intent(this, GraphActivity.class);
		//i.putExtra("id_oeuvre", clicked.id);
		this.startActivity(i);
    }
    
    public void loadRecipesStuff(View buttonClicked) {
        // LALALALA
        try {
            System.out.println("Ma bite");
            Filter filter = new Filter();
            filter.addWith("chorizo");

            OntologyQueryInterfaceConnector connector = new OntologyQueryInterfaceConnector(getAssets());
            RecipesManager manager = new RecipesManager(connector);
            Iterator<Recipe> iterator = manager.getFiltered(filter).iterator();
            System.out.println(manager.getFiltered(filter).size());
            while(iterator.hasNext()) {
                //System.out.println(iterator.next());
                iterator.next();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
