/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import ressources.definitions.BuilderLibrary;
import geometry.Point2D;
import geometry3D.Point3D;
import java.awt.Color;
import ressources.definitions.DefElement;
import java.util.HashMap;
import math.Angle;
import model.army.ArmyManager;
import model.army.data.actors.AnimationActor;
import model.army.data.actors.ModelActor;
import model.army.data.actors.MovableActor;
import model.army.data.actors.ParticleActor;
import model.army.data.actors.RagdollActor;
import model.army.data.actors.ProjectileActor;
import model.army.data.actors.UnitActor;
import ressources.definitions.Definition;
import model.army.data.effects.DamageEffect;
import model.army.data.effects.LauncherEffect;
import model.army.data.effects.PersistentEffect;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ActorBuilder {
    static final String TYPE = "Type"; 

    static final String TYPE_UNIT = "Unit";
    static final String TYPE_PROJECTILE = "Projectile";
    static final String TYPE_PARTICLE = "Particle";
    static final String TYPE_ANIMATION = "Animation";
    static final String TYPE_DEFAULT = "Default";
    static final String TYPE_PHYSIC = "Physic";
    
    static final String ACTOR_LIST = "ActorList";
    static final String TRIGGER = "Trigger";
    static final String ACTOR_LINK = "ActorLink";

    // model
    static final String MODEL_PATH = "ModelPath";
    static final String SCALE = "Scale";
    
    // physic
    static final String MASS = "Mass";
    static final String LIFE = "Life";
    static final String MASS_CENTER_BONE = "MassCenterBone";
    
    // animation
    static final String ANIMATION_NAME = "AnimName";
    static final String SPEED = "Speed";
    static final String CYCLE = "Cycle";
    static final String CYCLE_ONCE = "Once";
    static final String CYCLE_LOOP = "Loop";
    static final String CYCLE_CYCLE = "Cycle";
    
    // particle
    static final String SPRITE_PATH = "SpritePath";
    static final String NB_COL = "NbCol";
    static final String NB_ROW = "NbRow";
    static final String EMISSION_NODE = "EmissionBone";
    static final String DIRECTION_NODE = "DirectionBone";
    static final String VELOCITY = "Velocity";
    static final String FANNING = "Fanning";
    static final String MAX_COUNT = "MaxCount";
    static final String PER_SECOND = "PerSecond";
    static final String DURATION = "Duration";
    static final String START_SIZE = "StartSize";
    static final String END_SIZE = "EndSize";
    static final String START_COLOR = "StartColor";
    static final String END_COLOR = "EndColor";
    static final String MIN_LIFE = "MinLife";
    static final String MAX_LIFE = "MaxLife";
    static final String GRAVITY = "Gravity";
    static final String FACING = "Facing";
    static final String FACING_VELOCITY = "Velocity";
    static final String FACING_HORIZONTAL = "Horizontal";
    static final String ADD = "Add";
    static final String START_VARIATION = "StartVariation";
    static final String ROTATION_SPEED = "RotationSpeed";
    

    static final String RED = "R";
    static final String GREEN = "G";
    static final String BLUE = "B";
    static final String ALPHA = "A";
    
    
    
    String type;
    Definition def;
    BuilderLibrary lib;
    
    public ActorBuilder(Definition def, BuilderLibrary lib){
        this.def = def;
        this.lib = lib;
        for(DefElement de : def.elements)
            if(de.name.equals(TYPE)){
                type = de.getVal();
                break;
            }
        if(type == null)
            type = TYPE_DEFAULT;

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
        switch(type){
            case TYPE_DEFAULT :
                res = new Actor(trigger, parent);
                res.armyManager = lib.armyManager;
                break;
            case TYPE_UNIT :
                res = new UnitActor(trigger, parent);
                res.armyManager = lib.armyManager;
                ((UnitActor)res).setUnit((Unit)movable);
                break;
            case TYPE_PROJECTILE :
                res = new ProjectileActor(trigger, parent);
                res.armyManager = lib.armyManager;
                ((ProjectileActor)res).setProjectile((Projectile)movable);
                break;
            case TYPE_ANIMATION :
                res = new AnimationActor(trigger, parent);
                res.armyManager = lib.armyManager;
                break;
            case TYPE_PARTICLE :
                res = new ParticleActor(trigger, parent);
                res.armyManager = lib.armyManager;
                break;
            case TYPE_PHYSIC :
                res = new RagdollActor(trigger, parent);
                res.armyManager = lib.armyManager;
                break;
            default : throw new RuntimeException("Unknown actor type (id : "+def.id+").");
        }
        res.id = def.id;
        
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE : break;
                case TRIGGER : res.trigger = de.getVal(); break;
                case ACTOR_LIST :
                    Actor child = lib.getActorBuilder(de.getVal(ACTOR_LINK)).build(de.getVal(TRIGGER), res);
                    res.children.add(child);
                    break;
                    
                // model
                case MODEL_PATH : ((ModelActor)res).modelPath = de.getVal(); break;
                case SCALE : ((ModelActor)res).scale = de.getDoubleVal(); break;
                    
                // physic
                case LIFE : ((RagdollActor)res).startLife = de.getDoubleVal()*1000; break;
                case MASS : ((RagdollActor)res).mass = de.getDoubleVal(); break;
                case MASS_CENTER_BONE : ((RagdollActor)res).massCenterBone = de.getVal(); break;
                    
                // animation
                case ANIMATION_NAME : ((AnimationActor)res).animName = de.getVal(); break;
                case SPEED : ((AnimationActor)res).speed = de.getDoubleVal(); break;
                case CYCLE :
                    switch (de.getVal()){
                        case CYCLE_ONCE : ((AnimationActor)res).cycle = AnimationActor.Cycle.Once; break;
                        case CYCLE_LOOP : ((AnimationActor)res).cycle = AnimationActor.Cycle.Loop; break;
                        case CYCLE_CYCLE : ((AnimationActor)res).cycle = AnimationActor.Cycle.Cycle; break;
                    }
                    break;
                    
                // particle
                case SPRITE_PATH : ((ParticleActor)res).spritePath = de.getVal(); break;
                case NB_COL : ((ParticleActor)res).nbCol = de.getIntVal(); break;
                case NB_ROW : ((ParticleActor)res).nbRow = de.getIntVal(); break;
                case EMISSION_NODE : ((ParticleActor)res).emissionBone = de.getVal(); break;
                case DIRECTION_NODE : ((ParticleActor)res).directionBone = de.getVal(); break;
                case VELOCITY : ((ParticleActor)res).velocity = de.getDoubleVal(); break;
                case FANNING : ((ParticleActor)res).fanning = de.getDoubleVal(); break;
                case MAX_COUNT : ((ParticleActor)res).maxCount = de.getIntVal(); break;
                case PER_SECOND : ((ParticleActor)res).perSecond = de.getIntVal(); break;
                case DURATION : ((ParticleActor)res).duration = de.getDoubleVal(); break;
                case START_SIZE : ((ParticleActor)res).startSize = de.getDoubleVal(); break;
                case END_SIZE : ((ParticleActor)res).endSize = de.getDoubleVal(); break;
                case START_COLOR :
                    ((ParticleActor)res).startColor = new Color(de.getIntVal(RED),
                            de.getIntVal(GREEN),
                            de.getIntVal(BLUE),
                            de.getIntVal(ALPHA));
                    break;
                case END_COLOR :
                    ((ParticleActor)res).endColor = new Color(de.getIntVal(RED),
                            de.getIntVal(GREEN),
                            de.getIntVal(BLUE),
                            de.getIntVal(ALPHA));
                    break;
                case MIN_LIFE : ((ParticleActor)res).minLife = de.getDoubleVal(); break;
                case MAX_LIFE : ((ParticleActor)res).maxLife = de.getDoubleVal(); break;
                case GRAVITY : ((ParticleActor)res).gravity = de.getBoolVal(); break;
                case FACING : 
                    switch (de.getVal()){
                        case FACING_HORIZONTAL : ((ParticleActor)res).facing = ParticleActor.Facing.Horizontal; break;
                        case FACING_VELOCITY : ((ParticleActor)res).facing = ParticleActor.Facing.Velocity; break;
                    }
                    break;
                case ADD : ((ParticleActor)res).add = de.getBoolVal(); break;
                case START_VARIATION : ((ParticleActor)res).startVariation = de.getDoubleVal(); break;
                case ROTATION_SPEED : ((ParticleActor)res).rotationSpeed = Angle.toRadians(de.getDoubleVal()); break;

                default:throw new RuntimeException("'"+de.name+"' element unknown in "+def.id+" actor.");
            }
        return res;
    }
}
