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
    public String turretBone;
    public double turretOrientation;
    
    public UnitActor(String trigger, Actor parent){
        super(trigger, parent);
    }
    
    public void setUnit(Unit unit){
        movable = unit;
        act();
    }
    
    public void updateTurretOrientation(){
        turretOrientation = ((Unit)movable).getTurretOrientation();
    }
    
    public boolean hasTurret(){
        return getUnit().hasTurret();
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

}
