package fr.utc.irma;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button nextButton;
        
        nextButton = (Button) findViewById(R.id.beginSearchButton);
        
        nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent myIntent = new Intent(MainActivity.this, GraphActivity.class);
				MainActivity.this.startActivity(myIntent);
				 
			}
		});
    }
    
    public void startGraphActivity(View buttonClicked){
    	Intent i = new Intent(this, GraphActivity.class);
		//i.putExtra("id_oeuvre", clicked.id);
		this.startActivity(i);
    }
}
