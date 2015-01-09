/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import model.battlefield.actors.Actor;
import ressources.definitions.BuilderLibrary;
import java.awt.Color;
import ressources.definitions.DefElement;
import math.Angle;
import model.battlefield.actors.ParticleActor;
import model.battlefield.army.components.Movable;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class ParticleActorBuilder extends ActorBuilder{
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

    String spritePath;
    int nbCol;
    int nbRow;
    String emissionBone;
    String directionBone;
    double velocity;
    double fanning;
    int maxCount;
    int perSecond;
    double duration;
    double startSize;
    double endSize;
    Color startColor;
    Color endColor;
    double minLife;
    double maxLife;
    boolean gravity;
    ParticleActor.Facing facing;
    boolean add;
    double startVariation;
    double rotationSpeed;
    
    public ParticleActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
                for(DefElement de : def.elements)
            switch(de.name){
                // particle
                case SPRITE_PATH : spritePath = de.getVal(); break;
                case NB_COL : nbCol = de.getIntVal(); break;
                case NB_ROW : nbRow = de.getIntVal(); break;
                case EMISSION_NODE : emissionBone = de.getVal(); break;
                case DIRECTION_NODE : directionBone = de.getVal(); break;
                case VELOCITY : velocity = de.getDoubleVal(); break;
                case FANNING : fanning = de.getDoubleVal(); break;
                case MAX_COUNT : maxCount = de.getIntVal(); break;
                case PER_SECOND : perSecond = de.getIntVal(); break;
                case DURATION : duration = de.getDoubleVal(); break;
                case START_SIZE : startSize = de.getDoubleVal(); break;
                case END_SIZE : endSize = de.getDoubleVal(); break;
                case START_COLOR :
                    startColor = new Color(de.getIntVal(RED),
                            de.getIntVal(GREEN),
                            de.getIntVal(BLUE),
                            de.getIntVal(ALPHA));
                    break;
                case END_COLOR :
                    endColor = new Color(de.getIntVal(RED),
                            de.getIntVal(GREEN),
                            de.getIntVal(BLUE),
                            de.getIntVal(ALPHA));
                    break;
                case MIN_LIFE : minLife = de.getDoubleVal(); break;
                case MAX_LIFE : maxLife = de.getDoubleVal(); break;
                case GRAVITY : gravity = de.getBoolVal(); break;
                case FACING : 
                    switch (de.getVal()){
                        case FACING_HORIZONTAL : facing = ParticleActor.Facing.Horizontal; break;
                        case FACING_VELOCITY : facing = ParticleActor.Facing.Velocity; break;
                    }
                    break;
                case ADD : add = de.getBoolVal(); break;
                case START_VARIATION : startVariation = de.getDoubleVal(); break;
                case ROTATION_SPEED : rotationSpeed = Angle.toRadians(de.getDoubleVal()); break;

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
        res = new ParticleActor(trigger, parent);
        res.armyManager = lib.armyManager;
        return res;
    }
}
