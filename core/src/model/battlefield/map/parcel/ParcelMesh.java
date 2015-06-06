package model.battlefield.map.parcel;

import exception.TechnicalException;
import geometry.collections.Ring;
import geometry.geom2d.Point2D;
import geometry.geom2d.algorithm.Triangulator;
import geometry.geom3d.MyMesh;
import geometry.geom3d.Point3D;
import geometry.geom3d.Polygon3D;
import geometry.geom3d.Triangle3D;
import geometry.math.Angle;
import geometry.tools.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Cliff.Type;

/**
 * Creates a mesh from a grid of tiles and smooth the normals at each node. ParcelMesh also work with the parcel manager to get neighboring parcels, to smooth
 * the normals at frontiers.
 */
public class ParcelMesh extends MyMesh {

	private Map<Tile, List<Triangle3D>> tiles = new HashMap<>();

	public ParcelMesh() {

	}

	public void add(Tile t) {
		tiles.put(t, new ArrayList<Triangle3D>());
	}

	private List<Triangle3D> getGroundTriangles(Tile t) {
		if (t.e == null || t.n == null) {
			return new ArrayList<>();
		}

		if (tiles.containsKey(t)) {
			if (tiles.get(t).isEmpty()) {
				if (t.hasCliff()) {
					tiles.get(t).addAll(getCliffGrounds(t));
				} else {
					tiles.get(t).addAll(getTileGround(t));
				}
			}
			return tiles.get(t);
		} else {
			for (ParcelMesh n : ParcelManager.getNeighbors(this)) {
				if (n.tiles.containsKey(t)) {
					return n.getGroundTriangles(t);
				}
			}
		}
		throw new TechnicalException("Ground Triangle was not found, this must not happed. It's strange");
	}

	private List<Triangle3D> getTileGround(Tile t) {
		Point3D sw = new Point3D(t.x, t.y, t.getZ());
		Point3D se = new Point3D(t.e.x, t.e.y, t.e.getZ());
		Point3D ne = new Point3D(t.e.n.x, t.e.n.y, t.e.n.getZ());
		Point3D nw = new Point3D(t.n.x, t.n.y, t.n.getZ());

		List<Triangle3D> triangles = new ArrayList<>();
		triangles.add(new Triangle3D(sw, se, ne));
		triangles.add(new Triangle3D(sw, ne, nw));
		return triangles;
	}

	private List<Triangle3D> getCliffGrounds(Tile t) {
		if (t.getLowerCliff().type == Type.Bugged || t.getUpperCliff().type == Type.Bugged) {
			return new ArrayList<Triangle3D>();
		}

		List<Polygon3D> polygons = new ArrayList<>();
		if (t.getLowerCliff().face != null) {
			polygons.add(getGroundPolygon(t, t.getLowerCliff(), t.getLowerCliff().face.getLowerGround()));
		}
		for (Cliff c : t.getCliffs()) {
			if (t.getUpperCliff().face != null) {
				polygons.add(getGroundPolygon(t, c, c.face.getUpperGround()));
			}
		}

		List<Triangle3D> res = new ArrayList<>();
		for (Polygon3D p : polygons) {
			Triangulator triangulator = new Triangulator(p.getTranslation(t.getPos().x + 0.5, t.getPos().y + 0.5, 0));
			res.addAll(triangulator.getTriangles());
		}

		return res;
	}

	private Polygon3D getGroundPolygon(Tile t, Cliff c, Ring<Point3D> groundPoints) {
		Point2D sw = new Point2D(-0.5, -0.5);
		Point2D se = new Point2D(0.5, -0.5);
		Point2D ne = new Point2D(0.5, 0.5);
		Point2D nw = new Point2D(-0.5, 0.5);
		Ring<Point3D> elevatedRing = new Ring<>();
		for (Point3D p : groundPoints) {
			double elevation;
			if (p.get2D().equals(sw)) {
				elevation = getElevation(t, c);
			} else if (p.get2D().equals(se)) {
				elevation = getElevation(t.e, c);
			} else if (p.get2D().equals(ne)) {
				elevation = getElevation(t.n.e, c);
			} else if (p.get2D().equals(nw)) {
				elevation = getElevation(t.n, c);
			} else {
				elevation = c.level * Tile.STAGE_HEIGHT;
			}

			elevatedRing.add(p.getAddition(0, 0, elevation));
		}
		if (elevatedRing.isEmpty()) {
			LogUtil.logger.warning("ground is empty");
		}
		Polygon3D res = null;
		try {
			res = new Polygon3D(elevatedRing);
		} catch (Exception e) {
			LogUtil.logger.info("can't generate cliff ground at " + t + " because " + e);
		}
		return res;

	}

	private double getElevation(Tile t, Cliff c) {
		if (t.getModifiedLevel() > c.level + 1) {
			return (c.level + 1) * Tile.STAGE_HEIGHT;
		} else {
			return t.getZ();
		}
	}

	private List<Triangle3D> getNearbyTriangles(Tile t, model.battlefield.map.Map map) {
		List<Triangle3D> res = new ArrayList<>();
		for (Tile n : map.get9Around(t)) {
			// if(!neib.isCliff())
			res.addAll(getGroundTriangles(n));
		}
		return res;
	}

	public void compute(model.battlefield.map.Map map) {
		double xScale = 1.0 / map.width;
		double yScale = 1.0 / map.height;
		for (Tile tile : tiles.keySet()) {
			for (Triangle3D t : getGroundTriangles(tile)) {
				int index = vertices.size();
				vertices.add(t.a);
				vertices.add(t.b);
				vertices.add(t.c);

				indices.add(index);
				indices.add(index + 1);
				indices.add(index + 2);

				Point3D normal1 = t.normal;
				Point3D normal2 = t.normal;
				Point3D normal3 = t.normal;

				for (Triangle3D n : getNearbyTriangles(tile, map)) {
					List<Point3D> shared = t.getCommonPoints(n);
					if (t.normal.getAngleWith(n.normal) > Angle.RIGHT) {
						continue;
					}
					if (shared.size() == 3) {
						continue;
					}
					if (shared.contains(t.a)) {
						normal1 = normal1.getAddition(n.normal);
					}

					if (shared.contains(t.b)) {
						normal2 = normal2.getAddition(n.normal);
					}

					if (shared.contains(t.c)) {
						normal3 = normal3.getAddition(n.normal);
					}
				}

				if (normal1.isOrigin()) {
					normals.add(t.normal);
				} else {
					normals.add(normal1.getNormalized());
				}

				if (normal2.isOrigin()) {
					normals.add(t.normal);
				} else {
					normals.add(normal2.getNormalized());
				}

				if (normal3.isOrigin()) {
					normals.add(t.normal);
				} else {
					normals.add(normal3.getNormalized());
				}

				textCoord.add(t.a.get2D().getMult(xScale, yScale));
				textCoord.add(t.b.get2D().getMult(xScale, yScale));
				textCoord.add(t.c.get2D().getMult(xScale, yScale));
			}
		}
	}

	public void reset() {
		vertices.clear();
		textCoord.clear();
		normals.clear();
		indices.clear();
		for (Tile t : tiles.keySet()) {
			tiles.get(t).clear();
		}
	}

	public List<Tile> getTiles() {
		List<Tile> res = new ArrayList<>();
		for (Tile t : tiles.keySet()) {
			res.add(t);
		}
		return res;
	}
}
