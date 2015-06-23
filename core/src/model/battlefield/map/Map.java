package model.battlefield.map;

import geometry.geom2d.Point2D;
import geometry.structure.grid.Grid;
import geometry.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.atlas.Atlas;
import model.battlefield.map.cliff.Ramp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains everything to set up a terrain and explore it. Map is mainly : - a tile based grid with relief and cliffs - a texture atlas to paint on the ground -
 * a list of trinkets Also contains methods and fields dedicated to serialization/deserialization.
 */
public class Map extends Grid {

	@JsonIgnore
	public MapStyle style = new MapStyle();

	@JsonProperty
	public String mapStyleID;

	@JsonProperty
	public List<Tile> tiles = new ArrayList<>();

	@JsonProperty
	public List<Ramp> ramps = new ArrayList<>();
	
	@JsonProperty
	public List<TrinketMemento> serializableTrinkets = new ArrayList<>();

	@JsonIgnore
	public List<Trinket> trinkets = new ArrayList<>();

	@JsonProperty
	public Atlas atlas, cover;

	@JsonProperty
	public int width;
	@JsonProperty
	public int height;

	public Map(int width, int height) {
		this.width = width;
		this.height = height;
		atlas = new Atlas(width, height);
		atlas.finalize();
		cover = new Atlas(width, height);
		cover.finalize();
		
		tiles = new ArrayList<>(width * height);
	}

	public Map() {
	}

	public boolean isBlocked(int x, int y) {
		return getTile(x, y).isBlocked() ? true : false;
	}

	/*
	 * Fast Voxel Traversal Algorithm for Ray Tracing John Amanatides Andrew Woo
	 */
	public boolean meetObstacle(Point2D p1, Point2D p2) {
		// calculate the direction of the ray (linear algebra)
		double dirX = p2.x - p1.x;
		double dirY = p2.y - p1.y;
		double length = Math.sqrt(dirX * dirX + dirY * dirY);
		dirX /= length; // normalize the direction vector
		dirY /= length;
		double tDeltaX = 1 / Math.abs(dirX); // how far we must move in the ray direction before we encounter a new voxel in x-direction
		double tDeltaY = 1 / Math.abs(dirY); // same but y-direction

		// start voxel coordinates
		int x = (int) Math.floor(p1.x); // use your transformer function here
		int y = (int) Math.floor(p1.y);

		// end voxel coordinates
		int endX = (int) Math.floor(p2.x);
		int endY = (int) Math.floor(p2.y);

		// decide which direction to start walking in
		int stepX = (int) Math.signum(dirX);
		int stepY = (int) Math.signum(dirY);

		double tMaxX, tMaxY;
		// calculate distance to first intersection in the voxel we start from
		if (dirX < 0) {
			tMaxX = (x - p1.x) / dirX;
		} else {
			tMaxX = (x + 1 - p1.x) / dirX;
		}

		if (dirY < 0) {
			tMaxY = (y - p1.y) / dirY;
		} else {
			tMaxY = (y + 1 - p1.y) / dirY;
		}

		// check if first is occupied
		if (getTile(x, y).isBlocked()) {
			return true;
		}
		boolean reachedX = false, reachedY = false;
		while (!reachedX || !reachedY) {
			if (tMaxX < tMaxY) {
				tMaxX += tDeltaX;
				x += stepX;
			} else {
				tMaxY += tDeltaY;
				y += stepY;
			}
			if (getTile(x, y).isBlocked()) {
				return true;
			}

			if (stepX > 0) {
				if (x >= endX) {
					reachedX = true;
				}
			} else if (x <= endX) {
				reachedX = true;
			}

			if (stepY > 0) {
				if (y >= endY) {
					reachedY = true;
				}
			} else if (y <= endY) {
				reachedY = true;
			}
		}
		return false;
	}

	public boolean isWalkable(Point2D p) {
		return  !isInBounds(p) || getTile(p).isBlocked() ? false : true;
	}

	public void saveTrinkets() {
		serializableTrinkets.clear();
		for (Trinket t : trinkets) {
			serializableTrinkets.add(new TrinketMemento(t));
		}
	}

	public void resetTrinkets() {
		trinkets.clear();
		for (TrinketMemento st : serializableTrinkets) {
			Trinket t = st.getTrinket();
			trinkets.add(t);
			t.drawOnBattlefield();
		}
	}

	public void prepareForBattle() {
		for (Tile t : tiles) {
			t.hasBlockingTrinket = false;
		}
		for (Trinket t : trinkets) {
			if (t.getRadius() != 0) {
				for (Tile n : get9Around(getTile(t.getCoord()))) {
					if (n.getCoord().getAddition(0.5, 0.5).getDistance(t.getCoord()) < t.getRadius() + 0.3) {
						n.hasBlockingTrinket = true;
					}
				}
			}
		}
	}

	public List<Ramp> getRamps() {
		return ramps;
	}

}
