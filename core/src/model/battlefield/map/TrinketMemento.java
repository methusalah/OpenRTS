package model.battlefield.map;

import geometry.geom3d.Point3D;
import model.builders.entity.definitions.BuilderManager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TrinketMemento {
	@JsonProperty
	private String builderID;
	@JsonProperty
	private Point3D pos;
	@JsonProperty
	private double orientation;
	@JsonProperty
	private double scaleX, scaleY, scaleZ;
	@JsonProperty
	private String modelPath;

	public TrinketMemento() {

	}

	public TrinketMemento(Trinket t) {
		builderID = t.builderID;
		pos = t.pos;
		orientation = t.getOrientation();
		scaleX = t.scaleX;
		scaleY = t.scaleY;
		scaleZ = t.scaleZ;
		modelPath = t.modelPath;
	}

	@JsonIgnore
	public Trinket getTrinket() {
		Trinket res = BuilderManager.getTrinketBuilder(builderID).build(pos, orientation, modelPath, scaleX, scaleY, scaleZ);
		return res;

	}

}
