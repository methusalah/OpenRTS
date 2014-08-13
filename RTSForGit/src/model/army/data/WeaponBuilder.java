/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import model.army.data.definitions.DefElement;
import java.util.HashMap;
import model.army.data.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class WeaponBuilder {
    static final String UINAME = "UIName"; 
    static final String RANGE = "Range"; 
    static final String SCAN_RANGE = "ScanRange"; 
    static final String PERIOD = "Period"; 
    static final String EFFECT_LINK = "EffectLink";
    static final String ACTOR_LINK = "ActorLink";

    Definition def;
    BuilderLibrary lib;
            
    public WeaponBuilder(Definition def, BuilderLibrary lib){
        this.def = def;
        this.lib = lib;
    }
    
    public Weapon build(Unit holder){
        Weapon res = new Weapon(holder);
        for(DefElement de : def.elements)
            switch(de.name){
                case UINAME : res.UIName = de.getVal(); break;
                case RANGE : res.range = de.getDoubleVal(); break;
                case SCAN_RANGE : res.scanRange = de.getDoubleVal(); break;
                case PERIOD : res.period = de.getDoubleVal(); break;
                case EFFECT_LINK : res.effectBuilder = lib.getEffectBuilder(de.getVal()); break;
                case ACTOR_LINK : res.actor = lib.getActorBuilder(de.getVal()).build("", holder.actor); break;
            }
        return res;
    }
}
