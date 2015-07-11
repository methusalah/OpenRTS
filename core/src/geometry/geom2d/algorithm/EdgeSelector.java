package geometry.geom2d.algorithm;

import geometry.collections.EdgeRing;
import geometry.collections.Ring;
import geometry.geom2d.Polygon;
import geometry.geom2d.Segment2D;
import geometry.math.AngleUtil;

import java.util.ArrayList;

public class EdgeSelector {

	public enum Direction {FRONT, LEFT, RIGHT, BACK, SOUTH, EAST, WEST, NORTH, TOP, BOTTOM}
	// memo :
	// - front is north,
	// - left is west,
	// - right is east,
	// - south is back,
	// - top is south,
	// - bottom is north.
	
	
	// inputs
	private Polygon p;
	
	// outputs
	public Ring<Double> edgeValues;
	
	// internal data
	private Double baseNormal;
	
	public EdgeSelector(Polygon p) {
		this.p = p;
		setWorldNormal(); // Default value;
		reset();
	}
	
	private void reset() {
		edgeValues = new Ring<Double>();
		do
			edgeValues.add(0.0);
		while(edgeValues.size() < p.size());
	}
	
	/*
	 * after calling the method, the selector will select the polygon edges by comparison with it's first edge,
	 * which is considered "Front"
	 * 
	 * Note that this method reset the outputs.
	 */
	public void setRelativeNormal() {
		baseNormal = AngleUtil.normalize(p.getEdges().getFirst().getAngle() - AngleUtil.RIGHT);
		reset();
	}
	
	/*
	 * After calling this method, the selector will select the polygon edges by comparison with the north of the World Coordinate System.
	 * 
	 * Note that visually, north is on the -Z axis.
	 * Note that this method reset the outputs.
	 */
	public void setWorldNormal() {
		baseNormal = -AngleUtil.RIGHT;
		reset();
	}
	
	public void selectAll(double value) {
		edgeValues = new Ring<Double>();
		do
			edgeValues.add(value);
		while(edgeValues.size() < p.size());
	}
	
	/*
	 * Select and apply the given value to all edges with the given direction
	 * by comparison with the previously set normal (default normal is World).
	 * 
	 * Note that the method will always convert relative values to absolute : 
	 *  - North equals Front
	 *  - South equals Back
	 *  - East equals Right
	 *  - West equals Left
	 */
	public void selectByNormal(Direction d, double value) {
		EdgeRing edges = p.getEdges();
		for (int i = 0; i < p.size(); i++) {
			double edgeNormal = AngleUtil.normalize(edges.get(i).getAngle() - Math.PI/2);
			if((d == Direction.FRONT || d == Direction.NORTH || d == Direction.BOTTOM) && isNorth(edgeNormal))
				edgeValues.set(i, value);
			else if((d == Direction.BACK || d == Direction.SOUTH || d == Direction.TOP) && isSouth(edgeNormal))
				edgeValues.set(i, value);
			else if((d == Direction.RIGHT || d == Direction.EAST) && isEast(edgeNormal))
				edgeValues.set(i, value);
			else if((d == Direction.LEFT || d == Direction.WEST) && isWest(edgeNormal))
				edgeValues.set(i, value);
		}
	}
	
	public void selectByStreets(boolean onStreet, ArrayList<Double> streets, double value) {
		for (int i = 0; i < p.size(); i++) {
			if(streets.get(i) > 0)
				edgeValues.set(i, value);
		}
	}
	
	private boolean isNorth(double angle) {
		double north = baseNormal;
		if(AngleUtil.getSmallestDifference(north, angle) < Math.PI/4)
			return true;
		else
			return false;
	}
	private boolean isSouth(double angle) {
		double south = AngleUtil.normalize(baseNormal + Math.PI);
		if(AngleUtil.getSmallestDifference(south, angle) < Math.PI/4)
			return true;
		else
			return false;
	}
	private boolean isEast(double angle) {
		double east = AngleUtil.normalize(baseNormal + Math.PI/2);
		if(AngleUtil.getSmallestDifference(east, angle) < Math.PI/4)
			return true;
		else
			return false;
	}
	private boolean isWest(double angle) {
		double west = AngleUtil.normalize(baseNormal - Math.PI/2);
		if(AngleUtil.getSmallestDifference(west, angle) < Math.PI/4)
			return true;
		else
			return false;
	}
	
	public ArrayList<Segment2D> getSelectedEdges() {
		EdgeRing edges = p.getEdges();
		ArrayList<Segment2D> res = new ArrayList<Segment2D>();
		for (int i = 0; i < edgeValues.size(); i++)
			if(edgeValues.get(i) != 0)
				res.add(edges.get(i));
		return res;
	}
}
