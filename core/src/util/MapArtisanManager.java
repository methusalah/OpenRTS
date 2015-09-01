package util;

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
import brainless.openrts.event.EventManager;
import brainless.openrts.event.client.ParcelUpdateEvent;

import com.google.inject.Inject;

public class MapArtisanManager {
	
	@Inject
	private BuilderManager builderManager;
	
	@Inject
	private TileArtisanManager tileArtisanManager;
	
	@Inject
	public MapArtisanManager() {
		
	}
	
	public void buildMap(Battlefield b){
		if(b.getMap() == null) {
			createMapOn(b);
		} else {
			finalizeMapOn(b);
		}
	}


	private void createMapOn(Battlefield b){
		Map m = new Map(builderManager.getMapStyleBuilder("StdMapStyle").build());
		AtlasArtisanUtil.buildAtlas(m);
		b.setMap(m);
	}

	private void finalizeMapOn(Battlefield b){
		Map m = b.getMap();
		m.setStyle(builderManager.getMapStyleBuilder(m.getMapStyleID()).build());
		tileArtisanManager.finalizeTilesOn(m);
		tileArtisanManager.readElevation(m.getAll());
		m.setParcelling(new Parcelling(m));

		attachInitialTrinkets(m);
		for(Ramp r : m.getRamps()) {
			r.connect(m);
		}
		AtlasArtisanUtil.buildAtlas(m);
	}

	public void attachInitialTrinkets(Map m) {
		m.getTrinkets().clear();
		for (TrinketMemento memento : m.getInitialTrinkets()) {
			attachTrinket(memento.getTrinket(builderManager), m);
		}
	}

	public void attachTrinket(Trinket t, Map m){
		m.addTrinket(t);
		Tile containerTile = m.get(t.getCoord());
		containerTile.addData(t);
		tileArtisanManager.checkBlockingTrinkets(containerTile);
	}

	public void dettachTrinket(Trinket t, Map m){
		m.removeTrinket(t);
		Tile containerTile = m.get(t.getCoord());
		containerTile.removeData(t);
		tileArtisanManager.checkBlockingTrinkets(containerTile);
	}

	public void act(Map m){
		for(Trinket t : m.getTrinkets()) {
			t.drawOnBattlefield();
		}
	}

	public void updateParcelsFor(List<Tile> tiles) {
		Map m = tiles.get(0).getMap();
		List<Tile> extended = tileArtisanManager.getExtendedZone(tiles);

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
	
	public void cleanSowing(Map m, Point2D coord, double radius){
		for(Tile tile : m.getInCircle(coord, radius*2))
			for(Trinket trinket : tile.getData(Trinket.class))
				if(trinket.sowed){
					dettachTrinket(trinket, m);
					trinket.removeFromBattlefield();
				}
	}
}
