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
import model.army.data.actors.ModelActor;
import model.army.data.actors.UnitActor;
import model.army.data.definitions.Definition;
import model.army.data.effects.DamageEffect;
import model.army.data.effects.LauncherEffect;
import model.army.data.effects.PersistentEffect;

/**
 *
 * @author Benoît
 */
public class ActorBuilder {
    static final String TYPE = "Type"; 

    public static final String TYPE_UNIT = "Unit";
    public static final String TYPE_PARTICULE = "Particule";
    public static final String TYPE_ANIMATION = "Animation";

    static final String MODEL_PATH = "ModelPath";
    static final String SCALE = "Scale";
    static final String TURRET_BONE = "TurretBone";
    static final String WEAPON_BONE = "WeaponBone";

    
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

    }
    
    public Actor build(Movable movable, Actor parent){
        Actor res;
        switch(type){
            case TYPE_UNIT :
                res = new UnitActor("", parent);
                ((UnitActor)res).setUnit((Unit)movable);
                break;
            default : throw new RuntimeException("Unknown actor type (id : "+def.id+").");
        }
        
        for(DefElement de : def.elements)
            switch(de.name){
                case MODEL_PATH : ((ModelActor)res).modelPath = de.getVal(); break;
                case SCALE : ((ModelActor)res).scale = de.getDoubleVal(); break;
                case TURRET_BONE : ((UnitActor)res).turretBone = de.getVal(); break;
                case WEAPON_BONE : ; break;
            }
        return res;
    }
}
