/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import ressources.definitions.BuilderLibrary;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import ressources.definitions.DefElement;
import java.util.HashMap;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.effects.Effect;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import ressources.definitions.Definition;
import model.battlefield.army.effects.DamageEffect;
import model.battlefield.army.effects.LauncherEffect;
import model.battlefield.army.effects.PersistentEffect;

/**
 *
 * @author Benoît
 */
public class EffectBuilder extends Builder{
    static final String TYPE = "Type"; 
    static final String EFFECT_LINK_LIST = "EffectLinkList";
    public static final String TYPE_DAMAGE = "Damage";
    public static final String TYPE_PERSISTENT = "Persistent";
    public static final String TYPE_LAUNCHER = "Launcher";
    static final String AMOUNT = "Amount";
    static final String PERIOD_COUNT = "PeriodCount";
    static final String PERIOD_DURATION_LIST = "DurationList";
    static final String PERIOD_RANGE_LIST = "RangeList";
    static final String PROJECTILE_LINK = "ProjectileLink";
    
    String type = null;
    ArrayList<String> effectLinkList = new ArrayList<>();
    int amount;
    int periodCount;
    ArrayList<Double> durations = new ArrayList<>();
    ArrayList<Double> ranges = new ArrayList<>();
    String projectileLink = null;
    
    public EffectBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE : type = de.getVal(); break;
                case EFFECT_LINK_LIST : effectLinkList.add(de.getVal()); break;
                case AMOUNT : amount = de.getIntVal(); break;
                case PERIOD_COUNT : periodCount = de.getIntVal(); break;
                case PERIOD_DURATION_LIST : durations.add(de.getDoubleVal()*1000); break;
                case PERIOD_RANGE_LIST : ranges.add(de.getDoubleVal()*1000); break;
                case PROJECTILE_LINK : projectileLink = de.getVal(); break;
            }
    }
    
    public Effect build(Unit source, Unit target, Point3D targetPoint){
        ArrayList<EffectBuilder> effectBuilders = new ArrayList<>();
        for(String s : effectLinkList)
            effectBuilders.add(lib.getEffectBuilder(s));
        
        Projectile projectile = null;
        if(projectileLink != null)
            projectile = lib.getProjectileBuilder(projectileLink).build(source.getPos(), target, targetPoint);
        
        Effect res;
        switch(type){
            case TYPE_DAMAGE :
                res = new DamageEffect(type, amount, periodCount, durations, ranges, projectile, effectBuilders, source, target, targetPoint);
                break;
            case TYPE_PERSISTENT :
                res = new PersistentEffect(type, amount, periodCount, durations, ranges, projectile, effectBuilders, source, target, targetPoint);
                lib.armyManager.addPersistentEffect((PersistentEffect)res);
                break;
            case TYPE_LAUNCHER :
                res = new LauncherEffect(type, amount, periodCount, durations, ranges, projectile, effectBuilders, source, target, targetPoint);
                break;
            default : throw new RuntimeException("Unknown effect type (id : "+def.id+").");
        }
        return res;
    }
}
