package fr.utc.irma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import com.hp.hpl.jena.query.ResultSet;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import fr.utc.irma.R.drawable;
import fr.utc.irma.ontologies.Criteria;
import fr.utc.irma.ontologies.CriteriasManager;
import fr.utc.irma.ontologies.OntologyQueryInterfaceConnector;
import fr.utc.irma.ontologies.ResultManager;
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
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	String searchFilter="";
	
	// Tous les ingredients
	ArrayList<Criteria> all = new ArrayList<Criteria>();

	// Ingredients choisis par l'user
	ArrayList<Criteria> chosen = new ArrayList<Criteria>();
		
	// Liste (affichage) des ingredients
	RelativeLayout ingList ;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        
        loadList();
        ((Button)findViewById(R.id.startGraphButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				HomeActivity.this.startGraphActivity();
				
			}
		});
        // Adjust table when layout ready
        ingList.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				HomeActivity.this.ingList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				HomeActivity.this.refreshList();
				
			}
		});
        
        
    }
    
    public void startGraphActivity(){
    	Intent i = new Intent(this, GraphActivity.class);
    	ArrayList<String> chosenStr=new ArrayList<String>();
    	for(Criteria ing : chosen)
    		chosenStr.add(ing.getUrl());
    	i.putExtra("choice", chosen);
		this.startActivity(i);
    }
    
    public void pickCriteria(Criteria toAdd){
    	if(chosen.contains(toAdd))
    		chosen.remove(toAdd);
    	else
    		chosen.add(toAdd);
        refreshList();
    }
    
    private void loadList(){
	    all = CriteriasManager.getCM(getAssets()).getAll();
	    ingList = (RelativeLayout)findViewById(R.id.ingList);
	    refreshList();
	    setupFiltering();
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
    		ArrayList<String> chosenStrs=new ArrayList<String>();
    		for(Criteria c:chosen)
    			chosenStrs.add(c.getName());
    		
    		go.setText( TextUtils.join(" + ", chosenStrs));
    		go.setClickable(true);
    		go.setEnabled(true);
    	}
    	
    	ingList.removeAllViews();
    	
    	int max=30; 
	    for(Criteria toBeShown:all)
	    	if(searchFilter==""|| toBeShown.getName().toUpperCase(Locale.FRANCE).indexOf(searchFilter)!=-1 
		    	|| chosen.contains(toBeShown))
		    		if(max-->0)
		    			addIng(toBeShown);
	}
    
    EditText filtering;
    private void setupFiltering(){
    	filtering=(EditText)findViewById(R.id.criterias);
    	filtering.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				HomeActivity.this.setSearchFilter(HomeActivity.this.filtering.getText().toString());
				refreshList();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable arg0) {}
		});
    }
    
    @SuppressWarnings("deprecation")
	private void addIng(Criteria ing){
    	try{
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
	    	lp.topMargin=((int)numberOfCells/numberOfCollums)*imgWidth*142/215;
	    	
	    	ingButton.setLayoutParams(lp);
    	
    		UrlImageViewHelper.setUrlDrawable(ingButton, ing.getImageUrl());
    		ingList.addView(ingButton);
    		ingButton.setTag(ing);
        	if(!chosen.contains(ing))
        		ingButton.setAlpha(150);
        	ingButton.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				HomeActivity.this.pickCriteria((Criteria)v.getTag());
    			}
    		});	
        	
    	}catch (Exception e){
    		// Ignored, happens sometime when too many picts are loaded at a time
    	}
    	
    	
    }
    
}
