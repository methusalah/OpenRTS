package model.battlefield.map;

import geometry.structure.grid3D.Node3D;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base element of the map. Contains two informations : - height for relief - level for cliffs The cliffs and ramps themselves are also stored in tiles. Tiles
 * would be used in the future to store any field comp that is over them, to find nearby field comp without searching all the list (quad tree). Important note :
 * a tile is placed by its lower left coordinate. This has many non-intuitive consequents and must be kept in mind. for example, tiles at the east side of a
 * plateau will have thier coord at the upper ground, when tiles at the west will have their coord at the lower ground.
 */
public class Tile extends Node3D{
	public static final double STAGE_HEIGHT = 2;

	@JsonProperty
	public int level;
	@JsonProperty
	private String cliffShapeID = "";

	@JsonIgnore
	private Cliff cliff0;
	@JsonIgnore
	private Cliff cliff1;
	@JsonIgnore
	private Cliff cliff2;
	@JsonIgnore
	protected int modifiedLevel = 0;
	@JsonIgnore
	public Ramp ramp;
	@JsonIgnore
	public boolean hasBlockingTrinket = false;
	
	@JsonIgnore
	public List<Object> storedData = new ArrayList<>();

	public Tile(Map map, int index) {
		super(map, index);
		level = 0;
	}
	
	@JsonIgnore
	public boolean isBlocked() {
		return hasCliff() || hasBlockingTrinket;
	}

	@JsonIgnore
	public boolean hasCliff() {
		for (int i = 0; i < 3; i++) {
			if (hasCliffOnLevel(i)) {
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean hasCliffOnLevel(int level) {
		return getCliff(level) != null;
	}

	@JsonIgnore
	public Cliff getCliff(int level) {
		switch (level) {
			case 0:
				return cliff0;
			case 1:
				return cliff1;
			case 2:
				return cliff2;
			default:
				throw new IllegalArgumentException(level + " is not valid cliff level ");
		}
	}

	public void setCliff(int level, Cliff cliff) {
		switch (level) {
			case 0:
				cliff0 = cliff;
				break;
			case 1:
				cliff1 = cliff;
				break;
			case 2:
				cliff2 = cliff;
				break;
			default:
				throw new IllegalArgumentException(level + " is not valid cliff level ");
		}
	}

	@JsonIgnore
	public double getZ() {
		if (modifiedLevel != 0) {
			return modifiedLevel * STAGE_HEIGHT + elevation;
		} else {
			return level * STAGE_HEIGHT + elevation;
		}
	}

	@JsonIgnore
	public int getModifiedLevel() {
		if (modifiedLevel != 0) {
			return modifiedLevel;
		} else {
			return level;
		}
	}

	@JsonIgnore
	public Cliff getLowerCliff() {
		for (int i = 0; i < 3; i++) {
			if (getCliff(i) != null) {
				return getCliff(i);
			}
		}
		throw new RuntimeException("Tile has no cliff " + this);
	}

	@JsonIgnore
	public Cliff getUpperCliff() {
		Cliff res = getLowerCliff();
		for (int i = res.level + 1; i < 3; i++) {
			if (getCliff(i) != null) {
				res = getCliff(i);
			}
		}
		return res;
	}

	@JsonIgnore
	public List<Cliff> getCliffs() {
		List<Cliff> res = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			if (getCliff(i) != null) {
				res.add(getCliff(i));
			}
		}
		return res;

	}

	public String getCliffShapeID() {
		return cliffShapeID;
	}

	public void setCliffShapeID(String cliffShapeID) {
		this.cliffShapeID = cliffShapeID;
	}

	@Override
	public String toString() {
		return "Tile [level=" + level + "]";
	}
}
