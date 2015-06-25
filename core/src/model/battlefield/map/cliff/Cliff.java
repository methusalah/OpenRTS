package model.battlefield.map.cliff;

import geometry.structure.grid.Grid;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;
import model.battlefield.map.cliff.faces.Face;

/**
 * Connect to each other to create a path of cliffs and compute correct shape. This is a structuring class creating relationship. The shape is determined by the
 * CliffOrganizer class. The graphical representation is computed by the Face class. "Bugged" type is used to accept zero-solution configuration that may appear
 * while editing the map. In this case, the view draw an error flag to warn the user.
 */
public class Cliff {
	public enum Type {
		Orthogonal, Salient, Corner, Border, Bugged
	}

	public Type type;

	public Face face;
	public List<Trinket> trinkets = new ArrayList<>();

	private final Tile tile;
	public final int level;
	private Tile parentTile;
	private Tile childTile;
	public double angle = 0;

	public Cliff(Tile t, int level) {
		this.tile = t;
		this.level = level;
	}

	public void connect(Map map) {
		CliffOrganizer.organize(this, map);
	}

	public String getConnexionConfiguration(Map map) {
		String res = new String();
		if (isNeighborCliff(tile.n(), map)) {
			res = res.concat("n");
		}
		if (isNeighborCliff(tile.s(), map)) {
			res = res.concat("s");
		}
		if (isNeighborCliff(tile.e(), map)) {
			res = res.concat("e");
		}
		if (isNeighborCliff(tile.w(), map)) {
			res = res.concat("w");
		}
		return res;
	}

	private boolean isNeighborCliff(Tile t, Map map) {
		if (t == null || !t.hasCliffOnLevel(level) ||
				// t.level != tile.level ||
				t.getCliff(level).type == Type.Bugged) {
			return false;
		}

		for (Tile n1 : getUpperGrounds(map)) {
			for (Tile n2 : t.getCliff(level).getUpperGrounds(map)) {
				if (n1 == n2) {
					return true;
				}
			}
		}
		return false;
	}

	public void link(Tile parent, Tile child) {
		this.parentTile = parent;
		if (parent != null) {
			getParent().childTile = tile;
		}

		this.childTile = child;
		if (child != null) {
			getChild().parentTile = tile;
		}
	}

	public ArrayList<Tile> getUpperGrounds(Map map) {
		ArrayList<Tile> res = new ArrayList<>();
		for (Tile n : map.get8Around(tile)) {
			if (n.level > tile.level) {
				res.add(n);
			}
		}
		return res;
	}

	public void removeFromBattlefield() {
		for (Trinket t : trinkets) {
			t.removeFromBattlefield();
		}
		if (parentTile != null && parentTile.getCliff(level) != null) {
			getParent().childTile = null;
		}
		if (childTile != null && childTile.getCliff(level) != null) {
			getChild().parentTile = null;
		}
	}

	public Cliff getParent() {
		if (parentTile == null) {
			return null;
		}
		return parentTile.getCliff(level);
	}

	public Cliff getChild() {
		if (childTile == null) {
			return null;
		}
		return childTile.getCliff(level);
	}

	public boolean hasParent() {
		return parentTile != null;
	}

	public boolean hasChild() {
		return childTile != null;
	}

	public Tile getTile() {
		return tile;
	}

}
