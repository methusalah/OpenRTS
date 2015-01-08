/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army;

import model.battlefield.army.components.Unit;
import java.util.ArrayList;

/**
 * A Unity is a list of unit of the same type
 *
 * @author Benoît
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
