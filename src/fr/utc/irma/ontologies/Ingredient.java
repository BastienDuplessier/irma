package fr.utc.irma.ontologies;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hp.hpl.jena.query.QuerySolution;

public class Ingredient implements Serializable {
	
	private static final long serialVersionUID = 6894113962246182007L;
	
	private String id;
	private String name;
	private String url;
	private String imageUrl;
	
	
	public Ingredient(QuerySolution row) {
		this.id = row.get("id").toString();
		this.name = row.get("name").toString();
		this.url = row.get("url").toString();
		this.imageUrl = row.get("imageUrl").toString();
	}
	
	public String toString() {
		return this.name;
	}
	
	public boolean matchAgainstRecipe(Recipe R){
		return R.getName().toLowerCase().indexOf(name.toLowerCase())!=-1;
	}
	
	// Get Methods
	public String getId() { return this.id; }
	public String getName() { return this.name; }
	public String getUrl() { return this.url; }
	public String getImageUrl() { return this.imageUrl; }
	// Other get methods
    public String getIdShort() { return this.id.split("#")[1]; }
	public String getImageName() { 
	    String[] splitString = this.imageUrl.split("\\.");
	    String extension = splitString[splitString.length - 1];
	    return this.getIdShort() + "." + extension;
	}

    public Bitmap loadImageFromUrl() throws IOException {
        URL url = new URL(this.imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap myBitmap = BitmapFactory.decodeStream(input);
        return myBitmap;
    }
 
}
