/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

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
import model.battlefield.actors.RagdollActor;
import model.battlefield.actors.ProjectileActor;
import model.battlefield.actors.UnitActor;
import model.battlefield.army.components.Movable;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class ActorBuilder extends Builder{
    public static final String TYPE = "Type"; 

    public static final String TYPE_UNIT = "Unit";
    public static final String TYPE_PROJECTILE = "Projectile";
    public static final String TYPE_PARTICLE = "Particle";
    public static final String TYPE_ANIMATION = "Animation";
    public static final String TYPE_DEFAULT = "Default";
    public static final String TYPE_PHYSIC = "Physic";
    
    static final String ACTOR_LIST = "ActorList";
    static final String TRIGGER = "Trigger";
    static final String ACTOR_LINK = "ActorLink";

    String type;
    List<String> actorList = new ArrayList<>();
    String trigger;
    
    public ActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE : type = de.getVal(); break;
                case TRIGGER : trigger = de.getVal(); break;
                case ACTOR_LIST :actorList.add(de.getVal(ACTOR_LINK)); break;
                default:throw new RuntimeException("'"+de.name+"' element unknown in "+def.id+" actor.");
            }
    }
    
    public Actor build(){
        return build("", null, null);
    }
    public Actor build(Movable movable){
        return build("", movable, null);
    }
    public Actor build(String trigger, Actor parent){
        return build(trigger, null, parent);
    }
    
    public Actor build(String trigger, Movable movable, Actor parent){
        Actor res;
        res = new Actor(trigger, parent);

        res.armyManager = lib.armyManager;
        return res;
    }
    
    protected List<ActorBuilder> getChildrenBuilders(){
        List<ActorBuilder> res = new ArrayList<>();
        for(String s : actorList)
            res.add(lib.getActorBuilder(s));
        return res;
    }
}
