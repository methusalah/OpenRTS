/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.components;

import geometry3D.Point3D;
import java.util.List;
import model.battlefield.warfare.Faction;
import model.builders.UnitBuilder;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import ressources.definitions.BuilderLibrary;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
@Root
public class UnitPlacement {
    @Element
    public final String builderID;
    @Element
    public final String factionName;
    @Element
    public Point3D pos;
    @Element
    public double yaw;
    
    private Unit currentInstance;

    public UnitPlacement(String builderID, String factionName) {
        this.builderID = builderID;
        this.factionName = factionName;
    }
    public UnitPlacement(String builderID, String factionName, Point3D pos) {
        this(builderID, factionName);
        this.pos = pos;
    }
    public UnitPlacement(@Element(name="builderID")String builderID,
            @Element(name="factionName")String factionName,
            @Element(name="pos")Point3D pos,
            @Element(name="yaw")double yaw) {
        this(builderID, factionName, pos);
        this.yaw = yaw;
    }
    
    public Unit getNewInstance(BuilderLibrary lib, List<Faction> factions){
        LogUtil.logger.info("unit instanciated : "+builderID+" for faction "+factionName+"at pos "+pos);
        for(Faction f : factions)
            if(f.name.equals(factionName)){
                    currentInstance = lib.getUnitBuilder(builderID).build(f, pos, yaw);
                    break;
            }
        if(currentInstance == null)
            throw new RuntimeException("impossible to build unit, check faction names");
        return currentInstance;
    }
    
    public boolean isInstance(Unit u){
        return u == currentInstance;
    }
    
    
    
}
