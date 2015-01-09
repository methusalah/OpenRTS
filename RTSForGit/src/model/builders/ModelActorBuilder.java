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
public class ModelActorBuilder extends ActorBuilder{
    static final String MODEL_PATH = "ModelPath";
    static final String SCALE = "Scale";

    String modelPath;
    double scale;
    
    public ModelActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case MODEL_PATH : modelPath = de.getVal(); break;
                case SCALE : scale = de.getDoubleVal(); break;
                default:throw new RuntimeException("'"+de.name+"' element unknown in "+def.id+" actor.");
            }        
    }
    
    public Actor build(Movable movable){
        return build("", movable, null);
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
            default : throw new RuntimeException("Unknown actor type (id : "+def.id+").");
        }
        res.id = def.id;
        

        return res;
    }
}
