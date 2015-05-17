package model.battlefield;

import model.battlefield.actors.ActorPool;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.Engagement;
import model.battlefield.lighting.SunLight;
import model.battlefield.map.Map;
import model.battlefield.map.parcel.ParcelManager;
import model.builders.definitions.BuilderLibrary;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * This class simply contains all necessary elements to set a complete battlefield :
 * - a map and a parcel manager
 * - an army engagement and a manager
 * - a sunlight
 * - a pool of playing actors
 *
 */
@Root
public class Battlefield {
	@Element
	public final Map map;
	@Element
	public final Engagement engagement;

	public String fileName;

	public final ArmyManager armyManager;
	public ParcelManager parcelManager;
	public final SunLight sunLight;
	public final ActorPool actorPool;

	public Battlefield(@Element(name="map")Map unfinishedMap, @Element(name="engagement")Engagement engagement){
		this.map = unfinishedMap;
		this.engagement = engagement;
		armyManager = new ArmyManager();
		sunLight = new SunLight();
		actorPool = new ActorPool();
	}

	public Battlefield(Map map, BuilderLibrary lib) {
		this.map = map;
		armyManager = new ArmyManager();
		parcelManager = new ParcelManager(map);
		sunLight = new SunLight();
		actorPool = new ActorPool();
		engagement = new Engagement(this, lib);
	}

	public void buildParcels(){
		parcelManager = new ParcelManager(map);
	}
}
