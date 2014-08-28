/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.actors;

import java.util.ArrayList;
import model.Commander;
import model.army.data.Actor;
import model.army.data.Turret;
import model.army.data.Unit;

/**
 *
 * @author Benoît
 */
public class UnitActor extends MovableActor {
    public UnitActor(String trigger, Actor parent){
        super(trigger, parent);
    }
    
    public void setUnit(Unit unit){
        movable = unit;
        act();
    }
    
    public ArrayList<Turret> getTurrets(){
        return ((Unit)movable).getTurrets();
    }
    
    @Override
    public String getLabel(){
        return getUnit().label;
    }
    
    public Unit getUnit(){
        return (Unit)movable;
    }
    
    public boolean isSelectedOn(Commander commander){
        if(commander.selection.contains(getUnit()))
            return true;
        return false;
    }

    @Override
    public String getType() {
        return "unit";
    }
    
    

}
