package geometry.geom2d.intersection;

import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Intersection {

	public List<Point2D> points = new ArrayList<>();
	public Point2D point = null;
	public Point2D zoneStart = null;
	public Point2D zoneEnd = null;
	
	
	public boolean exist(){
		return isMultiple() || isZone() || isUnique();
	}
	
	
	public boolean isUnique(){
		return point != null;
	}
	
	public boolean isZone(){
		return zoneStart != null;
	}
	
	public boolean isMultiple(){
		return !points.isEmpty();
	}

	
	
	
	
	public Point2D getUnique(){
		if(!isUnique())
			throw new RuntimeException("This intersection is not unique. You must check before get.");
		return point;
	}
	
	public List<Point2D> getAll(){
		if(points.isEmpty()){
			List<Point2D> res = new ArrayList<>();
			if(point != null)
				res.add(point);
			return res;
		}
		return points;
	}
		
	public Point2D getZoneStart(){
		if(!isZone())
			throw new RuntimeException("This intersection is not zone. You must check before get.");
		return zoneStart;
	}
	public Point2D getZoneEnd(){
		if(!isZone())
			throw new RuntimeException("This intersection is not zone. You must check before get.");
		return zoneEnd;
	}
	public Point2D getZoneCenter(){
		if(!isZone())
			throw new RuntimeException("This intersection is not zone. You must check before get.");
		Point2D vector = zoneEnd.getSubtraction(zoneStart);
		return zoneStart.getAddition(vector.getMult(0.5));
	}
	
			
	
}
