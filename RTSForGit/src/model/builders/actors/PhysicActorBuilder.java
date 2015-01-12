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
import static model.builders.actors.ActorBuilder.TRIGGER;
import static model.builders.actors.ActorBuilder.TYPE;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class PhysicActorBuilder extends ActorBuilder{
    private static final String MODEL_PATH = "ModelPath";
    private static final String SCALE = "Scale";
    private static final String MASS = "Mass";
    private static final String LIFE = "Life";
    private static final String MASS_CENTER_BONE = "MassCenterBone";
    
    private String modelPath;
    private double scale = 1;
    private double mass = 1;
    private double life = 1;
    private String massCenterBone;
    
    public PhysicActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE :
                case TRIGGER :
                case ACTOR_LIST : break;
                case MODEL_PATH : modelPath = de.getVal(); break;
                case SCALE : scale = de.getDoubleVal(); break;
                case LIFE : life = de.getDoubleVal()*1000; break;
                case MASS : mass = de.getDoubleVal(); break;
                case MASS_CENTER_BONE : massCenterBone = de.getVal(); break;
                default:printUnknownElement(de.name);
            }        
    }
    
    public Actor build(String trigger, Hiker movable, Actor parent){
        return new PhysicActor(modelPath, scale, life, mass, massCenterBone, parent, trigger, childrenTriggers, getChildrenBuilders(), lib.battlefield.actorPool);
    }
}
