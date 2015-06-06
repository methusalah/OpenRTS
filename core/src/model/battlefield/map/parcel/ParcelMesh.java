package model.battlefield.map.parcel;

import geometry.geom3d.MyMesh;
import geometry.geom3d.Triangle3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.battlefield.map.Tile;

/**
 * Creates a mesh from a grid of tiles and smooth the normals at each node. ParcelMesh also work with the parcel manager to get neighboring parcels, to smooth
 * the normals at frontiers. A parcel mesh is an extended mesh with triangulation, textures coordinates and normals (smoothed)
 */
public class ParcelMesh extends MyMesh {

	Map<Tile, List<Triangle3D>> tiles = new HashMap<Tile, List<Triangle3D>>();
	// TODO: fill it and use this => see like in Tile
	protected ParcelMesh north;
	protected ParcelMesh east;
	protected ParcelMesh south;
	protected ParcelMesh west;

	public ParcelMesh() {
	}

	public void add(Tile t) {
		tiles.put(t, new ArrayList<Triangle3D>());
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
