/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import model.builders.actors.ModelActorBuilder;
import ressources.definitions.BuilderLibrary;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import ressources.definitions.DefElement;
import java.util.HashMap;
import model.battlefield.army.ArmyManager;
import model.battlefield.actors.UnitActor;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.battlefield.army.components.Weapon;
import ressources.definitions.Definition;
import model.battlefield.warfare.Faction;

/**
 *
 * @author Benoît
 */
public class UnitBuilder extends Builder{
    private static final String RADIUS = "Radius";
    private static final String SPEED = "Speed";
    private static final String MASS = "Mass";
    private static final String MOVER_LINK = "MoverLink";
    
    private static final String UINAME = "UIName"; 
    private static final String RACE = "Race";
    private static final String MAXHEALTH = "MaxHealth";
    
    private static final String WEAPONLIST = "WeaponList";
    private static final String TURRET_LINK = "TurretLink";
    private static final String WEAPON_LINK = "WeaponLink";
    private static final String ACTOR_LINK = "ActorLink";

    private String UIName;
    private String race;
    private int maxHealth;
    private String actorLink;
    private double radius;
    private double speed;
    private double mass;
    private String moverLink;
    private ArrayList<String> weaponLinks = new ArrayList<>();
    private ArrayList<String> turretLinks = new ArrayList<>();
    
    public UnitBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case RADIUS : radius = de.getDoubleVal(); break;
                case SPEED : speed = de.getDoubleVal(); break;
                case MASS : mass = de.getDoubleVal(); break;
                case MOVER_LINK : moverLink = de.getVal(); break;
                    
                case UINAME : UIName = de.getVal(); break;
                case RACE : race = de.getVal(); break;
                case MAXHEALTH : maxHealth = de.getIntVal(); break;
                case ACTOR_LINK : actorLink = de.getVal(); break;
                case WEAPONLIST :
                    weaponLinks.add(de.getVal(WEAPON_LINK));
                    turretLinks.add(de.getVal(TURRET_LINK));
            }
    }
    
    public Unit build(Faction faction, Point3D pos, double yaw){
        Unit res = new Unit(radius, speed, mass, pos, yaw, lib.getMoverBuilder(moverLink), UIName, race, maxHealth, faction, (ModelActorBuilder)lib.getActorBuilder(actorLink));
        
        int i = 0;
        for(String weaponLink : weaponLinks){
            WeaponBuilder wb = lib.getWeaponBuilder(weaponLink);
            TurretBuilder tb = null;
            if(turretLinks.get(i) != null)
                tb = lib.getTurretBuilder(turretLinks.get(i));
            res.addWeapon(wb, tb);
            i++;
        }
        
        return res;
    }
    
    public boolean hasRace(String race){
        return this.race.equals(race);
    }
    
    public String getUIName(){
        return UIName;
    }
}
