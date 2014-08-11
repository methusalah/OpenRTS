/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army;

import model.army.data.Unit;
import java.util.ArrayList;

/**
 * A Unity is a list of unit of the same type
 *
 * @author Benoît
 */
public class Unity extends ArrayList<Unit> {
    
    public String id;
    
    
    @Override
    public boolean add(Unit u){
        if(isEmpty())
            id = u.id;
        else if(!u.id.equals(id))
            throw new IllegalArgumentException("Trying to add a unit of the wrong config type.");
        return super.add(u);
    }
    
}
