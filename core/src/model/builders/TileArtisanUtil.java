package model.builders;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;
import model.battlefield.map.cliff.Cliff;
import model.builders.entity.definitions.BuilderManager;
import event.EventManager;
import event.TilesEvent;

public abstract class TileArtisanUtil {

	public static void finalizeTilesOn(Map m){
		for(Tile t : m.getTiles()) {
			t.setMap(m);
		}
	}

	public static void checkBlockingTrinkets(Tile tile){
		for (Tile n : tile.getMap().get9Around(tile)) {
			for(Trinket t : tile.getData(Trinket.class)) {
				if(t.blocking && n.getCenter().getDistance(t.getCoord()) < t.getRadius()) {
					n.hasBlockingTrinket = true;
				}
			}
		}
	}

	public static void changeLevel(List<Tile> tiles, int level, String cliffShapeBuilderID) {
		List<Tile> toUpdate = new ArrayList<>();
		for (Tile t : tiles) {
			t.level = level;
			t.setCliffShapeID(cliffShapeBuilderID);
			if (t.ramp != null) {
				toUpdate.addAll(t.ramp.destroy());
			}
		}
		tiles.addAll(toUpdate);

		// setting the cliff shape build ID to neighbors if they have none
		List<Tile> extended = getExtendedZone(tiles);
		extended.removeAll(tiles);
		for(Tile t : extended) {
			if(t.getCliffShapeID().isEmpty()) {
				t.setCliffShapeID(cliffShapeBuilderID);
			}
		}

		TileArtisanUtil.updatesElevation(tiles);
	}

	public static void updatesElevation(List<Tile> tiles){
		readElevation(tiles);
		EventManager.post(new TilesEvent(getExtendedZone(tiles)));
		MapArtisanUtil.updateParcelsFor(tiles);
	}

	public static void readElevation(List<Tile> tiles){
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
				BuilderManager.getCliffShapeBuilder(t.getCliffShapeID()).build(c);
			}
		}
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
