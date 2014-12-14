/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import ressources.definitions.BuilderLibrary;
import geometry.Point2D;
import geometry3D.Point3D;
import ressources.definitions.DefElement;
import java.util.HashMap;
import model.army.ArmyManager;
import ressources.definitions.Definition;
import model.army.data.effects.DamageEffect;
import model.army.data.effects.LauncherEffect;
import model.army.data.effects.PersistentEffect;

/**
 *
 * @author Benoît
 */
public class EffectBuilder {
    static final String TYPE = "Type"; 
    static final String EFFECT_LINK_LIST = "EffectLinkList";

    public static final String TYPE_DAMAGE = "Damage";
    public static final String TYPE_PERSISTENT = "Persistent";
    public static final String TYPE_LAUNCHER = "Launcher";

    // damage
    static final String AMOUNT = "Amount";
    
    // persistent
    static final String PERIOD_COUNT = "PeriodCount";
    static final String PERIOD_DURATION_LIST = "DurationList";
    static final String PERIOD_RANGE_LIST = "RangeList";
    
    // launcher
    static final String PROJECTILE_LINK = "ProjectileLink";

    String effectType;
    Definition def;
    BuilderLibrary lib;
    
    public EffectBuilder(Definition def, BuilderLibrary lib){
        this.def = def;
        this.lib = lib;
        for(DefElement de : def.elements)
            if(de.name.equals(TYPE)){
                effectType = de.getVal();
                break;
            }

    }
    
    public Effect build(Unit source, Unit target, Point3D targetPoint){
        Effect res;
        switch(effectType){
            case TYPE_DAMAGE : res = new DamageEffect(source, target, targetPoint); break;
            case TYPE_PERSISTENT :
                res = new PersistentEffect(source, target, targetPoint);
                lib.am.addPersistentEffect((PersistentEffect)res);
                break;
            case TYPE_LAUNCHER : res = new LauncherEffect(source, target, targetPoint); break;
            default : throw new RuntimeException("Unknown effect type (id : "+def.id+").");
        }
        
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE : res.type = de.getVal(); break;
                case EFFECT_LINK_LIST : res.effectBuilders.add(lib.getEffectBuilder(de.getVal())); break;

                case AMOUNT : res.amount = de.getIntVal(); break;

                case PERIOD_COUNT : res.periodCount = de.getIntVal(); break;
                case PERIOD_DURATION_LIST : res.durations.add(de.getDoubleVal()*1000); break;
                case PERIOD_RANGE_LIST : res.ranges.add(de.getDoubleVal()*1000); break;
                    
                case PROJECTILE_LINK : res.projectile = lib.getProjectileBuilder(de.getVal()).build((LauncherEffect)res, target, targetPoint); break;
            }
        return res;
    }
}
