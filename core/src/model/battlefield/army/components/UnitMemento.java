package model.battlefield.army.components;

import geometry.geom3d.Point3D;

import java.util.List;

import model.battlefield.warfare.Faction;
import model.builders.entity.definitions.BuilderManager;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores the minimal information needed to define a unit at this initial position.
 *
 * For serialisation/deserialization purpose only.
 *
 */
public class UnitMemento {
	@JsonProperty
	private String builderID;
	@JsonProperty
	private String factionName;
	@JsonProperty
	private Point3D pos;
	@JsonProperty
	private double orientation;

	public UnitMemento() {

	}

	public UnitMemento(Unit u) {
		builderID = u.builderID;
		factionName = u.faction.getName();
		pos = u.pos;
		orientation = u.getOrientation();
	}

	public Unit getUnit(List<Faction> factions) {
		for(Faction f : factions) {
			if (f.getName().equals(factionName)) {
				Unit u = BuilderManager.getUnitBuilder(builderID).build(f, pos, orientation);
				u.drawOnBattlefield();
				return u;
			}
		}
		throw new RuntimeException("impossible to build unit, check faction names");
	}
}
