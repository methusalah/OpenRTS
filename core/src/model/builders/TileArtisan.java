package model.builders;

import java.util.ArrayList;
import java.util.List;

import event.EventManager;
import event.TilesEvent;
import model.ModelManager;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;
import model.battlefield.map.cliff.Cliff;

public class TileArtisan {

	public static void finalizeTilesOn(Map m){
		for(Tile t : m.getTiles())
			t.setMap(m);
	}
	
	public static void checkBlockingTrinkets(Tile tile){
		for (Tile n : tile.getMap().get9Around(tile)) {
			for(Trinket t : tile.getData(Trinket.class))
				if (n.getCenter().getDistance(t.getCoord()) < t.getRadius()) {
					n.hasBlockingTrinket = true;
				}
		}
	}
	
	public static void updatesElevation(List<Tile> tiles, int clifShapeBuilderIndex){
		List<Tile> extended = getExtendedZone(tiles);

		for (Tile t : extended) {
			int minLevel = t.level;
			int maxLevel = t.level;
			for (Tile n : t.getMap().get8Around(t)) {
				maxLevel = Math.max(maxLevel, n.level);
			}
			if (t.hasCliff()) {
				t.unsetCliff();
			}

			if (minLevel != maxLevel) {
				t.setCliff(minLevel, maxLevel);
			}
		}

		for (Tile t : extended) {
			for (Cliff c : t.getCliffs()) {
				c.connect(t.getMap());
			}
		}
		for (Tile t : extended) {
			for (Cliff c : t.getCliffs()) {
				t.getMap().getStyle().cliffShapeBuilders.get(clifShapeBuilderIndex).build(c);
			}
		}
		EventManager.post(new TilesEvent(extended));
		MapArtisan.updateParcelsFor(tiles);
	}
	
	public static List<Tile> getExtendedZone(List<Tile> tiles) {
		List<Tile> res = new ArrayList<>();
		res.addAll(tiles);
		for (Tile t : tiles) {
			for (Tile n : t.getMap().get8Around(t)) {
				if (!res.contains(n)) {
					res.add(n);
				}
			}
		}
		return res;
	}
}
