package model.battlefield.army;

import java.util.ArrayList;

import model.battlefield.army.components.Unit;

/**
 * A Unity is a list of unit of the same type.
 * 
 * Used in the controller to select all units of the same time in the screen 
 *
 */
public class Unity extends ArrayList<Unit> {
    
    public String UIName;
    
    
    @Override
    public boolean add(Unit u){
        if(isEmpty())
            UIName = u.UIName;
        else if(!u.UIName.equals(UIName))
            throw new IllegalArgumentException("Trying to add a unit of the wrong config type.");
        return super.add(u);
    }
    
}
