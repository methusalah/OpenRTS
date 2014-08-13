/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry3D.Point3D;
import model.army.data.definitions.DefElement;
import model.army.ArmyManager;
import model.army.data.definitions.Definition;
import model.army.data.effects.LauncherEffect;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ProjectileBuilder {
    static final String SPEED = "Speed";
    static final String MASS = "Mass";
    static final String MOVER_LINK = "MoverLink";
    static final String ACTOR_LINK = "ActorLink";

    static final String MODELPATH = "ModelPath";
    static final String PRECISION = "Precision"; 
    public static final String PRECISION_CENTER = "Center"; 
    public static final String PRECISION_IN_RADIUS = "InRadius";
    public static final String PRECISION_OTHER = "Other";

    ArmyManager am;
    Definition def;
    BuilderLibrary lib;
    
    public ProjectileBuilder(Definition def, BuilderLibrary lib, ArmyManager am){
        this.def = def;
        this.am = am;
        this.lib = lib;
    }
    
    public Projectile build(LauncherEffect launcher, Unit target, Point3D targetPoint){
        Projectile res = new Projectile(launcher, target, targetPoint);
        for(DefElement de : def.elements)
            switch(de.name){
                case SPEED : res.speed = de.getDoubleVal(); break;
                case MASS : res.mass = de.getDoubleVal(); break;
                case MOVER_LINK : res.mover = lib.getMoverBuilder(de.getVal()).build(res, launcher.source.getPos()); break;
                case ACTOR_LINK : lib.getActorBuilder(de.getVal()).build(res); break;
                
                case MODELPATH : res.modelPath = de.getVal(); break;
                case PRECISION :
                    switch (de.getVal()){
                        case PRECISION_IN_RADIUS : res.precisionType = de.getVal(); break;
                        case PRECISION_CENTER : res.precisionType = de.getVal(); break;
                        default :
                            res.precisionType = PRECISION_OTHER;
                            res.precision = de.getDoubleVal();
                    }
            }
        res.updateTargetPoint();
        am.registerProjectile(res);
        return res;
    }
}
