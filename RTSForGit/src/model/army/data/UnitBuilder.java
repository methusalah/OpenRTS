/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry.Point2D;
import model.army.data.definitions.DefElement;
import java.util.HashMap;
import model.army.ArmyManager;
import model.army.data.actors.UnitActor;
import model.army.data.definitions.Definition;
import model.warfare.Faction;

/**
 *
 * @author Benoît
 */
public class UnitBuilder {
    static final String RADIUS = "Radius";
    static final String SEPARATION_RADIUS = "SeparationRadius";
    static final String SPEED = "Speed";
    static final String MASS = "Mass";
    static final String MOVER_LINK = "MoverLink";
    
    static final String UINAME = "UIName"; 
    static final String RACE = "Race";
    static final String MODELPATH = "ModelPath";
    static final String MAXHEALTH = "MaxHealth";
    static final String SIGHT = "Sight";
    
    static final String WEAPONLIST = "WeaponList";
    static final String TURRET_LINK = "TurretLink";
    static final String WEAPON_LINK = "WeaponLink";
    static final String ACTOR_LINK = "ActorLink";

    public String id;
    public String race;
    
    private Definition def;
    private ArmyManager am;
    private BuilderLibrary lib;
            
    public UnitBuilder(Definition def, ArmyManager am, BuilderLibrary lib){
        this.def = def;
        this.am = am;
        this.lib = lib;
        id = def.id;
        for(DefElement de : def.elements)
            if(de.name.equals(RACE)){
                race = de.getVal();
                break;
            }
    }
    
    public Unit build(Faction faction, Point2D position){
        Unit res = new Unit(faction);
        res.id = def.id;
        for(DefElement de : def.elements)
            switch(de.name){
                case RADIUS : res.radius = de.getDoubleVal(); break;
                case SEPARATION_RADIUS : res.separationRadius = de.getDoubleVal(); break;
                case SPEED : res.speed = de.getDoubleVal(); break;
                case MASS : res.mass = de.getDoubleVal(); break;
                case MOVER_LINK : res.mover = lib.getMoverBuilder(de.getVal()).build(res, position); break;
                    
                case UINAME : res.UIName = de.getVal(); break;
                case RACE : res.race = de.getVal(); break;
                case MODELPATH : res.modelPath = de.getVal(); break;
                case MAXHEALTH : res.setMaxHealth(de.getIntVal()); break;
                case SIGHT : res.sight = de.getDoubleVal(); break;
                case ACTOR_LINK : res.actor = (UnitActor)lib.getActorBuilder(de.getVal()).build(res); break;
                case WEAPONLIST :
                    Weapon w = lib.getWeaponBuilder(de.getVal(WEAPON_LINK)).build(res);
                    Turret t = null;
                    if(de.getVal(TURRET_LINK) != null)
                        t = lib.getTurretBuilder(de.getVal(TURRET_LINK)).build();
                    if(w!=null){
                        res.weapons.add(w);
                        res.turrets.add(t);
                    }
                    break;
            }
        am.registerUnit(res);
        res.linkActors();
        return res;
    }
}
