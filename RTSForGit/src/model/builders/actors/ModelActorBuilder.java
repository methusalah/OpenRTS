/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders.actors;

import model.battlefield.actors.Actor;
import ressources.definitions.BuilderLibrary;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import ressources.definitions.DefElement;
import math.Angle;
import model.battlefield.actors.AnimationActor;
import model.battlefield.actors.ModelActor;
import model.battlefield.actors.ParticleActor;
import model.battlefield.actors.PhysicActor;
import model.battlefield.actors.ProjectileActor;
import model.battlefield.actors.UnitActor;
import model.battlefield.army.components.Movable;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import static model.builders.actors.ActorBuilder.TRIGGER;
import static model.builders.actors.ActorBuilder.TYPE;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class ModelActorBuilder extends ActorBuilder{
    private static final String MODEL_PATH = "ModelPath";
    private static final String SCALE = "Scale";

    private String modelPath;
    private double scale;
    
    public ModelActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE :
                case TRIGGER :
                case ACTOR_LIST : break;
                case MODEL_PATH : modelPath = de.getVal(); break;
                case SCALE : scale = de.getDoubleVal(); break;
                default:printUnknownElement(de.name);
            }        
    }
    
    public Actor build(Movable movable){
        return build("", movable, null);
    }

    @Override
    public Actor build(String trigger, Actor parent) {
        throw new RuntimeException("Can't create Model actor without a Movable");
    }
    
    public Actor build(String trigger, Movable movable, Actor parent){
        Actor res;
        switch(type){
            case TYPE_DEFAULT : res = new Actor(parent, trigger, childrenTriggers, getChildrenBuilders(), lib.battlefield.actorPool); break;
            case TYPE_UNIT : res = new UnitActor(parent, trigger, childrenTriggers, getChildrenBuilders(), lib.battlefield.actorPool, modelPath, scale, movable); break;
            case TYPE_PROJECTILE : res = new ProjectileActor(parent, trigger, childrenTriggers, getChildrenBuilders(), lib.battlefield.actorPool, modelPath, scale, movable); break;
            default : printUnknownValue(TYPE, type); throw new RuntimeException();
        }
        res.debbug_id = getId();
        return res;
        
    }
}
