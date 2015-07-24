package model.builders;

import event.EventManager;
import event.ParcelUpdateEvent;
import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;

import java.util.List;

import model.battlefield.Battlefield;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;
import model.battlefield.map.TrinketMemento;
import model.battlefield.map.cliff.Ramp;
import model.battlefield.map.parcelling.Parcel;
import model.battlefield.map.parcelling.Parcelling;
import model.builders.entity.definitions.BuilderManager;

public abstract class MapArtisanUtil {
	public static void buildMap(Battlefield b){
		if(b.getMap() == null) {
			createMapOn(b);
		} else {
			finalizeMapOn(b);
		}
	}


	private static void createMapOn(Battlefield b){
		Map m = new Map(BuilderManager.getMapStyleBuilder("StdMapStyle").build());
		AtlasArtisanUtil.buildAtlas(m);
		b.setMap(m);
	}

	private static void finalizeMapOn(Battlefield b){
		Map m = b.getMap();
		m.setStyle(BuilderManager.getMapStyleBuilder(m.getMapStyleID()).build());
		TileArtisanUtil.finalizeTilesOn(m);
		TileArtisanUtil.readElevation(m.getAll());
		m.setParcelling(new Parcelling(m));

		attachInitialTrinkets(m);
		for(Ramp r : m.getRamps()) {
			r.connect(m);
		}
		AtlasArtisanUtil.buildAtlas(m);
	}

	public static void attachInitialTrinkets(Map m) {
		m.getTrinkets().clear();
		for (TrinketMemento memento : m.getInitialTrinkets()) {
			attachTrinket(memento.getTrinket(), m);
		}
	}

	public static void attachTrinket(Trinket t, Map m){
		m.addTrinket(t);
		Tile containerTile = m.get(t.getCoord());
		containerTile.addData(t);
		TileArtisanUtil.checkBlockingTrinkets(containerTile);
	}

	public static void dettachTrinket(Trinket t, Map m){
		m.removeTrinket(t);
		Tile containerTile = m.get(t.getCoord());
		containerTile.removeData(t);
		TileArtisanUtil.checkBlockingTrinkets(containerTile);
	}

	public static void act(Map m){
		for(Trinket t : m.getTrinkets()) {
			t.drawOnBattlefield();
		}
	}

	public static void updateParcelsFor(List<Tile> tiles) {
		Map m = tiles.get(0).getMap();
		List<Tile> extended = TileArtisanUtil.getExtendedZone(tiles);

		for (Tile t : extended) {
			for (Object o : t.storedData) {
				if(o instanceof FieldComp){
					FieldComp fc = (FieldComp)o;
					fc.setPos(new Point3D(fc.getPos().x,
							fc.getPos().y,
							t.getMap().getAltitudeAt(fc.getCoord())));
				}
			}
		}
		List<Parcel> toUpdate = m.getParcelling().updateParcelsContaining(extended);
		EventManager.post(new ParcelUpdateEvent(toUpdate));
	}
	
	public static void cleanSowing(Map m, Point2D coord, double radius){
		for(Tile tile : m.getInCircle(coord, radius*2))
			for(Trinket trinket : tile.getData(Trinket.class))
				if(trinket.sowed){
					dettachTrinket(trinket, m);
					trinket.removeFromBattlefield();
				}
	}
}
