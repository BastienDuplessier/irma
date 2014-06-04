package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import fr.utc.irma.R.drawable;
import fr.utc.irma.ontologies.Ingredient;
import fr.utc.irma.ontologies.IngredientsManager;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	String searchFilter="";
	
	// Tous les ingredients
	ArrayList<Ingredient> all = new ArrayList<Ingredient>();

	// Ingredients choisis par l'user
	ArrayList<Ingredient> chosen = new ArrayList<Ingredient>();
		
	// Liste (affichage) des ingredients
	RelativeLayout ingList ;
	
	
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
        refreshList();
    }
    
    private void loadList(){
    	try {
    	    OntologyQueryInterfaceConnector OQIC = new OntologyQueryInterfaceConnector(getAssets());
    	    IngredientsManager ingMng = new IngredientsManager(OQIC);
    	    Iterator<Ingredient> allIng = ingMng.getAll().iterator();
    	    while(allIng.hasNext())
        	    all.add(allIng.next());
    	    
    	    ingList = (RelativeLayout)findViewById(R.id.ingList);
    	    /*ingList.(new OnLayoutChangeListener() {
				
				@Override
				public void onLayoutChange(View v, int left, int top, int right,
						int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
					//MainActivity.this.refreshList();
					
				}
			});*/
    	    refreshList();
    	    
    	    
    	    setupFiltering();
    	} catch (IOException e) {
    	    Log.d("IngredientsLoader","Haha, nobody cares");
    	}
    	//((DynamicTableView)findViewById(R.id.ingList)).updateDisplay();
    }
    
    public void setSearchFilter(String sf){
    	searchFilter=sf.toUpperCase(Locale.FRANCE).trim();
    }
    
    public void refreshList() {
    	Button go=((Button)findViewById(R.id.startGraphButton));
    	if(chosen.size()==0){
    		go.setText("GO");
    		go.setClickable(false);
    		go.setEnabled(false);
    	}else{
    		go.setText( TextUtils.join(" + ", chosen));
    		go.setClickable(true);
    		go.setEnabled(true);
    	}
    	
    	ingList.removeAllViews();
	    for(Ingredient toBeShown:all)
	    	if(searchFilter==""|| toBeShown.getName().toUpperCase(Locale.FRANCE).indexOf(searchFilter)!=-1 
	    	|| chosen.contains(toBeShown))
	    		addIng(toBeShown);
	}
    
    EditText filtering;
    private void setupFiltering(){
    	filtering=(EditText)findViewById(R.id.criterias);
    	filtering.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				MainActivity.this.setSearchFilter(MainActivity.this.filtering.getText().toString());
				refreshList();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
    }
    
    @SuppressWarnings("deprecation")
	private void addIng(Ingredient ing){
    	
    	ImageView ingButton = new ImageView(this);
    	// grid 
    	int fullWidth=ingList.getWidth();
    	if(fullWidth==0)
    		fullWidth=800;
    	int numberOfCells=ingList.getChildCount();
    	int numberOfCollums=4;
    	int imgWidth=fullWidth/4;
    	
    	
    	RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(imgWidth, imgWidth);
    	lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
    	lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
    	lp.leftMargin=(numberOfCells%numberOfCollums)*imgWidth;
    	lp.topMargin=((int)numberOfCells/numberOfCollums)*imgWidth;
    	
    	ingButton.setLayoutParams(lp);
    	UrlImageViewHelper.setUrlDrawable(ingButton, ing.getImageUrl(), drawable.courgette);
    	ingList.addView(ingButton);
    	
    	ingButton.setTag(ing);
    	if(!chosen.contains(ing))
    		ingButton.setAlpha(150);
    	ingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				MainActivity.this.pickCriteria((Ingredient)v.getTag());
			}
		});
    	
    }
    
}
