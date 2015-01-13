/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import geometry3D.Point3D;
import java.util.List;
import model.battlefield.abstractComps.FieldComp;
import model.builders.actors.ActorBuilder;

/**
 *
 * @author Benoît
 */
public class FieldCompActor extends ModelActor {
    public final static String TYPE = "FieldCompActor";
    final FieldComp comp;

    public FieldCompActor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool,
            String modelPath,
            double scale,
            FieldComp comp) {
        super(parent, trigger, childrenTriggers, childrenBuilders, pool, modelPath, scale);
        this.comp = comp;
        act();
    }
    
    public Point3D getPos(){
        return comp.getPos();
    }
    
    public double getYaw(){
        return comp.getYaw();
    }
    
    public Point3D getDirection(){
        return comp.direction;
    }

    @Override
    public String getType() {
        return "movable";
    }
}
