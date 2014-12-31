/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army;

import model.army.data.Unit;
import model.army.data.Projectile;
import geometry.Point2D;
import java.awt.Color;
import java.util.ArrayList;
import model.army.data.Actor;
import ressources.definitions.BuilderLibrary;
import model.map.Map;
import model.army.data.effects.PersistentEffect;
import model.warfare.Faction;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ArmyManager {
    public ArrayList<Unit> units = new ArrayList<>();
    public ArrayList<Unit> destroyedUnits = new ArrayList<>();

    private ArrayList<PersistentEffect> persistenteffects = new ArrayList<>();
    public ArrayList<Projectile> projectiles = new ArrayList<>();
    public ArrayList<Actor> activeActors = new ArrayList<>();
    public ArrayList<Actor> deletedActors = new ArrayList<>();

    public void createTestArmy(BuilderLibrary lib){
        Faction f1 = new Faction(Color.RED);
        Faction f2 = new Faction(Color.GREEN);
        f1.setEnnemy(f2);
        
        for(int y=32; y<40; y+=1)
            for(int x=16; x<25; x+=1)
                lib.buildUnitFromRace("human", f1, new Point2D(x, y));
        for(int y=2; y<15; y+=2)
            for(int x=44; x<56; x+=2)
                lib.buildUnitFromRace("alien", f2, new Point2D(x, y));
        LogUtil.logger.info("count : "+units.size());
        
    }

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
    public void registerProjectile(Projectile projectile){
        projectiles.add(projectile);
    }
    
    public void registerActor(Actor actor){
        activeActors.add(actor);
    }
    
    public void deleteActor(Actor actor){
        activeActors.remove(actor);
        deletedActors.add(actor);
    }
    
    public ArrayList<Actor> grabDeletedActors(){
        ArrayList<Actor> res = new ArrayList<>(deletedActors);
        deletedActors.clear();
        return res;
    }
    public ArrayList<Actor> getActors(){
        ArrayList<Actor> res = new ArrayList<>(activeActors);
        return res;
    }
}
