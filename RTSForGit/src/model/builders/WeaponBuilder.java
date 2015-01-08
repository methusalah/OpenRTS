/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import geometry3D.Point3D;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import java.util.HashMap;
import model.battlefield.actors.Actor;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.battlefield.army.components.Weapon;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class WeaponBuilder extends Builder{
    static final String UINAME = "UIName"; 
    static final String RANGE = "Range";
    static final String SCAN_RANGE = "ScanRange";
    static final String PERIOD = "Period"; 
    static final String EFFECT_LINK = "EffectLink";
    static final String ACTOR_LINK = "ActorLink";
    static final String SOURCE_BONE = "SourceBone";
    static final String DIRECTION_BONE = "DirectionBone";

    private String UIName;
    private double range;
    private double scanRange;
    private double period;
    private String effectLink;
    private String actorLink = null;
    private String sourceBone;
    private String directionBone;
    
    public WeaponBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case UINAME : UIName = de.getVal(); break;
                case RANGE : range = de.getDoubleVal(); break;
                case SCAN_RANGE : scanRange = de.getDoubleVal(); break;
                case PERIOD : period = de.getDoubleVal(); break;
                case EFFECT_LINK : effectLink = de.getVal(); break;
                case ACTOR_LINK : actorLink = de.getVal(); break;
                case SOURCE_BONE : sourceBone = de.getVal(); break;
                case DIRECTION_BONE : directionBone = de.getVal(); break;
            }
    }
    
    public Weapon build(Unit holder, Turret t){
        EffectBuilder effectBuilder = lib.getEffectBuilder(effectLink);
        Actor actor = null;
        if(actorLink != null){
            actor = lib.getActorBuilder(actorLink).build();
            actor.parent = holder.actor;
        }
        Weapon res = new Weapon(UIName, range, scanRange, period, effectBuilder, sourceBone, directionBone, holder, actor, t);
        return res;
    }
}
