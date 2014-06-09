package fr.utc.irma;

import java.util.ArrayList;
import android.graphics.Paint;
import android.text.TextUtils;
import android.widget.TextView;

public class MiseEnPage {

	public static void justifyText(TextView textView)
	{
		int contentWidth = textView.getWidth();
		String text=textView.getText().toString();
        String tempText;
        String resultText = "";
        Paint paint=textView.getPaint();

        ArrayList<String> paraList = new ArrayList<String>();        
        paraList = paraBreak(text);        
        for(int i = 0; i<paraList.size(); i++) {  
        	ArrayList<String> lineList=lineBreak(paraList.get(i).trim(),paint,contentWidth);  
            tempText = TextUtils.join("\r", lineList).replaceFirst("\\s*", "");  
       	    resultText += tempText.replaceFirst("\\s*", "");      
        }                
        
        textView.setText(resultText);
	}
	
	 //separer les paragraphes
    public static ArrayList<String> paraBreak(String text) {
        ArrayList<String> paraList = new ArrayList<String>();
        String[] paraArray = text.split("\\n+");
           for(String para:paraArray) {
               paraList.add(para);
        }
        return paraList;
    }
    
  //separer les lignes
    private static ArrayList<String> lineBreak(String text, Paint paint, float contentWidth){
        String [] wordArray=text.split("\\s"); 
        ArrayList<String> lineList = new ArrayList<String>();
        String myText="";

        for(String word:wordArray){
            if(paint.measureText(myText+" "+word)<=contentWidth)
                myText=myText+" "+word;
            else{
                int totalSpacesToInsert=(int)((contentWidth-paint.measureText(myText))/paint.measureText(" "));
                lineList.add(justifyLine(myText,totalSpacesToInsert));
                myText=word;
            }
        }
        
        lineList.add(myText);
        return lineList;
    }
    
  //inserer les espaces
    private static String justifyLine(String text,int totalSpacesToInsert){
        String[] wordArray=text.split("\\s");
        String toAppend="";

        while(totalSpacesToInsert >= (wordArray.length-1) && wordArray.length-1 > 0){
            toAppend=toAppend+" ";
            totalSpacesToInsert=totalSpacesToInsert-(wordArray.length-1);
        }
        int i=0;
        String justifiedText="";
        for(String word:wordArray){
            if(i<totalSpacesToInsert)
                justifiedText=justifiedText+" "+toAppend+word;

            else                
                justifiedText=justifiedText+toAppend+word;

            i++;
        }

        return justifiedText;
    }
}
