package geometry.geom2d;

import geometry.geom2d.intersection.Intersection;
import geometry.geom2d.intersection.LineLineIntersector;
import geometry.math.AngleUtil;

import java.util.ArrayList;
import java.util.List;

public class Polyline2D extends ArrayList<Segment2D>{
	
	Point2D initialPoint = null;
	
	public Polyline2D() {
	}
	
	public Polyline2D(Point2D initialPoint){
		this.initialPoint = initialPoint;
	}
	
	public void addPoint(Point2D p){
		if(isEmpty())
			if(initialPoint == null)
				initialPoint = p;
			else
				add(new Segment2D(initialPoint, p));
		else
			add(new Segment2D(get(size()-1).getEnd(), p));
	}
	
	@Override
	public void clear() {
		super.clear();
		initialPoint = null;
	}

	public boolean contains(Point2D p){
		for(Segment2D s : this)
			if(s.contains(p))
				return true;
		return false;
	}
	
	public Polyline2D getTransformed(Transform2D transform){
		Polyline2D res = new Polyline2D();
		for(Segment2D s : this)
			res.add(s.getTransformed(transform));
		return res;
	}
	
	public Point2D getFirstPoint(){
		return get(0).getStart();
	}
	public Point2D getLastPoint(){
		if(size() == 0)
			if(initialPoint != null)
				return initialPoint;
			else
				throw new RuntimeException("Can't access last point of an empty polyline");
		return get(size()-1).getEnd();
	}
	
	public boolean isLoop(){
		return getFirstPoint().equals(getLastPoint());
	}
	
	public boolean hasInside(Point2D p){
		if(!isLoop())
			return false;
		int turn = AngleUtil.NONE;
		for(Segment2D s : this){
			int localTurn = AngleUtil.getTurn(s.getStart(), s.getEnd(), p);
			if(localTurn == AngleUtil.NONE)
				return true;
			if(turn == AngleUtil.NONE)
				turn = localTurn;
			else if(turn != localTurn)
				return false;
		}
		return true;
	}
	
	public Intersection getIntersection(Line2D line){
		List<Point2D> intersectionPoints = new ArrayList<>();
		for(Segment2D s : this){
			LineLineIntersector it = new LineLineIntersector(s, line);
			if(it.hasUniqueIntersection())
				intersectionPoints.add(it.getIntersection().getUnique());
		}
		Intersection res = new Intersection();
		res.points = intersectionPoints;
		return res;
	}
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	