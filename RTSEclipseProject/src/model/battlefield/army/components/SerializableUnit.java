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
public class SerializableUnit {
    @Element
    public final String builderID;
    @Element
    public final String factionName;
    @Element
    public Point3D pos;
    @Element
    public double yaw;
    
    public SerializableUnit(Unit u) {
        builderID = u.builderID;
        factionName = u.faction.name;
        pos = u.pos;
        yaw = u.yaw;
    }
    public SerializableUnit(@Element(name="builderID")String builderID,
            @Element(name="factionName")String factionName,
            @Element(name="pos")Point3D pos,
            @Element(name="yaw")double yaw) {
    	this.builderID = builderID;
    	this.factionName = factionName;
    	this.pos = pos;
        this.yaw = yaw;
    }
    
    public Unit getUnit(BuilderLibrary lib, List<Faction> factions){
        for(Faction f : factions)
            if(f.name.equals(factionName))
                    return lib.getUnitBuilder(builderID).build(f, pos, yaw);
        throw new RuntimeException("impossible to build unit, check faction names");
    }
}
