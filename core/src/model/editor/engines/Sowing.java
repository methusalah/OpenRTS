package model.editor.engines;

import geometry.geom2d.Point2D;
import geometry.math.Angle;
import geometry.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;
import model.battlefield.map.atlas.AtlasLayer;
import model.builders.entity.TrinketBuilder;
import model.builders.entity.definitions.BuilderManager;


public class Sowing {

	public double distFromCliff = 0;
	public double slopeMin = 0, slopeMax = 0;
	public List<String> allowedGrounds = new ArrayList<>();
	public List<String> forbiddenCovers = new ArrayList<>();

	public List<TrinketBuilder> trinketBuilders = new ArrayList<>();
	List<Trinket> toGrow = new ArrayList<>();
	
	
	public Sowing() {
		
	}
	
	public void addTrinket(String id){
		trinketBuilders.add(BuilderManager.getTrinketBuilder(id));
	}
	
	public void setMinSlope(double slope){
		slopeMin = slope;
	}

	public void setMaxSlope(double slope){
		slopeMax = slope;
	}

	public void setCliffDist(double dist){
		distFromCliff = dist;
	}
	
	public boolean isAllowed(Point2D p){
		Map map = ModelManager.getBattlefield().getMap();
		
		// check if the point is near enough from a cliff.
		if(distFromCliff > 0){
			boolean hasCliff = false;
			for(Tile t : map.getTilesAround(p, distFromCliff))
				if(t.hasCliff()){
					hasCliff = true;
					break;
				}
			if(!hasCliff)
				return false;
		}
			

		// check if the point is on a correctly sloped terrain.
		if(slopeMin > 0 || slopeMax > 0){
			double dist = map.getNormalVectorAt(p).get2D().getLength(); 
			double angle = Angle.RIGHT - new Point2D(dist, 1).getAngle();
			double a = Angle.toDegrees(angle);
			//angle = a;
			if(slopeMin > 0 && angle < Angle.toRadians(slopeMin))
				return false;
			if(slopeMax > 0 && angle > Angle.toRadians(slopeMax))
				return false;
		}
		
		// check if the point is on allowed ground texture
		if(!allowedGrounds.isEmpty()){
			boolean allowedGroundFound = false;
			for(String s : allowedGrounds){
				int i = Integer.parseInt(s);
				AtlasLayer l = map.atlas.getLayers().get(i);
				if(l.getInMapSpace(p) > 0){
					allowedGroundFound = true;
					break;
				}
			}
			if(!allowedGroundFound)
				return false;
		}
			
		
		return true;
		
	}
	
}
