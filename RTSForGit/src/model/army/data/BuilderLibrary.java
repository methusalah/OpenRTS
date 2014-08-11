/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import math.MyRandom;
import model.army.ArmyManager;
import model.army.data.definitions.Definition;
import model.map.Map;
import model.warfare.Faction;

/**
 *
 * @author Benoît
 */
public class BuilderLibrary {
    private static final String UNIT = "Unit";
    private static final String MOVER = "Mover";
    private static final String WEAPON = "Weapon";
    private static final String TURRET = "Turret";
    private static final String EFFECT = "Effect";
    private static final String PROJECTILE = "Projectile";
    
    public HashMap<String, UnitBuilder> unitBuilders = new HashMap<>();
    public HashMap<String, MoverBuilder> moverBuilders = new HashMap<>();
    public HashMap<String, WeaponBuilder> weaponBuilders = new HashMap<>();
    public HashMap<String, TurretBuilder> turretBuilders = new HashMap<>();
    public HashMap<String, EffectBuilder> effectBuilders = new HashMap<>();
    public HashMap<String, ProjectileBuilder> projectileBuilders = new HashMap<>();

    Map map;
    ArmyManager am;
    
    public BuilderLibrary(Map map, ArmyManager am){
        this.map = map;
        this.am = am;
    }
    
    
    public void submit(Definition def){
        switch (def.type){
            case UNIT : submitUnit(def); break;
            case MOVER : submitMover(def); break;
            case WEAPON : submitWeapon(def); break;
            case TURRET : submitTurret(def); break;
            case EFFECT : submitEffect(def); break;
            case PROJECTILE : submitProjectile(def); break;
        }
    }
    
    private void submitUnit(Definition def){
        unitBuilders.put(def.id, new UnitBuilder(def, am, this));
    }

    private void submitMover(Definition def){
        moverBuilders.put(def.id, new MoverBuilder(def, map));
    }

    private void submitWeapon(Definition def){
        weaponBuilders.put(def.id, new WeaponBuilder(def, this));
    }

    private void submitTurret(Definition def){
        turretBuilders.put(def.id, new TurretBuilder(def));
    }

    private void submitEffect(Definition def){
        effectBuilders.put(def.id, new EffectBuilder(def, am, this));
    }

    private void submitProjectile(Definition def){
        projectileBuilders.put(def.id, new ProjectileBuilder(def, this, am));
    }
    
    public void buildUnitFromRace(String race, Faction faction, Point2D pos){
        ArrayList<UnitBuilder> subList = new ArrayList<>();
        for(UnitBuilder ub : unitBuilders.values())
            if(ub.race.equals(race))
                subList.add(ub);
        
        int i = (int)Math.floor(MyRandom.next()*subList.size());
        subList.get(i).build(faction, pos);
    }
}
