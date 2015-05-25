package model.battlefield;

import model.battlefield.actors.ActorPool;
import model.battlefield.army.Engagement;
import model.battlefield.lighting.SunLight;
import model.battlefield.map.Map;
import model.battlefield.map.parcel.ParcelManager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class simply contains all necessary elements to set a complete battlefield :
 * - a map and a parcel manager
 * - an army engagement and a manager
 * - a sunlight
 * - a pool of playing actors
 *
 */

public class Battlefield {

	@JsonProperty
	private Map map;
	@JsonProperty
	private Engagement engagement;

	@JsonIgnore
	private String fileName;

	@JsonIgnore
	private ParcelManager parcelManager;
	@JsonIgnore
	private SunLight sunLight = new SunLight();
	@JsonIgnore
	private ActorPool actorPool = new ActorPool();

	public Battlefield() {

	}

	// public Battlefield(@Element(name="map")Map unfinishedMap, @Element(name="engagement")Engagement engagement){
	// this.map = unfinishedMap;
	// this.engagement = engagement;
	// sunLight = new SunLight();
	// actorPool = new ActorPool();
	// }

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
