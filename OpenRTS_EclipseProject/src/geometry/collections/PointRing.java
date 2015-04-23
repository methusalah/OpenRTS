package geometry.collections;

import geometry.geom2d.Point2D;

import java.util.Collection;


public class PointRing extends Ring<Point2D>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PointRing(Collection<Point2D> col) {
		super(col);
	}
	
	public PointRing() {
		
	}
}
