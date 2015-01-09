/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import model.battlefield.actors.Actor;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import model.battlefield.actors.AnimationActor;
import model.battlefield.army.components.Movable;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class AnimationActorBuilder extends ActorBuilder{
    static final String ANIMATION_NAME = "AnimName";
    static final String SPEED = "Speed";
    static final String CYCLE = "Cycle";
    static final String CYCLE_ONCE = "Once";
    static final String CYCLE_LOOP = "Loop";
    static final String CYCLE_CYCLE = "Cycle";
    
    String animationName;
    double speed;
    AnimationActor.Cycle cycle;
    
    public AnimationActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case ANIMATION_NAME : animationName = de.getVal(); break;
                case SPEED : speed = de.getDoubleVal(); break;
                case CYCLE :
                    switch (de.getVal()){
                        case CYCLE_ONCE : cycle = AnimationActor.Cycle.Once; break;
                        case CYCLE_LOOP : cycle = AnimationActor.Cycle.Loop; break;
                        case CYCLE_CYCLE : cycle = AnimationActor.Cycle.Cycle; break;
                    }
                    break;
                default:throw new RuntimeException("'"+de.name+"' element unknown in "+def.id+" actor.");
            }        
    }
    
    public Actor build(String trigger, Actor parent){
        Actor res;
        res = new AnimationActor(trigger, parent);
        res.armyManager = lib.armyManager;
        return res;
    }
}
