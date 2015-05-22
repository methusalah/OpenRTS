package model.battlefield;

import model.battlefield.actors.ActorPool;
import model.battlefield.army.Engagement;
import model.battlefield.lighting.SunLight;
import model.battlefield.map.Map;
import model.battlefield.map.parcel.ParcelManager;

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
	private Map map;
	@Element
	private Engagement engagement;

	private String fileName;

	private ParcelManager parcelManager;
	private SunLight sunLight;
	private ActorPool actorPool;

	public Battlefield() {

	}

	public Battlefield(@Element(name="map")Map unfinishedMap, @Element(name="engagement")Engagement engagement){
		this.map = unfinishedMap;
		this.engagement = engagement;
		sunLight = new SunLight();
		actorPool = new ActorPool();
	}

	public Battlefield(Map map) {
		this.map = map;
		parcelManager = new ParcelManager(map);
		sunLight = new SunLight();
		actorPool = new ActorPool();
		engagement = new Engagement();
	}

	public void buildParcels(){
		parcelManager = new ParcelManager(map);
	}

	public Map getMap() {
		return map;
	}

	public Engagement getEngagement() {
		return engagement;
	}

	public ParcelManager getParcelManager() {
		return parcelManager;
	}

	public SunLight getSunLight() {
		return sunLight;
	}

	public ActorPool getActorPool() {
		return actorPool;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
