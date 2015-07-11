package model.battlefield.army.motion.pathfinding;

import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Map;
import model.battlefield.map.MapTraversor;
import model.battlefield.map.Tile;

/**
 * a flow field is a table of vectors put on a tiled map full of obstacles. each vector is computed to give the ideal direction to a goal.
 */
public class FlowField {
	private static final double OBSTACLE_CHECK_DIST = 30;

	private Map map;
	public int[][] heatMap;
	Point2D[][] vectorMap;
	public int maxHeat = 0;
	int i = 0;
	List<Tile> toVisit = new ArrayList<Tile>();
	public Point2D destination;

	public FlowField(Map map, Point2D destination) {
		this.destination = destination;
		this.map = map;
		double start = System.currentTimeMillis();
		initHeatMap(map.xSize(), map.ySize());
		Tile goalTile = map.get(destination);
		toVisit.add(goalTile);

		visitMap();

		vectorMap = new Point2D[map.xSize()][map.ySize()];
		generateVectors(map.xSize(), map.ySize());
	}

	private void travelMapFrom(Tile t, int heat) {
		if (t == null || t.isBlocked() || heat >= getHeat(t)) {
			return;
		}
		setHeat(t, heat);

		travelMapFrom(t.n(), heat + 1);
		travelMapFrom(t.s(), heat + 1);
		travelMapFrom(t.e(), heat + 1);
		travelMapFrom(t.w(), heat + 1);
	}

	private void visitMap() {
		int heat = 0;
		while (!toVisit.isEmpty()) {
			ArrayList<Tile> toVisitThisTurn = new ArrayList<>();
			toVisitThisTurn.addAll(toVisit);
			toVisit.clear();
			for (Tile t : toVisitThisTurn) {
				iterate();
				if (t != null && !t.isBlocked() && getHeat(t) == Integer.MAX_VALUE) {
					setHeat(t, heat);
					toVisit.add(t.n());
					toVisit.add(t.s());
					toVisit.add(t.e());
					toVisit.add(t.w());
				}
			}
			heat++;
		}
		maxHeat = heat;
	}

	private void iterate() {
		i++;
	}

	private void initHeatMap(int width, int height) {
		heatMap = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				heatMap[i][j] = Integer.MAX_VALUE;
			}
		}
	}

	private void setHeat(Tile t, int heat) {
		heatMap[(int) t.getCoord().x][(int) t.getCoord().y] = heat;
		if (heat > maxHeat) {
			maxHeat = heat;
		}
	}

	public int getHeat(Tile t) {
		return heatMap[(int) t.getCoord().x][(int) t.getCoord().y];
	}

	public int getHeat(int x, int y) {
		return heatMap[x][y];
	}

	public boolean hasNoHeat(int x, int y) {
		return getHeat(x, y) == Integer.MAX_VALUE;
	}

	private void generateVectors(int width, int height) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (hasNoHeat(x, y)) {
					vectorMap[x][y] = Point2D.ORIGIN;
				} else {
					Point2D tileCenter = new Point2D(x + 0.5, y + 0.5);
					// First we check if there is a strait way to the destination whithout obstacle
					if (tileCenter.getDistance(destination) < OBSTACLE_CHECK_DIST && !MapTraversor.meetObstacle(map, tileCenter, destination)) {
						vectorMap[x][y] = null;
					} else {
						int north;
						if (y == height - 1 || hasNoHeat(x, y + 1)) {
							north = getHeat(x, y);
						} else {
							north = getHeat(x, y + 1);
						}

						int south;
						if (y == 0 || hasNoHeat(x, y - 1)) {
							south = getHeat(x, y);
						} else {
							south = getHeat(x, y - 1);
						}

						int west;
						if (x == 0 || hasNoHeat(x - 1, y)) {
							west = getHeat(x, y);
						} else {
							west = getHeat(x - 1, y);
						}

						int east;
						if (x == width - 1 || hasNoHeat(x + 1, y)) {
							east = getHeat(x, y);
						} else {
							east = getHeat(x + 1, y);
						}

						int vx;
						int vy;
						vx = west - east;
						vy = south - north;
						vectorMap[x][y] = new Point2D(vx, vy).getNormalized();
					}
				}
			}
		}
	}

	public Point2D getVector(Tile t) {
		return vectorMap[(int) t.getCoord().x][(int) t.getCoord().y];
	}

	public Point2D getVector(Point2D p) {
		Tile t = map.get(p);
		if (getVector(t) != null) {
			return getVector(t);
		}
		return destination.getSubtraction(p).getNormalized();
	}
}
