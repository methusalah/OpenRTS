/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import ressources.definitions.DefElement;
import java.util.HashMap;
import math.Angle;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class TurretBuilder extends Builder {
    static final String SPEED = "Speed";
    static final String IDLE_SPEED = "IdleSpeed";
    static final String ON_IDLE = "OnIdle";
    static final String BONE_NAME = "BoneName";
    
    static final String RESET_ON_MOVE = "ResetOnMove";
    static final String RESET = "Reset";
    static final String SPIN = "Spin";
    static final String HOLD = "Hold";
    
    private double speed;
    private double idleSpeed;
    private Turret.OnIdleBehave onIdle;
    private String boneName;

    public TurretBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case SPEED : speed = Angle.toRadians(de.getDoubleVal()); break;
                case IDLE_SPEED : idleSpeed = Angle.toRadians(de.getDoubleVal()); break;
                case ON_IDLE :
                    switch (de.getVal()){
                        case RESET_ON_MOVE : onIdle = Turret.OnIdleBehave.RESET_ON_MOVE; break;
                        case RESET : onIdle = Turret.OnIdleBehave.RESET; break;
                        case SPIN : onIdle = Turret.OnIdleBehave.SPIN; break;
                        case HOLD : onIdle = Turret.OnIdleBehave.HOLD; break;
                        default : throw new IllegalArgumentException(de.getVal()+" is not a valid onIdle value for Turret "+def.id);
                    }
                    break;
                case BONE_NAME : boneName = de.getVal(); break;
            }
    }
    
    public Turret build(Unit holder){
        Turret res = new Turret(speed, idleSpeed, onIdle, boneName, holder);
        return res;
    }
}
