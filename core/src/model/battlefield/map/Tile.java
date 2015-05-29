package model.battlefield.map;

import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.BoundingShape;
import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;

import org.simpleframework.xml.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base element of the map. Contains two informations : - height for relief - level for cliffs The cliffs and ramps themselves are also stored in tiles. Tiles
 * would be used in the future to store any field comp that is over them, to find nearby field comp without searching all the list (quad tree). Important note :
 * a tile is placed by its lower left coordinate. This has many non-intuitive consequents and must be kept in mind. for example, tiles at the east side of a
 * plateau will have thier coord at the upper ground, when tiles at the west will have their coord at the lower ground.
 */
public class Tile {
	public static final double STAGE_HEIGHT = 2;
	@JsonIgnore
	public Map map;
	@JsonIgnore
	public Tile n, s, e, w;

	@JsonProperty
	public int x;
	@JsonProperty
	public int y;
	@JsonProperty
	public int level;
	@JsonProperty
	public double elevation = 0;
	@JsonProperty
	private String cliffShapeID = "";

	@JsonIgnore
	private Cliff cliff0;
	@JsonIgnore
	private Cliff cliff1;
	@JsonIgnore
	private Cliff cliff2;
	@JsonIgnore
	private int modifiedLevel = 0;
	@JsonIgnore
	public Ramp ramp;
	@JsonIgnore
	public boolean hasBlockingTrinket = false;

	public Tile() {

	}

	public Tile(int x, int y, Map map) {
		this.map = map;
		this.x = x;
		this.y = y;
		level = 0;
	}

	public Tile(@Element(name = "x") int x, @Element(name = "y") int y, @Element(name = "level") int level, @Element(name = "elevation") double elevation,
			@Element(name = "cliffShapeID") String cliffShapeID) {
		this.x = x;
		this.y = y;
		this.level = level;
		this.elevation = elevation;
		this.setCliffShapeID(cliffShapeID);
	}

	public int getNeighborsMaxLevel() {
		int res = Integer.MIN_VALUE;
		for (Tile n : map.get4Around(this)) {
			if (n.level > res) {
				res = n.level;
			}
		}
		return res;
	}

	public int getNeighborsMinLevel() {
		int res = Integer.MAX_VALUE;
		for (Tile n : map.get4Around(this)) {
			if (n.level < res) {
				res = n.level;
			}
		}
		return res;
	}

	public boolean isBlocked() {
		return hasCliff() || hasBlockingTrinket;
	}

	public boolean hasCliff() {
		for (int i = 0; i < 3; i++) {
			if (hasCliffOnLevel(i)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasCliffOnLevel(int level) {
		return getCliff(level) != null;
	}

	public Point3D getPos() {
		return new Point3D(x, y, getZ());
	}

	public Point2D getCoord() {
		return new Point2D(x, y);
	}

	public BoundingShape getBounds() {
		ArrayList<Point2D> points = new ArrayList<>();
		points.add(getCoord());
		points.add(getCoord().getAddition(1, 0));
		points.add(getCoord().getAddition(1, 1));
		points.add(getCoord().getAddition(0, 1));
		return new AlignedBoundingBox(points);
	}

	public ArrayList<Tile> get4Neighbors() {
		return map.get4Around(this);
	}

	public ArrayList<Tile> get8Neighbors() {
		return map.get8Around(this);
	}

	public ArrayList<Tile> get9Neighbors() {
		return map.get9Around(this);
	}

	public void setCliff(int minLevel, int maxLevel) {
		if (ramp != null && ramp.getCliffSlopeRate(this) == 1) {
			return;
		}
		for (int level = minLevel; level < maxLevel; level++) {
			if (getCliff(level) == null) {
				setCliff(level, new Cliff(this, level));
			}
		}
		modifyLevel();

	}

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

	private void setCliff(int level, Cliff cliff) {
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

	public void unsetCliff() {
		for (int level = 0; level < 3; level++) {
			if (getCliff(level) != null) {
				getCliff(level).removeFromBattlefield();
				setCliff(level, null);
			}
		}
		modifyLevel();
	}

	public void modifyLevel() {
		modifiedLevel = 0;
		for (int i = 0; i < 3; i++) {
			Cliff c = getCliff(i);
			if (c == null || w == null || s == null || w.s == null) {
				continue;
			}
			if (w.level > c.level || s.level > c.level || w.s.level > c.level) {
				modifiedLevel = c.level + 1;
			}
		}
	}

	public double getZ() {
		if (modifiedLevel != 0) {
			return modifiedLevel * STAGE_HEIGHT + elevation;
		} else {
			return level * STAGE_HEIGHT + elevation;
		}
	}

	public int getModifiedLevel() {
		if (modifiedLevel != 0) {
			return modifiedLevel;
		} else {
			return level;
		}
	}

	public Cliff getLowerCliff() {
		for (int i = 0; i < 3; i++) {
			if (getCliff(i) != null) {
				return getCliff(i);
			}
		}
		throw new RuntimeException("Tile as no cliff " + this);
	}

	public Cliff getUpperCliff() {
		Cliff res = getLowerCliff();
		for (int i = res.level + 1; i < 3; i++) {
			if (getCliff(i) != null) {
				res = getCliff(i);
			}
		}
		return res;
	}

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
}
