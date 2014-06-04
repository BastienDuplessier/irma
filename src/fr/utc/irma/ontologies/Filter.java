package fr.utc.irma.ontologies;

import java.util.ArrayList;
import java.util.Iterator;

public class Filter extends ArrayList<Criteria>{

    public void addWith(String name) {
        String criteria = "+" + name;
        if(!this.contains(criteria))
            this.add("+" + name);
    }

    public String toSPARQLFragment() {
        Iterator<Criteria> iterator = iterator();
        StringBuffer queryBuffer = new StringBuffer();
        
        while (iterator.hasNext()) {
            queryBuffer.append(iterator.next().toSparql());
        }
        return queryBuffer.toString();
    }

}
