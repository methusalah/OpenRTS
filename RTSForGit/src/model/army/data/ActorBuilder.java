/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry.Point2D;
import geometry3D.Point3D;
import model.army.data.definitions.DefElement;
import java.util.HashMap;
import model.army.ArmyManager;
import model.army.data.actors.AnimationActor;
import model.army.data.actors.ModelActor;
import model.army.data.actors.ProjectileActor;
import model.army.data.actors.UnitActor;
import model.army.data.definitions.Definition;
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
    static final String TYPE_PARTICULE = "Particule";
    static final String TYPE_ANIMATION = "Animation";
    static final String TYPE_DEFAULT = "Default";
    
    static final String ACTOR_LIST = "ActorList";
    static final String TRIGGER = "Trigger";
    static final String ACTOR_LINK = "ActorLink";

    static final String MODEL_PATH = "ModelPath";
    static final String SCALE = "Scale";
    static final String TURRET_BONE = "TurretBone";
    static final String WEAPON_BONE = "WeaponBone";
    
    static final String ANIMATION_NAME = "AnimName";
    static final String SPEED = "Speed";
    static final String CYCLE = "Cycle";
    static final String CYCLE_ONCE = "Once";
    static final String CYCLE_LOOP = "Loop";
    static final String CYCLE_CYCLE = "Cycle";
    

    
    String type;
    Definition def;
    private ArmyManager am;
    BuilderLibrary lib;
    
    public ActorBuilder(Definition def, ArmyManager am, BuilderLibrary lib){
        this.def = def;
        this.am = am;
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
                res.armyManager = am;
                break;
            case TYPE_UNIT :
                res = new UnitActor(trigger, parent);
                res.armyManager = am;
                ((UnitActor)res).setUnit((Unit)movable);
                break;
            case TYPE_PROJECTILE :
                res = new ProjectileActor(trigger, parent);
                res.armyManager = am;
                ((ProjectileActor)res).setProjectile((Projectile)movable);
                break;
            case TYPE_ANIMATION :
                res = new AnimationActor(trigger, parent);
                res.armyManager = am;
                break;
            default : throw new RuntimeException("Unknown actor type (id : "+def.id+").");
        }
        
        for(DefElement de : def.elements)
            switch(de.name){
                case TRIGGER : res.trigger = de.getVal(); break;
                case ACTOR_LIST :
                    Actor child = lib.getActorBuilder(de.getVal(ACTOR_LINK)).build(de.getVal(TRIGGER), res);
                    res.children.add(child);
                    break;
                    
                case MODEL_PATH : ((ModelActor)res).modelPath = de.getVal(); break;
                case SCALE : ((ModelActor)res).scale = de.getDoubleVal(); break;
                case TURRET_BONE : ((UnitActor)res).turretBone = de.getVal(); break;
                case WEAPON_BONE : ; break;
                    
                case ANIMATION_NAME : ((AnimationActor)res).animName = de.getVal(); break;
                case SPEED : ((AnimationActor)res).speed = de.getDoubleVal(); break;
                case CYCLE : switch (de.getVal()){
                    case CYCLE_ONCE : ((AnimationActor)res).cycle = AnimationActor.Cycle.Once; break;
                    case CYCLE_LOOP : ((AnimationActor)res).cycle = AnimationActor.Cycle.Loop; break;
                    case CYCLE_CYCLE : ((AnimationActor)res).cycle = AnimationActor.Cycle.Cycle; break;
                }

            }
        return res;
    }
}
