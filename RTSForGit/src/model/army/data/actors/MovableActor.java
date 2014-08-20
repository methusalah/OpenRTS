/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.actors;

import geometry3D.Point3D;
import model.army.data.Actor;
import model.army.data.Movable;

/**
 *
 * @author Benoît
 */
public class MovableActor extends ModelActor {
    Movable movable;
    
    public MovableActor(String trigger, Actor parent){
        super(trigger, parent);
    }
    
    public void setMovable(Movable movable){
        this.movable = movable;
        act();
    }
    
    public Point3D getPos(){
        return movable.getPos();
    }
    
    public double getOrientation(){
        return movable.getOrientation();
    }
    
}
