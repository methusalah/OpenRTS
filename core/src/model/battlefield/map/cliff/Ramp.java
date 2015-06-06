package model.battlefield.map.cliff;

import geometry.math.Angle;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Create a passage into cliffs to allow hiker hiking throught levels. For now, ramps are only orthogonal and work on one-level height cliffs only. A ramp is
 * created on a cliff, replace it and create cliffs at its sides. Ramps can be enlarged but not narrowed.
 */

public class Ramp {

	private List<Tile> tiles = new ArrayList<>();
	@JsonIgnore
	int minX, maxX, minY, maxY;

	private int level;
	private double angle;
	private List<Integer> tilesRef = new ArrayList<>();

	public Ramp(Tile t) {
		ModelManager.getBattlefield().getMap().ramps.add(this);
		if (!t.hasCliff()) {
			throw new IllegalArgumentException("Ramp must be first created on a cliff.");
		}
		angle = t.getLowerCliff().angle;
		level = t.getNeighborsMaxLevel();
		tiles.add(t);
		compute();
	}

	public Ramp(int level, double angle, List<Integer> tilesRef) {
		this.level = level;
		this.angle = angle;
		this.tilesRef = tilesRef;
	}

	/**
	 * for deserialization purpose
	 *
	 * @param map
	 */
	public void connect(Map map) {
		for (Integer ref : tilesRef) {
			tiles.add(map.getTile(ref));
		}
		compute();
	}

	private void compute() {
		minX = Integer.MAX_VALUE;
		maxX = 0;
		minY = Integer.MAX_VALUE;
		maxY = 0;
		for (Tile t : tiles) {
			minX = t.x < minX ? t.x : minX;
			maxX = t.x > maxX ? t.x : maxX;
			minY = t.y < minY ? t.y : minY;
			maxY = t.y > maxY ? t.y : maxY;
		}
		tilesRef.clear();
		for (Tile t : tiles) {
			tilesRef.add(ModelManager.getBattlefield().getMap().getRef(t));
			t.ramp = this;
			t.level = level;
			t.elevation = -Tile.STAGE_HEIGHT * getSlopeRate(t);
			for (Tile n : ModelManager.getBattlefield().getMap().get8Around(t)) {
				n.ramp = this;
			}
		}

	}

	public void add(ArrayList<Tile> tiles) {
		this.tiles.addAll(tiles);
		compute();
	}

	public void grow(Tile t) {
		if (angle == 0) {
			if (t.x > maxX) {
				growEast();
			}
			if (t.y > maxY) {
				growNorth();
			}
			if (t.y < minY) {
				growSouth();
			}
		} else if (angle == Angle.FLAT) {
			if (t.x < minX) {
				growWest();
			}
			if (t.y > maxY) {
				growNorth();
			}
			if (t.y < minY) {
				growSouth();
			}
		} else if (angle == Angle.RIGHT) {
			if (t.x < minX) {
				growWest();
			}
			if (t.x > maxX) {
				growEast();
			}
			if (t.y > maxY) {
				growNorth();
			}
		} else if (angle == -Angle.RIGHT) {
			if (t.x < minX) {
				growWest();
			}
			if (t.x > maxX) {
				growEast();
			}
			if (t.y < minY) {
				growSouth();
			}
		}
	}

	private void growNorth() {
		ArrayList<Tile> grown = new ArrayList<>();
		for (Tile t : tiles) {
			if (t.n != null && !tiles.contains(t.n)) {
				grown.add(t.n);
			}
		}
		add(grown);
	}

	private void growSouth() {
		ArrayList<Tile> grown = new ArrayList<>();
		for (Tile t : tiles) {
			if (t.s != null && !tiles.contains(t.s)) {
				grown.add(t.s);
			}
		}
		add(grown);
	}

	private void growEast() {
		ArrayList<Tile> grown = new ArrayList<>();
		for (Tile t : tiles) {
			if (t.e != null && !tiles.contains(t.e)) {
				grown.add(t.e);
			}
		}
		add(grown);
	}

	private void growWest() {
		ArrayList<Tile> grown = new ArrayList<>();
		for (Tile t : tiles) {
			if (t.w != null && !tiles.contains(t.w)) {
				grown.add(t.w);
			}
		}
		add(grown);

	}

	/**
	 * Get the slope rate at the given tile coords. At the top of the ramp, slope is 0, and 1 at the bottom.
	 *
	 * @param t
	 * @return
	 */
	public double getSlopeRate(Tile t) {
		if (t.ramp != this) {
			return 0;
		}
		if (angle == 0) {
			if (t.x > maxX) {
				return 1;
			} else if (t.x < minX) {
				return 0;
			} else {
				return (double) (t.x - minX) / (maxX - minX + 1);
			}
		} else if (angle == Angle.FLAT) {
			if (t.x > maxX) {
				return 0;
			} else if (t.x < minX) {
				return 1;
			} else {
				return (double) (maxX - t.x + 1) / (maxX - minX + 1);
			}

		} else if (angle == Angle.RIGHT) {
			if (t.y > maxY) {
				return 1;
			} else if (t.y < minY) {
				return 0;
			} else {
				return (double) (t.y - minY) / (maxY - minY + 1);
			}
		} else if (angle == -Angle.RIGHT) {
			if (t.y > maxY) {
				return 0;
			} else if (t.y < minY) {
				return 1;
			} else {
				return (double) (maxY - t.y + 1) / (maxY - minY + 1);
			}
		}
		throw new RuntimeException();
	}

	public double getCliffSlopeRate(Tile t) {
		if (angle == 0) {
			return getSlopeRate(t);
		} else if (angle == Angle.FLAT) {
			return getSlopeRate(t.e);
		} else if (angle == Angle.RIGHT) {
			return getSlopeRate(t);
		} else if (angle == -Angle.RIGHT) {
			return getSlopeRate(t.n);
		}
		throw new RuntimeException();
	}

	public List<Tile> destroy() {
		List<Tile> res = new ArrayList<>();
		res.addAll(tiles);
		for (Tile t : tiles) {
			t.ramp = null;
			t.level--;
			t.elevation = 0;
			for (Tile n : ModelManager.getBattlefield().getMap().get8Around(t)) {
				if (!res.contains(n)) {
					res.add(n);

					n.ramp = null;
					n.elevation = 0;
				}
			}
		}
		ModelManager.getBattlefield().getMap().ramps.remove(this);
		return res;

	}

	public List<Tile> getTiles() {
		return tiles;
	}

}
