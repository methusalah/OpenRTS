package model.battlefield;

import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.abstractComps.FieldComp;
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
	
	public <T extends FieldComp> List<T> getCloseComps(T fc, double radius){
		double fcX = fc.getCoord().x;
		double fcY = fc.getCoord().y;
		List<T> res = new ArrayList<>();
		for(int x = (int)(fcX-radius); x < (int)(fcX+radius); x++)
			for(int y = (int)(fcY-radius); y < (int)(fcY+radius); y++)
				if(map.isInBounds(new Point2D(x, y)))
					for(Object o : map.get(x, y).storedData)
						if(o != fc &&
								o.getClass() == fc.getClass() &&
								((FieldComp)o).getCoord().getDistance(fc.getCoord()) < radius)
							res.add((T)o);
		return res;
	}

	public <T extends FieldComp> List<T> getCloseComps(T c, Point2D p, double radius){
		double fcX = p.x;
		double fcY = p.y;
		List<T> res = new ArrayList<>();
		for(int x = (int)(fcX-radius); x < (int)(fcX+radius); x++)
			for(int y = (int)(fcY-radius); y < (int)(fcY+radius); y++)
				if(map.isInBounds(new Point2D(x, y)))
					for(Object o : map.get(x, y).storedData)
						if(o.getClass() == c.getClass() &&
								((FieldComp)o).getCoord().getDistance(p) < radius)
							res.add((T)o);
		return res;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
