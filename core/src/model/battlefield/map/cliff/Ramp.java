package model.battlefield.map.cliff;

import geometry.geom2d.Point2D;
import geometry.math.AngleUtil;

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
	
	private ModelManager modelManager;

	public Ramp(Tile t, ModelManager modelManager) {
		modelManager.getBattlefield().getMap().getRamps().add(this);
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
			tiles.add((Tile)map.get(ref));
		}
		compute();
	}

	private void compute() {
		minX = Integer.MAX_VALUE;
		maxX = 0;
		minY = Integer.MAX_VALUE;
		maxY = 0;
		for (Tile t : tiles) {
			Point2D p = t.getCoord();
			minX = (int)p.getX() < minX ? (int)p.getX() : minX;
			maxX = (int)p.getX() > maxX ? (int)p.getX() : maxX;
			minY = (int)p.getY() < minY ? (int)p.getY() : minY;
			maxY = (int)p.getY() > maxY ? (int)p.getY() : maxY;
		}
		tilesRef.clear();
		for (Tile t : tiles) {
			tilesRef.add(t.getIndex());
			t.ramp = this;
			t.level = level;
			t.setElevation(-Tile.STAGE_HEIGHT * getSlopeRate(t));
			for (Tile n : modelManager.getBattlefield().getMap().get8Around(t)) {
				n.ramp = this;
			}
		}

	}

	public void add(ArrayList<Tile> tiles) {
		this.tiles.addAll(tiles);
		compute();
	}

	public void grow(Tile t) {
		Point2D p = t.getCoord();
		if (angle == 0) {
			if (p.getX() > maxX) {
				growEast();
			}
			if (p.getY() > maxY) {
				growNorth();
			}
			if (p.getY() < minY) {
				growSouth();
			}
		} else if (angle == AngleUtil.FLAT) {
			if (p.getX() < minX) {
				growWest();
			}
			if (p.getY() > maxY) {
				growNorth();
			}
			if (p.getY() < minY) {
				growSouth();
			}
		} else if (angle == AngleUtil.RIGHT) {
			if (p.getX() < minX) {
				growWest();
			}
			if (p.getX() > maxX) {
				growEast();
			}
			if (p.getY() > maxY) {
				growNorth();
			}
		} else if (angle == -AngleUtil.RIGHT) {
			if (p.getX() < minX) {
				growWest();
			}
			if (p.getX() > maxX) {
				growEast();
			}
			if (p.getY() < minY) {
				growSouth();
			}
		}
	}

	private void growNorth() {
		ArrayList<Tile> grown = new ArrayList<>();
		for (Tile t : tiles) {
			if (t.n() != null && !tiles.contains(t.n())) {
				grown.add(t.n());
			}
		}
		add(grown);
	}

	private void growSouth() {
		ArrayList<Tile> grown = new ArrayList<>();
		for (Tile t : tiles) {
			if (t.s() != null && !tiles.contains(t.s())) {
				grown.add(t.s());
			}
		}
		add(grown);
	}

	private void growEast() {
		ArrayList<Tile> grown = new ArrayList<>();
		for (Tile t : tiles) {
			if (t.e() != null && !tiles.contains(t.e())) {
				grown.add(t.e());
			}
		}
		add(grown);
	}

	private void growWest() {
		ArrayList<Tile> grown = new ArrayList<>();
		for (Tile t : tiles) {
			if (t.w() != null && !tiles.contains(t.w())) {
				grown.add(t.w());
			}
		}
		add(grown);

	}

	/**
	 * Get the slope rate at the given tile coords. At the top of the ramp, slope is 0, and 1 at the bottom.
	 *
	 * @param p
	 * @return
	 */
	public double getSlopeRate(Tile t) {
		Point2D p = t.getCoord();
		if (t.ramp != this) {
			return 0;
		}
		if (angle == 0) {
			if (p.getX() > maxX) {
				return 1;
			} else if (p.getX() < minX) {
				return 0;
			} else {
				return (double) (p.getX() - minX) / (maxX - minX + 1);
			}
		} else if (angle == AngleUtil.FLAT) {
			if (p.getX() > maxX) {
				return 0;
			} else if (p.getX() < minX) {
				return 1;
			} else {
				return (double) (maxX - p.getX() + 1) / (maxX - minX + 1);
			}

		} else if (angle == AngleUtil.RIGHT) {
			if (p.getY() > maxY) {
				return 1;
			} else if (p.getY() < minY) {
				return 0;
			} else {
				return (double) (p.getY() - minY) / (maxY - minY + 1);
			}
		} else if (angle == -AngleUtil.RIGHT) {
			if (p.getY() > maxY) {
				return 0;
			} else if (p.getY() < minY) {
				return 1;
			} else {
				return (double) (maxY - p.getY() + 1) / (maxY - minY + 1);
			}
		}
		throw new RuntimeException();
	}

	public double getCliffSlopeRate(Tile t) {
		if (angle == 0) {
			return getSlopeRate(t);
		} else if (angle == AngleUtil.FLAT) {
			return getSlopeRate(t.e());
		} else if (angle == AngleUtil.RIGHT) {
			return getSlopeRate(t);
		} else if (angle == -AngleUtil.RIGHT) {
			return getSlopeRate(t.n());
		}
		throw new RuntimeException();
	}

	public List<Tile> destroy() {
		List<Tile> res = new ArrayList<>();
		res.addAll(tiles);
		for (Tile t : tiles) {
			t.ramp = null;
			t.level--;
			t.setElevation(0);
			for (Tile n : modelManager.getBattlefield().getMap().get8Around(t)) {
				if (!res.contains(n)) {
					res.add(n);

					n.ramp = null;
					n.setElevation(0);
				}
			}
		}
		modelManager.getBattlefield().getMap().getRamps().remove(this);
		return res;

	}

	public List<Tile> getTiles() {
		return tiles;
	}

}
