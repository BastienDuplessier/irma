package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.RecipesManager;
import fr.utc.irma.ontologies.Recipe;
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
}
