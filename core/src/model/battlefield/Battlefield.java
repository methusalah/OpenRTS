package model.battlefield;

import model.battlefield.actors.ActorPool;
import model.battlefield.army.Engagement;
import model.battlefield.lighting.SunLight;
import model.battlefield.map.Map;

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
	private SunLight sunLight = new SunLight();
	@JsonIgnore
	private ActorPool actorPool = new ActorPool();

	public Battlefield() {
		sunLight = new SunLight();
		actorPool = new ActorPool();
		engagement = new Engagement();
	}


	public Map getMap() {
		return map;
	}

	public Engagement getEngagement() {
		return engagement;
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

	public void setMap(Map map) {
		this.map = map;
	}
}
