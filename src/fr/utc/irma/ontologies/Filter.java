package fr.utc.irma.ontologies;

import java.util.ArrayList;
import java.util.Iterator;

public class Filter extends ArrayList<String>{

    public void addWith(String name) {
        String criteria = "+" + name;
        if(!this.contains(criteria))
            this.add("+" + name);
    }

    public String toSPARQLFragment() {
        Iterator<String> iterator = iterator();
        StringBuffer queryBuffer = new StringBuffer();
        
        while (iterator.hasNext()) {
            String criteria = iterator.next();
            String ingredient = criteria.substring(1);
            if(criteria.startsWith("+"))
                queryBuffer.append("?recipe irma:linked_to irma:" + ingredient + " . ");
        }
        return queryBuffer.toString();
    }

}
