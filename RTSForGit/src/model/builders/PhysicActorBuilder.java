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
public class PhysicActorBuilder extends ActorBuilder{
    // physic
    static final String MASS = "Mass";
    static final String LIFE = "Life";
    static final String MASS_CENTER_BONE = "MassCenterBone";
    
    double mass;
    double life;
    String massCenterBone;
    
    public PhysicActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case LIFE : life = de.getDoubleVal()*1000; break;
                case MASS : mass = de.getDoubleVal(); break;
                case MASS_CENTER_BONE : massCenterBone = de.getVal(); break;
                default:throw new RuntimeException("'"+de.name+"' element unknown in "+def.id+" actor.");
            }        
    }
    
    public Actor build(String trigger, Movable movable, Actor parent){
        Actor res;
        res = new RagdollActor(trigger, parent);
        res.armyManager = lib.armyManager;
        res.id = def.id;
        return res;
    }
}
