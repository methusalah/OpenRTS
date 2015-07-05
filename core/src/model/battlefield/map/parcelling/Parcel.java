package model.battlefield.map.parcelling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.battlefield.map.Tile;
import geometry.geom3d.MyMesh;
import geometry.geom3d.Triangle3D;
import geometry.structure.grid.Node;

public class Parcel extends Node {

	Map<Tile, List<Triangle3D>> tiles = new HashMap<Tile, List<Triangle3D>>();

	private MyMesh mesh;
	
	public Parcel(ParcelGrid grid, int index) {
		super(grid, index);
		// TODO Auto-generated constructor stub
	}

	public void add(Tile t) {
		tiles.put(t, new ArrayList<Triangle3D>());
	}

	public List<Tile> getTiles() {
		List<Tile> res = new ArrayList<>();
		for (Tile t : tiles.keySet()) {
			res.add(t);
		}
		return res;
	}

	public void reset() {
		mesh.vertices.clear();
		mesh.textCoord.clear();
		mesh.normals.clear();
		mesh.indices.clear();
		for (Tile t : tiles.keySet()) {
			tiles.get(t).clear();
		}
	}
}
