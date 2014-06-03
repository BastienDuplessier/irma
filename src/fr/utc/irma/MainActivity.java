package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import fr.utc.irma.R.drawable;
import fr.utc.irma.ontologies.Ingredient;
import fr.utc.irma.ontologies.IngredientsManager;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ArrayList<Ingredient> chosen = new ArrayList<Ingredient>();
	
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
        loadList();
        ((Button)findViewById(R.id.startGraphButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				MainActivity.this.startGraphActivity();
				
			}
		});
    }
    
    public void startGraphActivity(){
    	Intent i = new Intent(this, GraphActivity.class);
    	ArrayList<String> chosenStr=new ArrayList<String>();
    	for(Ingredient ing : chosen)
    		chosenStr.add(ing.getUrl());
    	i.putExtra("choice", chosen);
		this.startActivity(i);
    }
    
    public void pickCriteria(Ingredient toAdd){
    	if(chosen.contains(toAdd))
    		chosen.remove(toAdd);
    	else
    		chosen.add(toAdd);
    	String l="";
    	for(Ingredient i:chosen)
    		l+="\n"+i.getName();
        ((TextView)findViewById(R.id.selectedCriterias)).setText(l);;
    }
    
    private void loadList(){
    	try {
    	    OntologyQueryInterfaceConnector OQIC = new OntologyQueryInterfaceConnector(getAssets());
    	    IngredientsManager ingMng = new IngredientsManager(OQIC);
    	    Iterator<Ingredient> allIng = ingMng.getAll().iterator();

    	    while(allIng.hasNext())
    	    	addIng(allIng.next());
    	} catch (IOException e) {
    	    Log.d("IngredientsLoader","Haha, nobody cares");
    	}
    	//((DynamicTableView)findViewById(R.id.ingList)).updateDisplay();
    }
    
    private void addIng(Ingredient ing){
    	LinearLayout ingList = (LinearLayout)findViewById(R.id.ingList);
    	ImageView ingButton = new ImageView(this);
    	ingButton.setLayoutParams(new LayoutParams(200, 200));
    	UrlImageViewHelper.setUrlDrawable(ingButton, ing.getImageUrl(), drawable.courgette);
    	ingList.addView(ingButton);
    	
    	ingButton.setTag(ing);
    	ingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				MainActivity.this.pickCriteria((Ingredient)v.getTag());
			}
		});
    }
    
}
