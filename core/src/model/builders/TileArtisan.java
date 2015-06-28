package model.builders;

import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;

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
}
