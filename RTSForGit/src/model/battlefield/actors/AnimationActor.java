/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import java.util.List;
import model.battlefield.Battlefield;
import model.battlefield.army.ArmyManager;
import model.builders.actors.ActorBuilder;

/**
 *
 * @author Benoît
 */
public class AnimationActor extends Actor {
    public enum Cycle{Once, Loop, Cycle};
    
    public final String animName;
    public final Cycle cycle;
    public final double speed;
    
    public boolean launched = false;

    public AnimationActor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool,
            String animName,
            Cycle cycle,
            double speed) {
        super(parent, trigger, childrenTriggers, childrenBuilders, pool);
        this.animName = animName;
        this.cycle = cycle;
        this.speed = speed;
    }
    

    @Override
    public void act() {
        launched = false;
        super.act();
    }

    
    @Override
    public void stopActing() {
        super.stopActing();
        launched = false;
    }
    
    public ModelActor getParentModelActor(){
        Actor parent = this;
        do {
            parent = parent.getParent();
            if(parent == null)
                throw new RuntimeException("AnimationActor seems to miss a modelActor parent");
            
        } while(!parent.containsModel());
        return (ModelActor)parent;

    }

    @Override
    public String getType() {
        return "animation";
    }
    
    
    
    
}
