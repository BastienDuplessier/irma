package fr.utc.irma.ontologies;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import fr.utc.irma.MainActivity;

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
	
	// Load all ingredients images
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
	            int maxElements = all.size();
	            
	            while (iterator.hasNext()) {
	                Ingredient ingredient = iterator.next();
	                String filename = ingredient.getImageName();
	                FileOutputStream outputStream;

	                try {
	                    Bitmap image = ingredient.loadImage();
	                    outputStream = activity.openFileOutput(filename, Context.MODE_PRIVATE);
	                    image.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
	                    outputStream.flush();
	                    outputStream.close();

	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                progress.incrementProgressBy(1 / maxElements);
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
