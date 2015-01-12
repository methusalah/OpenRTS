/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import java.util.ArrayList;
import java.util.List;
import model.Commander;
import model.battlefield.Battlefield;
import model.battlefield.army.ArmyManager;
import model.battlefield.abstractComps.Hiker;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.builders.actors.ActorBuilder;

/**
 *
 * @author Benoît
 */
public class UnitActor extends HikerActor {

    public UnitActor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool,
            String modelPath,
            double scale,
            Hiker movable) {
        super(parent, trigger, childrenTriggers, childrenBuilders, pool, modelPath, scale, movable);
        act();
    }
    
    public ArrayList<Turret> getTurrets(){
        return ((Unit)hiker).getTurrets();
    }
    
    @Override
    public String getLabel(){
        return getUnit().label;
    }
    
    public Unit getUnit(){
        return (Unit)hiker;
    }
    
    public boolean isSelectedOn(Commander commander){
        if(commander.selection.contains(getUnit()))
            return true;
        return false;
    }

    public boolean isSelected(){
        return getUnit().selected;
    }

    @Override
    public String getType() {
        return "unit";
    }
    
    

}
