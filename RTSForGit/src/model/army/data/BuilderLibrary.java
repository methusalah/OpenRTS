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
    private static final String ERROR = "Impossible to find ";
    
    private static final String UNIT = "Unit";
    private static final String MOVER = "Mover";
    private static final String WEAPON = "Weapon";
    private static final String TURRET = "Turret";
    private static final String EFFECT = "Effect";
    private static final String PROJECTILE = "Projectile";
    private static final String ACTOR = "Actor";
    
    private HashMap<String, UnitBuilder> unitBuilders = new HashMap<>();
    private HashMap<String, MoverBuilder> moverBuilders = new HashMap<>();
    private HashMap<String, WeaponBuilder> weaponBuilders = new HashMap<>();
    private HashMap<String, TurretBuilder> turretBuilders = new HashMap<>();
    private HashMap<String, EffectBuilder> effectBuilders = new HashMap<>();
    private HashMap<String, ProjectileBuilder> projectileBuilders = new HashMap<>();
    private HashMap<String, ActorBuilder> actorBuilders = new HashMap<>();

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
            case ACTOR : submitActor(def); break;
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
    private void submitActor(Definition def){
        actorBuilders.put(def.id, new ActorBuilder(def, am, this));
    }
    
    public void buildUnitFromRace(String race, Faction faction, Point2D pos){
        ArrayList<UnitBuilder> subList = new ArrayList<>();
        for(UnitBuilder ub : unitBuilders.values())
            if(ub.race.equals(race))
                subList.add(ub);
        
        int i = (int)Math.floor(MyRandom.next()*subList.size());
        subList.get(i).build(faction, pos);
    }
    
    public UnitBuilder getUnitBuilder(String id){
        UnitBuilder res = unitBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public MoverBuilder getMoverBuilder(String id){
        MoverBuilder res = moverBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public WeaponBuilder getWeaponBuilder(String id){
        WeaponBuilder res = weaponBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public TurretBuilder getTurretBuilder(String id){
        TurretBuilder res = turretBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public EffectBuilder getEffectBuilder(String id){
        EffectBuilder res = effectBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public ProjectileBuilder getProjectileBuilder(String id){
        ProjectileBuilder res = projectileBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public ActorBuilder getActorBuilder(String id){
        ActorBuilder res = actorBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    
    
    
}
