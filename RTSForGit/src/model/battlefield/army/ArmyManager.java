/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army;

import model.battlefield.army.components.Unit;
import model.battlefield.army.components.Projectile;
import geometry.Point2D;
import java.awt.Color;
import java.util.ArrayList;
import model.battlefield.actors.Actor;
import model.battlefield.army.effects.Effect;
import ressources.definitions.BuilderLibrary;
import model.battlefield.map.Map;
import model.battlefield.army.effects.PersistentEffect;
import model.battlefield.warfare.Faction;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ArmyManager {
    public ArrayList<Unit> units = new ArrayList<>();
    public ArrayList<Unit> destroyedUnits = new ArrayList<>();

    public ArrayList<PersistentEffect> persistenteffects = new ArrayList<>();
    public ArrayList<Projectile> projectiles = new ArrayList<>();

    public void update(double elapsedTime) {
        ArrayList<Unit> destroyedThisTurn = new ArrayList<>();
        for(Unit u : units) {
            if(u.destroyed()){
                u.faction.units.remove(u);
                destroyedThisTurn.add(u);
            } else {
                u.update(elapsedTime);
            }
        }
        units.removeAll(destroyedThisTurn);
        destroyedUnits.addAll(destroyedThisTurn);
        
        // update persistent effects
        ArrayList<PersistentEffect> terminated = new ArrayList<>();
        for(PersistentEffect e : persistenteffects)
            if(e.terminated)
                terminated.add(e);
            else
                e.update();
        persistenteffects.removeAll(terminated);
        
        // update projectiles
        ArrayList<Projectile> arrived = new ArrayList<>();
        for(Projectile p : projectiles)
            if(p.arrived)
                arrived.add(p);
            else
                p.update(elapsedTime);
        projectiles.removeAll(arrived);
        
//        ArrayList<Actor> safeList = new ArrayList<>();
//        safeList.addAll(activeActors);
//        for(Actor a : safeList)
//            if(a.isDestroyed() && ! a.isActing())
//                deleteActor(a);
    }

    public ArrayList<Unit> getUnits(){
        ArrayList<Unit> res = new ArrayList<>();
        res.addAll(units);
        res.addAll(destroyedUnits);
        return res;
    }
    
    public void addPersistentEffect(PersistentEffect eff){
        persistenteffects.add(eff);
    }
    
    public void registerUnit(Unit unit){
        units.add(unit);
    }
    public void unregisterUnit(Unit unit){
        unit.removeFromBattlefield();
    }
    
    public void registerProjectile(Projectile projectile){
        projectiles.add(projectile);
    }
    public void unregisterProjectile(Projectile projectile){
        projectile.removeFromBattlefield();
    }
    
    public void reset(){
        for(Unit u : units)
            u.removeFromBattlefield();
        for(Projectile p : projectiles)
            p.removeFromBattlefield();
        persistenteffects.clear();
    }
}
