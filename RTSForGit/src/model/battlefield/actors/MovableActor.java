/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import geometry3D.Point3D;
import java.util.List;
import model.battlefield.Battlefield;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.components.Movable;
import model.builders.actors.ActorBuilder;

/**
 *
 * @author Benoît
 */
public class MovableActor extends ModelActor {
    final Movable movable;

    public MovableActor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool,
            String modelPath,
            double scale,
            Movable movable) {
        super(parent, trigger, childrenTriggers, childrenBuilders, pool, modelPath, scale);
        this.movable = movable;
        act();
    }
    
    public Point3D getPos(){
        return movable.getPos();
    }
    
    public double getOrientation(){
        return movable.getOrientation();
    }

    @Override
    public String getType() {
        return "movable";
    }
    
    
    
}
