package model.battlefield.map.parcel;

import exception.TechnicalException;
import geometry.collections.Ring;
import geometry.geom2d.Point2D;
import geometry.geom2d.algorithm.Triangulator;
import geometry.geom3d.Point3D;
import geometry.geom3d.Polygon3D;
import geometry.geom3d.Triangle3D;
import geometry.math.Angle;
import geometry.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Cliff.Type;

/**
 * Divides the tile based grid into parcels for performance purpose. the objectives : - group tiles for the graphic card to manage less objects, - divide map to
 * prevent the graphic car to draw it entirely at each frame. Other resolutions may offer better results. Resolution may become dynamic. The challenge here is
 * to smooth texture at parcels' frontiers (see ParcelMesh)
 */
public class ParcelManager {

	private static final int RESOLUTION = 10;

	private static List<ParcelMesh> meshes = new ArrayList<>();
	private static int WIDTHJUMP;

	private ParcelManager() {
	}

	public static void createParcelMeshes(Map map) {
		WIDTHJUMP = (int) (Math.ceil((double) map.width / RESOLUTION));
		int nbParcel = WIDTHJUMP * (int) Math.ceil((double) map.height / RESOLUTION);
		for (int i = 0; i < nbParcel; i++) {
			meshes.add(new ParcelMesh());
		}

		for (int i = 0; i < map.width; i++) {
			for (int j = 0; j < map.height; j++) {
				int index = (int) (Math.floor(j / RESOLUTION) * WIDTHJUMP + Math.floor(i / RESOLUTION));
				meshes.get(index).add(map.getTile(i, j));
			}
		}

		for (ParcelMesh mesh : meshes) {
			compute(map, mesh);
		}
	}

	public static List<ParcelMesh> getParcelsFor(List<Tile> tiles) {
		List<ParcelMesh> res = new ArrayList<>();
		for (Tile t : tiles) {
			int index = (int) (Math.floor((t.y) / RESOLUTION) * WIDTHJUMP + Math.floor((t.x) / RESOLUTION));
			if (!res.contains(meshes.get(index))) {
				res.add(meshes.get(index));
			}
		}
		return res;
	}

	public static List<ParcelMesh> updateParcelsFor(List<Tile> tiles) {
		List<ParcelMesh> meshes = getParcelsFor(tiles);
		for (ParcelMesh mesh : meshes) {
			mesh.reset();
		}
		Map map = ModelManager.getBattlefield().getMap();
		for (ParcelMesh mesh : meshes) {
			compute(map, mesh);
		}
		return meshes;
	}

	public static List<ParcelMesh> getNeighbors(ParcelMesh parcelMesh) {
		List<ParcelMesh> res = new ArrayList<>();
		int index = meshes.indexOf(parcelMesh);
		// TODO: this smells like a switch-case command
		if (index + 1 < meshes.size()) {
			res.add(meshes.get(index + 1));
		}

		if (index + WIDTHJUMP - 1 < meshes.size()) {
			res.add(meshes.get(index + WIDTHJUMP - 1));
		}
		if (index + WIDTHJUMP < meshes.size()) {
			res.add(meshes.get(index + WIDTHJUMP));
		}
		if (index + WIDTHJUMP + 1 < meshes.size()) {
			res.add(meshes.get(index + WIDTHJUMP + 1));
		}

		if (index - 1 >= 0) {
			res.add(meshes.get(index - 1));
		}

		if (index - WIDTHJUMP - 1 >= 0) {
			res.add(meshes.get(index - WIDTHJUMP - 1));
		}
		if (index - WIDTHJUMP >= 0) {
			res.add(meshes.get(index - WIDTHJUMP));
		}
		if (index - WIDTHJUMP + 1 >= 0) {
			res.add(meshes.get(index - WIDTHJUMP + 1));
		}

		return res;
	}

	public static List<ParcelMesh> getMeshes() {
		return meshes;
	}

	private static Tile findTile(int x, int y) {
		for (ParcelMesh parcelMesh : meshes) {
			for (Tile tile : parcelMesh.getTiles()) {
				if (tile.x == x && tile.y == y) {
					return tile;
				}
			}
		}
		return null;
	}

	private static List<Triangle3D> getGroundTriangles(Tile t, ParcelMesh mesh) {
		if (t.e == null || t.n == null) {
			return new ArrayList<>();
		}

		if (mesh.tiles.containsKey(t)) {
			if (mesh.tiles.get(t).isEmpty()) {
				if (t.hasCliff()) {
					mesh.tiles.get(t).addAll(getCliffGrounds(t));
				} else {
					mesh.tiles.get(t).addAll(getTileGround(t));
				}
			}
			return mesh.tiles.get(t);
		}
		for (ParcelMesh n : getNeighbors(mesh)) {
			for (Tile tile : n.tiles.keySet()) {
				if (tile.equals(t)) {
					return getGroundTriangles(t, n);
				}
			}
			// if (n.tiles.containsKey(t)) {
			// return n.getGroundTriangles(t);
			// }
		}

		throw new TechnicalException("Ground Triangle was not found, this must not happed. It's strange");
	}

	private static List<Triangle3D> getTileGround(Tile t) {
		Point3D sw = new Point3D(t.x, t.y, t.getZ());
		Point3D se = new Point3D(t.e.x, t.e.y, t.e.getZ());
		Point3D ne = new Point3D(t.e.n.x, t.e.n.y, t.e.n.getZ());
		Point3D nw = new Point3D(t.n.x, t.n.y, t.n.getZ());

		List<Triangle3D> triangles = new ArrayList<>();
		triangles.add(new Triangle3D(sw, se, ne));
		triangles.add(new Triangle3D(sw, ne, nw));
		return triangles;
	}

	private static List<Triangle3D> getCliffGrounds(Tile t) {
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

	private static Polygon3D getGroundPolygon(Tile t, Cliff c, Ring<Point3D> groundPoints) {
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

	private static double getElevation(Tile t, Cliff c) {
		if (t.getModifiedLevel() > c.level + 1) {
			return (c.level + 1) * Tile.STAGE_HEIGHT;
		}
		return t.getZ();
	}

	public static List<Triangle3D> getNearbyTriangles(Tile t, model.battlefield.map.Map map, ParcelMesh mesh) {
		List<Triangle3D> res = new ArrayList<>();
		for (Tile n : map.get9Around(t)) {
			// if(!neib.isCliff())
			res.addAll(getGroundTriangles(n, mesh));
		}
		return res;
	}

	public static void compute(model.battlefield.map.Map map, ParcelMesh mesh) {
		double xScale = 1.0 / map.width;
		double yScale = 1.0 / map.height;
		for (Tile tile : mesh.getTiles()) {
			for (Triangle3D t : getGroundTriangles(tile, mesh)) {
				int index = mesh.vertices.size();
				mesh.vertices.add(t.a);
				mesh.vertices.add(t.b);
				mesh.vertices.add(t.c);

				mesh.indices.add(index);
				mesh.indices.add(index + 1);
				mesh.indices.add(index + 2);

				Point3D normal1 = t.normal;
				Point3D normal2 = t.normal;
				Point3D normal3 = t.normal;

				for (Triangle3D n : getNearbyTriangles(tile, map, mesh)) {
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
					mesh.normals.add(t.normal);
				} else {
					mesh.normals.add(normal1.getNormalized());
				}

				if (normal2.isOrigin()) {
					mesh.normals.add(t.normal);
				} else {
					mesh.normals.add(normal2.getNormalized());
				}

				if (normal3.isOrigin()) {
					mesh.normals.add(t.normal);
				} else {
					mesh.normals.add(normal3.getNormalized());
				}

				mesh.textCoord.add(t.a.get2D().getMult(xScale, yScale));
				mesh.textCoord.add(t.b.get2D().getMult(xScale, yScale));
				mesh.textCoord.add(t.c.get2D().getMult(xScale, yScale));
			}
		}
	}

}
