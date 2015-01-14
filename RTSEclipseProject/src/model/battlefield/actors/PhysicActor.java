/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import java.util.List;
import model.builders.actors.ActorBuilder;

/**
 *
 * @author Benoît
 */
public class PhysicActor extends ModelActor {
    public final double startLife;
    public final double mass;
    public final String massCenterBone;
    
    
    public double life;
    public long timer;
    public boolean launched = false;

    public PhysicActor(String modelPath,
            double scale,
            double startLife,
            double mass,
            String massCenterBone,
            Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool) {
        super(parent, trigger, childrenTriggers, childrenBuilders, pool, modelPath, scale, scale, scale, null, null);
        this.startLife = startLife;
        this.mass = mass;
        this.massCenterBone = massCenterBone;
    }

    
    
    @Override
    public String getType() {
        return "physic";
    }
    
    public ModelActor getParentModelActor(){
        Actor parent = this;
        do {
            parent = parent.getParent();
            if(parent == null)
                throw new RuntimeException(this.getClass().getSimpleName()+" seems to miss a modelActor parent");
            
        } while(!parent.containsModel());
        return (ModelActor)parent;
    }
    
    public void renderingDone(){
        stopActing();
    }
    
    public boolean alive(){
        return life > 0;
    }

    
    
    
}
