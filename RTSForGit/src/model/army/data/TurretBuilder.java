/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import model.army.data.definitions.DefElement;
import java.util.HashMap;
import math.Angle;
import model.army.data.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class TurretBuilder {
    static final String SPEED = "Speed";
    static final String IDLE_SPEED = "IdleSpeed";
    static final String ON_IDLE = "OnIdle";
    static final String BONE_NAME = "BoneName";
    
    static final String RESET_ON_MOVE = "ResetOnMove";
    static final String RESET = "Reset";
    static final String SPIN = "Spin";
    static final String HOLD = "Hold";

    Definition def;
    
    public TurretBuilder(Definition def){
        this.def = def;
    }
    
    public Turret build(){
        Turret res = new Turret();
        for(DefElement de : def.elements)
            switch(de.name){
                case SPEED : res.speed = Angle.toRadians(de.getDoubleVal()); break;
                case IDLE_SPEED : res.idleSpeed = Angle.toRadians(de.getDoubleVal()); break;
                case ON_IDLE :
                    switch (de.getVal()){
                        case RESET_ON_MOVE : res.onIdle = Turret.OnIdleBehave.RESET_ON_MOVE; break;
                        case RESET : res.onIdle = Turret.OnIdleBehave.RESET; break;
                        case SPIN : res.onIdle = Turret.OnIdleBehave.SPIN; break;
                        case HOLD : res.onIdle = Turret.OnIdleBehave.HOLD; break;
                    }
                    break;
                case BONE_NAME : res.boneName = de.getVal(); break;
            }
        return res;
    }
}
