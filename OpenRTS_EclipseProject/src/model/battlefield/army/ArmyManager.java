package model.battlefield.army;

import java.util.ArrayList;

import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import model.battlefield.army.effects.PersistentEffect;

/**
 * updates and destroys all armies' elements at each frame
 * 
 * @author Benoît
 */
public class ArmyManager {
    public ArrayList<Unit> units = new ArrayList<>();
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
        
    }

    public void addPersistentEffect(PersistentEffect eff){
        persistenteffects.add(eff);
    }
    
    public void registerUnit(Unit unit){
        units.add(unit);
    }
    public void unregisterUnit(Unit unit){
        unit.removeFromBattlefield();
        units.remove(unit);
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
        update(0);
    }
}
