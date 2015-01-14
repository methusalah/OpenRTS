/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army;

import geometry.Point2D;
import geometry3D.Point3D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import model.battlefield.Battlefield;
import model.battlefield.army.components.Unit;
import model.battlefield.army.components.UnitPlacement;
import model.battlefield.warfare.Faction;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import ressources.definitions.BuilderLibrary;

/**
 *
 * @author Benoît
 */
@Root
public class Engagement {
    public Battlefield battlefield;
    public BuilderLibrary lib;
    
    public List<Faction> factions = new ArrayList<>();
    
    @ElementList
    public List<UnitPlacement> unitPlacements = new ArrayList<>();

    public Engagement(){
        Faction f1 = new Faction(Color.red, "1");
        Faction f2 = new Faction(Color.blue, "2");
        f1.setEnnemy(f2);
        factions.add(f1);
        factions.add(f2);
    }
    
    public Engagement(Battlefield battlefield, BuilderLibrary lib) {
        Faction f1 = new Faction(Color.red, "1");
        Faction f2 = new Faction(Color.blue, "2");
        f1.setEnnemy(f2);
        factions.add(f1);
        factions.add(f2);
        
        this.lib = lib;
        this.battlefield = battlefield;
    }
    
    public void addUnit(UnitPlacement up){
        unitPlacements.add(up);
        battlefield.armyManager.registerUnit(up.getNewInstance(lib, factions));
    }
    
    public void removeUnit(Unit u){
        unitPlacements.remove(getPlacement(u));
        battlefield.armyManager.unregisterUnit(u);
    }
    
    public void resetEngagement(){
        battlefield.armyManager.reset();
        
        for(UnitPlacement up : unitPlacements)
            battlefield.armyManager.registerUnit(up.getNewInstance(lib, factions));
    }
    
    public void setYaw(Unit u, double yaw){
        getPlacement(u).yaw = yaw;
        u.yaw = yaw;
    }
    
    public void setCoord(Unit u, Point2D coord){
        getPlacement(u).pos = coord.get3D(0);
        u.mover.changeCoord(coord);
    }
    
    private UnitPlacement getPlacement(Unit u){
        UnitPlacement res = null;
        for(UnitPlacement up : unitPlacements)
            if(up.isInstance(u)){
                res = up;
                break;
            }
        if(res == null)
            throw new RuntimeException("The unit seems not to exist in the engagement.");
        return res;
    }
    
    
    
}
