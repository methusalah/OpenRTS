package model.battlefield.army.components;

import geometry.geom3d.Point3D;

import java.util.List;

import model.battlefield.warfare.Faction;
import model.builders.definitions.BuilderLibrary;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Stores the minimal information needed to define a unit at this initial position.
 *
 * For serialisation/deserialization purpose only.
 *
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
		factionName = u.faction.getName();
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
		for(Faction f : factions) {
			if (f.getName().equals(factionName)) {
				return lib.getUnitBuilder(builderID).build(f, pos, yaw);
			}
		}
		throw new RuntimeException("impossible to build unit, check faction names");
	}
}
