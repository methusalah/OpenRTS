package model.builders;

import event.EventManager;
import event.ParcelUpdateEvent;
import geometry.geom3d.Point3D;

import java.util.List;

import model.ModelManager;
import model.battlefield.Battlefield;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;
import model.battlefield.map.TrinketMemento;
import model.battlefield.map.cliff.Ramp;
import model.battlefield.map.parcelling.ParcelGrid;
import model.battlefield.map.parcelling.ParcelMesh;
import model.builders.entity.definitions.BuilderManager;

public class MapArtisan {
	public static void buildMap(Battlefield b){
		if(b.getMap() == null)
			createMapOn(b);
		else
			finalizeMapOn(b);
	}
	
	
	private static void createMapOn(Battlefield b){
		Map m = new Map(BuilderManager.getMapStyleBuilder("StdMapStyle").build());
		AtlasArtisan.buildAtlas(m);
		b.setMap(m);
	}

	private static void finalizeMapOn(Battlefield b){
		Map m = b.getMap();
		m.setStyle(BuilderManager.getMapStyleBuilder(m.getMapStyleID()).build());
		TileArtisan.finalizeTilesOn(m);
		
		attachInitialTrinkets(m);
		for(Ramp r : m.getRamps())
			r.connect(m);
		AtlasArtisan.buildAtlas(m);
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
		TileArtisan.checkBlockingTrinkets(containerTile);
	}

	public static void dettachTrinket(Trinket t, Map m){
		m.removeTrinket(t);
		Tile containerTile = m.get(t.getCoord());
		containerTile.removeData(t);
		TileArtisan.checkBlockingTrinkets(containerTile);
	}
	
	public static void act(Map m){
		for(Trinket t : m.getTrinkets())
			t.drawOnBattlefield();
	}

	public static void updateParcelsFor(List<Tile> tiles) {
		List<Tile> extended = TileArtisan.getExtendedZone(tiles);

		for (Tile t : extended) {
			for (Object o : t.storedData) {
				if(o instanceof FieldComp){
					FieldComp fc = (FieldComp)o;
					fc.setPos(new Point3D(fc.getPos().x,
							fc.getPos().y,
							t.getMap().getAltitudeAt(fc.getPos().get2D())));
				}
			}
		}
		List<ParcelMesh> toUpdate = ParcelGrid.updateParcelsFor(extended);
		EventManager.post(new ParcelUpdateEvent(toUpdate));
	}
	
	
}
