package model.battlefield.map.parcelling;

import exception.TechnicalException;
import geometry.collections.Ring;
import geometry.geom2d.Point2D;
import geometry.geom2d.algorithm.Triangulator;
import geometry.geom3d.Point3D;
import geometry.geom3d.Polygon3D;
import geometry.geom3d.Triangle3D;
import geometry.math.AngleUtil;
import geometry.structure.grid.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Cliff.Type;

/**
 * Divides the tile based grid into parcels for performance purpose. the objectives : - group tiles for the graphic card to manage less objects, - divide map to
 * prevent the graphic car to draw it entirely at each frame. Other resolutions may offer better results. Resolution may become dynamic. The challenge here is
 * to smooth texture at parcels' frontiers (see ParcelMesh)
 */
public class Parcelling extends Grid<Parcel>{

	private static final Logger logger = Logger.getLogger(Parcelling.class.getName());

	private static int RESOLUTION = 10;

	public Parcelling(Map map) {
		super((int) Math.ceil((double) map.xSize() / RESOLUTION), (int) Math.ceil((double) map.ySize() / RESOLUTION));
		int nbParcel = xSize*ySize;
		for (int i = 0; i < nbParcel; i++) {
			set(i, new Parcel(this, i));
		}

		for (int x = 0; x < map.xSize(); x++) {
			for (int y = 0; y < map.ySize(); y++) {
				get(inParcellingSpace(new Point2D(x, y))).add(map.get(x, y));
			}
		}

		for (Parcel p : getAll()) {
			compute(map, p);
		}
	}

	private static int inParcellingSpace(double valInMapSpace){
		return (int) (valInMapSpace / RESOLUTION);
	}

	private static Point2D inParcellingSpace(Point2D pInMapSpace){
		return new Point2D(inParcellingSpace(pInMapSpace.x), inParcellingSpace(pInMapSpace.y));
	}

	public List<Parcel> getParcelsContaining(List<Tile> tiles) {
		List<Parcel> res = new ArrayList<>();
		for (Tile t : tiles) {
			Parcel container = get(inParcellingSpace(t.getCoord()));
			if (!res.contains(container)) {
				res.add(container);
			}
		}
		return res;
	}

	public List<Parcel> updateParcelsContaining(List<Tile> tiles) {
		Map m = tiles.get(0).getMap();
		List<Parcel> res = getParcelsContaining(tiles);
		for (Parcel p : res) {
			p.reset();
		}
		for (Parcel p : res) {
			compute(m, p);
		}
		return res;
	}
	//
	//	public List<ParcelMesh> getNeighbors(ParcelMesh parcelMesh) {
	//		List<ParcelMesh> res = new ArrayList<>();
	//		int index = meshes.indexOf(parcelMesh);
	//		// TODO: this smells like a switch-case command
	//		if (index + 1 < meshes.size()) {
	//			res.add(meshes.get(index + 1));
	//		}
	//
	//		if (index + widthJump - 1 < meshes.size()) {
	//			res.add(meshes.get(index + widthJump - 1));
	//		}
	//		if (index + widthJump < meshes.size()) {
	//			res.add(meshes.get(index + widthJump));
	//		}
	//		if (index + widthJump + 1 < meshes.size()) {
	//			res.add(meshes.get(index + widthJump + 1));
	//		}
	//
	//		if (index - 1 >= 0) {
	//			res.add(meshes.get(index - 1));
	//		}
	//
	//		if (index - widthJump - 1 >= 0) {
	//			res.add(meshes.get(index - widthJump - 1));
	//		}
	//		if (index - widthJump >= 0) {
	//			res.add(meshes.get(index - widthJump));
	//		}
	//		if (index - widthJump + 1 >= 0) {
	//			res.add(meshes.get(index - widthJump + 1));
	//		}
	//
	//		return res;
	//	}

	private List<Triangle3D> getGroundTriangles(Tile t, Parcel parcel) {
		if (t.e() == null || t.n() == null) {
			return new ArrayList<>();
		}

		if (parcel.triangles.containsKey(t)) {
			if (parcel.triangles.get(t).isEmpty()) {
				if (t.hasCliff()) {
					parcel.triangles.get(t).addAll(getCliffGrounds(t));
				} else {
					parcel.triangles.get(t).addAll(getTileGround(t));
				}
			}
			return parcel.triangles.get(t);
		}
		for (Parcel n : get8Around(parcel)) {
			for (Tile tile : n.triangles.keySet()) {
				if (tile.equals(t)) {
					return getGroundTriangles(t, n);
				}
			}
		}
		throw new TechnicalException("Ground Triangle was not found, this must not happen. tile : "+t.getCoord());
	}

	private List<Triangle3D> getTileGround(Tile t) {
		Point3D sw = t.getPos();
		Point3D se = t.e().getPos();
		Point3D ne = t.e().n().getPos();
		Point3D nw = t.n().getPos();

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
			// TODO: smell like switch case
			if (p.get2D().equals(sw)) {
				elevation = getElevation(t, c);
			} else if (p.get2D().equals(se)) {
				elevation = getElevation(t.e(), c);
			} else if (p.get2D().equals(ne)) {
				elevation = getElevation(t.n().e(), c);
			} else if (p.get2D().equals(nw)) {
				elevation = getElevation(t.n(), c);
			} else {
				elevation = c.level * Tile.STAGE_HEIGHT;
			}

			elevatedRing.add(p.getAddition(0, 0, elevation));
		}
		if (elevatedRing.isEmpty()) {
			logger.warning("ground is empty");
		}
		Polygon3D res = null;
		try {
			res = new Polygon3D(elevatedRing);
		} catch (Exception e) {
			logger.info("can't generate cliff ground at " + t + " because " + e);
		}
		return res;

	}

	private double getElevation(Tile t, Cliff c) {
		if (t.getModifiedLevel() > c.level + 1) {
			return (c.level + 1) * Tile.STAGE_HEIGHT;
		}
		return t.getElevation();
	}

	public List<Triangle3D> getNearbyTriangles(Tile t, Map map, Parcel parcel) {
		List<Triangle3D> res = new ArrayList<>();
		for (Tile n : map.get9Around(t)) {
			// if(!neib.isCliff())
			res.addAll(getGroundTriangles(n, parcel));
		}
		return res;
	}

	public void compute(Map map, Parcel parcel) {
		double xScale = 1.0 / map.xSize();
		double yScale = 1.0 / map.ySize();
		for (Tile tile : parcel.getTiles()) {
			for (Triangle3D t : getGroundTriangles(tile, parcel)) {
				int index = parcel.getMesh().vertices.size();
				parcel.getMesh().vertices.add(t.a);
				parcel.getMesh().vertices.add(t.b);
				parcel.getMesh().vertices.add(t.c);

				parcel.getMesh().indices.add(index);
				parcel.getMesh().indices.add(index + 1);
				parcel.getMesh().indices.add(index + 2);

				Point3D normal1 = t.normal;
				Point3D normal2 = t.normal;
				Point3D normal3 = t.normal;

				for (Triangle3D n : getNearbyTriangles(tile, map, parcel)) {
					List<Point3D> shared = t.getCommonPoints(n);
					if (t.normal.getAngleWith(n.normal) > AngleUtil.RIGHT) {
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
					parcel.getMesh().normals.add(t.normal);
				} else {
					parcel.getMesh().normals.add(normal1.getNormalized());
				}

				if (normal2.isOrigin()) {
					parcel.getMesh().normals.add(t.normal);
				} else {
					parcel.getMesh().normals.add(normal2.getNormalized());
				}

				if (normal3.isOrigin()) {
					parcel.getMesh().normals.add(t.normal);
				} else {
					parcel.getMesh().normals.add(normal3.getNormalized());
				}

				parcel.getMesh().textCoord.add(t.a.get2D().getMult(xScale, yScale));
				parcel.getMesh().textCoord.add(t.b.get2D().getMult(xScale, yScale));
				parcel.getMesh().textCoord.add(t.c.get2D().getMult(xScale, yScale));
			}
		}
	}

}
