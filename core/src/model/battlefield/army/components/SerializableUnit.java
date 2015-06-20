package model.battlefield.army.components;

import geometry.geom3d.Point3D;

import java.util.List;

import model.battlefield.warfare.Faction;
import model.builders.definitions.BuilderManager;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores the minimal information needed to define a unit at this initial position.
 *
 * For serialisation/deserialization purpose only.
 *
 */
public class SerializableUnit {
	@JsonProperty
	private String builderID;
	@JsonProperty
	private String factionName;
	@JsonProperty
	private Point3D pos;
	@JsonProperty
	private double yaw;

	public SerializableUnit() {

	}

	public SerializableUnit(Unit u) {
		builderID = u.builderID;
		factionName = u.faction.getName();
		pos = u.pos;
		yaw = u.yaw;
	}

	public Unit getUnit(List<Faction> factions) {
		for(Faction f : factions) {
			if (f.getName().equals(factionName)) {
				Unit u = BuilderManager.getUnitBuilder(builderID).build(f, pos, yaw);
				u.drawOnBattlefield();
				return u;
			}
		}
		throw new RuntimeException("impossible to build unit, check faction names");
	}
}
