/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ressources.definitions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Benoît
 */
public class Definition {
    public String id;
    public String type;

    boolean upToDate = true;

    
    
    public ArrayList<DefElement> elements = new ArrayList<>();
    
    public Definition(String type, String id){
        this.type = type;
        this.id = id;
    }
    
    public Definition(Definition other){
        id = other.id;
        type = other.type;
        elements = new ArrayList<>(other.elements);
//        update();
    }
    
    public boolean equals(Definition other){
        if(!id.equals(other.id))
            return false;
        if(!type.equals(other.type))
            return false;
        
        for(int i=0; i<elements.size(); i++)
            if(!elements.get(i).equals(other.elements.get(i)))
                return false;
        
        return true;
    }
    
    public void updateElements(ArrayList<DefElement> elements){
        this.elements.clear();
        this.elements.addAll(elements);
        update();
    }
    
    void update(){
        
    }
}
