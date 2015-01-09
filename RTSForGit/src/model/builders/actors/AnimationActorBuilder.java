/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders.actors;

import model.battlefield.actors.Actor;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import model.battlefield.actors.AnimationActor;
import model.battlefield.army.components.Movable;
import static model.builders.actors.ActorBuilder.TYPE;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class AnimationActorBuilder extends ActorBuilder{
    private static final String ANIMATION_NAME = "AnimName";
    private static final String SPEED = "Speed";
    private static final String CYCLE = "Cycle";
    private static final String CYCLE_ONCE = "Once";
    private static final String CYCLE_LOOP = "Loop";
    private static final String CYCLE_CYCLE = "Cycle";
    
    private String animationName;
    private double speed;
    private AnimationActor.Cycle cycle;
    
    public AnimationActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE :
                case TRIGGER :
                case ACTOR_LIST : break;
                case ANIMATION_NAME : animationName = de.getVal(); break;
                case SPEED : speed = de.getDoubleVal(); break;
                case CYCLE :
                    switch (de.getVal()){
                        case CYCLE_ONCE : cycle = AnimationActor.Cycle.Once; break;
                        case CYCLE_LOOP : cycle = AnimationActor.Cycle.Loop; break;
                        case CYCLE_CYCLE : cycle = AnimationActor.Cycle.Cycle; break;
                    }
                    break;
                default:printUnknownElement(de.name);
            }        
    }
    
    @Override
    public Actor build(String trigger, Actor parent){
        return new AnimationActor(parent, trigger, childrenTriggers, getChildrenBuilders(), lib.armyManager, animationName, cycle, speed);
    }
}
