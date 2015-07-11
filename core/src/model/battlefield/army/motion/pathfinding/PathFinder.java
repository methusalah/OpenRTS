package model.battlefield.army.motion.pathfinding;

import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Map;
import model.battlefield.map.MapTraversor;

/**
 * This PathFinder works with A* algorithm.
 *
 * It is unused in OpenRTS at this time but may be usefull for local blockades.
 */
public class PathFinder {
	Map map;
	private Node[][] nodes;
	private int tx, ty, sx, sy;

	List<Node> closed = new ArrayList<Node>();
	List<Node> open = new ArrayList<Node>();

	public PathFinder(Map map) {
		this.map = map;
	}

	public Path findPath(Point2D start, Point2D dest) {
		this.tx = (int)Math.floor(dest.x);
		this.ty = (int)Math.floor(dest.y);
		this.sx = (int)Math.floor(start.x);
		this.sy = (int)Math.floor(start.y);

		if(map.isBlocked(tx, ty)) {
			return new Path();
		}

		Path res;
		initialize();
		searchPath();
		res = buildPath(start, dest);
		res = getSimplified(res);
		return res;
	}

	private void initialize() {
		nodes = new Node[map.xSize()][map.ySize()];
		for(int x=0; x<map.xSize(); x++) {
			for(int y=0; y<map.ySize(); y++) {
				nodes[x][y] = new Node(x, y);
			}
		}

		nodes[sx][sy].cost = 0;
		nodes[sx][sy].depth = 0;
		nodes[tx][ty].parent = null;
		closed.clear();
		open.clear();
		open.add(nodes[sx][sy]);
	}

	private void searchPath(){
		int maxDepth = 0;
		while (!open.isEmpty() && maxDepth<2000) {
			Node n = open.get(0);
			if(n == nodes[tx][ty]) {
				break;
			}
			open.remove(n);
			closed.add(n);

			for(int x=-1;x<2;x++) {
				for(int y=-1;y<2;y++){
					if(x==0 && y==0) {
						continue;
					}
					int xp = x + n.x;
					int yp = y + n.y;

					if(isValidLocation(xp, yp)) {
						double nextCost = n.cost + getCost(xp, yp, n.x, n.y);
						Node neighbour = nodes[xp][yp];
						if (nextCost < neighbour.cost) {
							open.remove(neighbour);
							closed.remove(neighbour);
						}

						if (!open.contains(neighbour) && !closed.contains(neighbour)) {
							neighbour.cost = nextCost;
							neighbour.setParent(n);
							maxDepth = Math.max(maxDepth, neighbour.depth);
							neighbour.setHeuristic();
							open.add(neighbour);
						}
					}
				}
			}
		}
	}

	private Path buildPath(Point2D start, Point2D dest) {
		Path res = new Path();
		if (nodes[tx][ty].parent == null) {
			return res;
		}


		res.add(0, new Point2D(dest.x, dest.y));

		Node target = nodes[tx][ty].parent;
		while (target != nodes[sx][sy]) {
			res.add(0, new Point2D(target.x+0.5, target.y+0.5));
			target = target.parent;
		}
		res.add(0, new Point2D(start.x, start.y));

		return res;
	}

	private boolean isValidLocation(int x, int y) {
		return x>=0 && y>=0 &&
				x<map.xSize() && y<map.ySize() &&
				!(x==sx && y==sy) &&
				!map.isBlocked(x, y);
	}

	private double getCost(int x, int y, int x0, int y0) {
		Point2D a = new Point2D(x, y);
		Point2D b = new Point2D(x0, y0);
		return a.getDistance(b);
	}

	private Path getSimplified(Path path) {
		if(path.isEmpty()) {
			return path;
		}
		Path res = new Path();
		res.add(path.getFirstWaypoint());
		path.discardFirstWaypoint();
		for(Point2D p : path) {
			Point2D lastInRes = res.getLastWaypoint();
			if(path.indexOf(p) == 0) {
				continue;
			}
			if(MapTraversor.meetObstacle(map, lastInRes, p)) {
				//                    m.getTile(p).n.isCliff() ||
				//                    m.getTile(p).s.isCliff() ||
				//                    m.getTile(p).e.isCliff() ||
				//                    m.getTile(p).w.isCliff())
				res.add(path.get(path.indexOf(p)-1));
			}
		}
		res.add(path.getLastWaypoint());

		return res;
	}

	private class Node {

		Node parent = null;
		double cost = 0;
		int depth = 0;
		int x;
		int y;
		double heuristic = 0;

		private Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		private void setHeuristic() {
			Point2D pos = new Point2D(x, y);
			Point2D target = new Point2D(tx, ty);
			heuristic = pos.getDistance(target);
		}

		private void setParent(Node parent) {
			this.parent = parent;
			depth = parent.depth+1;
		}

	}
}
