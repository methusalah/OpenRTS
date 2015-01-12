/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import geometry3D.Point3D;
import java.util.List;
import model.battlefield.Battlefield;
import model.battlefield.army.ArmyManager;
import model.battlefield.abstractComps.Hiker;
import model.builders.actors.ActorBuilder;

/**
 *
 * @author Benoît
 */
public class HikerActor extends ModelActor {
    final Hiker hiker;

    public HikerActor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool,
            String modelPath,
            double scale,
            Hiker hiker) {
        super(parent, trigger, childrenTriggers, childrenBuilders, pool, modelPath, scale);
        this.hiker = hiker;
        act();
    }
    
    public Point3D getPos(){
        return hiker.getPos();
    }
    
    public double getYaw(){
        return hiker.getYaw();
    }
    
    public Point3D getDirection(){
        return hiker.direction;
    }

    @Override
    public String getType() {
        return "movable";
    }
    
    
    
}
