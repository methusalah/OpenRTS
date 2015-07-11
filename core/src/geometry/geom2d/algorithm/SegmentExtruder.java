package geometry.geom2d.algorithm;

import geometry.collections.PointRing;
import geometry.geom2d.Polygon;
import geometry.geom2d.Segment2D;
import geometry.math.AngleUtil;

public class SegmentExtruder {

	private Segment2D segment;
	public Polygon extrusion;
	private double extrudeWidth;
	
	public SegmentExtruder(Segment2D segment, double width) {
		this.segment = segment;
		extrudeWidth = width;
		compute();
	}

	private void compute() {
		double normal = segment.getAngle() - AngleUtil.RIGHT;
		Segment2D projection = (Segment2D) segment.getTranslation(normal, extrudeWidth);
		
		PointRing ring = new PointRing();
		ring.add(segment.getEnd());
		ring.add(segment.getStart());
		ring.add(projection.getStart());
		ring.add(projection.getEnd());
		extrusion = new Polygon(ring);
	}
	
	
}
