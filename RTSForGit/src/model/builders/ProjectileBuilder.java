/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import geometry.Point2D;
import ressources.definitions.BuilderLibrary;
import geometry3D.Point3D;
import ressources.definitions.DefElement;
import model.battlefield.army.ArmyManager;
import model.battlefield.actors.ProjectileActor;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import ressources.definitions.Definition;
import model.battlefield.army.effects.LauncherEffect;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ProjectileBuilder extends Builder {
    static final String SPEED = "Speed";
    static final String MASS = "Mass";
    static final String MOVER_LINK = "MoverLink";
    static final String ACTOR_LINK = "ActorLink";

    static final String PRECISION = "Precision"; 
    public static final String PRECISION_CENTER = "Center"; 
    public static final String PRECISION_IN_RADIUS = "InRadius";
    public static final String PRECISION_OTHER = "Other";

    private double radius = 0;
    private double separationRadius = 0;
    private double speed;
    private double mass;
    private String moverLink;
    private String actorLink;
    private Projectile.PrecisionType precisionType;
    private double precision;
    
    public ProjectileBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case SPEED : speed = de.getDoubleVal(); break;
                case MASS : mass = de.getDoubleVal(); break;
                case MOVER_LINK : moverLink = de.getVal(); break;
                case ACTOR_LINK : actorLink = de.getVal(); break;
                case PRECISION :
                    switch (de.getVal()){
                        case PRECISION_IN_RADIUS : precisionType = Projectile.PrecisionType.InRadius; break;
                        case PRECISION_CENTER : precisionType = Projectile.PrecisionType.Center; break;
                        default : precisionType = Projectile.PrecisionType.Other;
                            precision = de.getDoubleVal();
                            break;
                    }
            }
    }
    
    public Projectile build(Point3D pos, Unit target, Point3D targetPoint){
        Projectile res = new Projectile(radius, separationRadius, speed, mass, pos, lib.getMoverBuilder(moverLink), precisionType, precision, lib.getActorBuilder(actorLink), target, targetPoint);
        lib.armyManager.registerProjectile(res);
        return res;
    }
}
