package fr.utc.irma.ontologies;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class IngredientsManager {


	private static final String PREFIX = "PREFIX irma: <http://www.w3.org/2014/06/irma#>" ;
	private OntologyQueryInterfaceConnector connector;

	public IngredientsManager(OntologyQueryInterfaceConnector connector) {
		this.connector = connector;
	}

	public ArrayList<Ingredient> getAll() {
		return this.fromSPARQL(PREFIX + " "
				+ "SELECT ?id ?name ?url ?imageUrl WHERE { "
				+ "?id a irma:Ingredient . "
				+ "?id irma:name ?name . "
				+ "?id irma:url ?url . "
				+ "?id irma:image_url ?imageUrl } ");

	}

	// Build Ingredients from SPARQL Query
	public ArrayList<Ingredient> fromSPARQL(String query) {
		ResultSet results = connector.executeSparql(query);
		return this.fromResultSet(results);
	}
	
	// Load all ingredients images async
	public void loadImagesAsync(final Activity activity) {
	    new AsyncTask<Void, Integer, Void>() {
	        ProgressDialog progress = ProgressDialog.show(activity, "Downloading Ingredients Images", "Please wait while images are downloaded");

	        @Override
	        protected void onPreExecute()
	        {
	            progress.setCancelable(false);
	            progress.show();
	        }
	        
	        @Override
	        protected void onPostExecute(Void result) {
	            progress.dismiss();
	        }
	        
	        @Override
	        protected Void doInBackground(Void... params) {

	            ArrayList<Ingredient> all = getAll();
	            Iterator<Ingredient> iterator = all.iterator();

	            while (iterator.hasNext()) {
	                Ingredient ingredient = iterator.next();
	                String filename = ingredient.getImageName();

	                File file = activity.getBaseContext().getFileStreamPath(filename);
	                if(!file.exists()) {
	                    try {
	                        // Download and save image
	                        Bitmap image = ingredient.loadImageFromUrl();
	                        FileOutputStream outputStream = activity.openFileOutput(filename, Context.MODE_PRIVATE);
	                        image.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
	                        outputStream.flush();
	                        outputStream.close();

	                    } catch (Exception e) {
	                        Log.d("IngredientManager - loadImagesAsync", "Fail");
	                    }
	                }
	            }
                return null;
	        }
	    }.execute();
	}

	// Build Ingredients from ResultSet
	private ArrayList<Ingredient> fromResultSet(ResultSet inData) {
		ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
		
		while(inData.hasNext()) {
			QuerySolution row = inData.next();
			ingredients.add(new Ingredient(row));
		}

		return ingredients;
	}
}
