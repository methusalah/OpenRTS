package model.battlefield.map;

import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.BoundingShape;
import geometry.geom2d.Point2D;
import geometry.structure.grid.Node;
import geometry.structure.grid3D.Grid3D;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.atlas.Atlas;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains everything to set up a terrain and explore it. Map is mainly : - a tile based grid with relief and cliffs - a texture atlas to paint on the ground -
 * a list of trinkets Also contains methods and fields dedicated to serialization/deserialization.
 */
public final class Map extends Grid3D<Tile> {

	@JsonIgnore
	public MapStyle style = new MapStyle();

	@JsonProperty
	public String mapStyleID;

	@JsonProperty
	public List<Ramp> ramps = new ArrayList<>();
	
	@JsonProperty
	public List<TrinketMemento> serializableTrinkets = new ArrayList<>();

	@JsonIgnore
	public List<Trinket> trinkets = new ArrayList<>();

	@JsonProperty
	public Atlas atlas, cover;

	public Map(int width, int height) {
		super(width, height);
		atlas = new Atlas(width, height);
		atlas.finalize();
		cover = new Atlas(width, height);
		cover.finalize();
	}

	public boolean isBlocked(int x, int y) {
		return (get(x, y)).isBlocked() ? true : false;
	}

	public boolean isWalkable(Point2D p) {
		return  !isInBounds(p) || get(p).isBlocked() ? false : true;
	}

	public void saveTrinkets() {
		serializableTrinkets.clear();
		for (Trinket t : trinkets) {
			serializableTrinkets.add(new TrinketMemento(t));
		}
	}

	public void resetTrinkets() {
		trinkets.clear();
		for (TrinketMemento st : serializableTrinkets) {
			Trinket t = st.getTrinket();
			trinkets.add(t);
			t.drawOnBattlefield();
		}
	}

	public BoundingShape getBoundsOf(Tile t) {
		ArrayList<Point2D> points = new ArrayList<>();
		points.add(t.getCoord());
		points.add(t.getCoord().getAddition(1, 0));
		points.add(t.getCoord().getAddition(1, 1));
		points.add(t.getCoord().getAddition(0, 1));
		return new AlignedBoundingBox(points);
	}

	
	public void prepareForBattle() {
		for (Node n : getAll()) {
			((Tile)n).hasBlockingTrinket = false;
		}
		for (Trinket trinket : trinkets) {
			if (trinket.getRadius() != 0) {
				for (Tile n : get9Around(get(trinket.getCoord()))) {
					if (n.getCoord().getAddition(0.5, 0.5).getDistance(trinket.getCoord()) < trinket.getRadius() + 0.3) {
						n.hasBlockingTrinket = true;
					}
				}
			}
		}
	}

	public List<Ramp> getRamps() {
		return ramps;
	}
}
