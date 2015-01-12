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
import model.battlefield.abstractComps.Hiker;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import model.builders.Builder;
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
    
    protected static final String ACTOR_LIST = "ActorList";
    protected static final String TRIGGER = "Trigger";
    protected static final String ACTOR_LINK = "ActorLink";

    protected String type;
    private List<String> childrenActorLinks = new ArrayList<>();
    protected List<String> childrenTriggers = new ArrayList<>();
    
    public ActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE : type = de.getVal(); break;
                case ACTOR_LIST :
                    childrenActorLinks.add(de.getVal(ACTOR_LINK));
                    childrenTriggers.add(de.getVal(TRIGGER));
                    break;
            }
    }
    
    public Actor build(String trigger, Actor parent){
        return new Actor(parent, trigger, childrenTriggers, getChildrenBuilders(), lib.battlefield.actorPool);
    }
    
    protected List<ActorBuilder> getChildrenBuilders(){
        List<ActorBuilder> res = new ArrayList<>();
        for(String s : childrenActorLinks)
            res.add(lib.getActorBuilder(s));
        return res;
    }
}
