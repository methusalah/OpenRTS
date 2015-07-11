package geometry.geom2d.algorithm;

import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.Point2D;
import geometry.geom2d.Polygon;
import geometry.geom2d.Segment2D;

import java.util.ArrayList;
import java.util.logging.Logger;


public class PolygonFiller {

	private static final Logger logger = Logger.getLogger(PolygonFiller.class.getName());
	public static int RESOLUTION = 1024;

	public boolean res[][] = new boolean[RESOLUTION][RESOLUTION];
	Polygon p;
	AlignedBoundingBox b;

	int currentY = 0;

	public PolygonFiller(Polygon p) {
		this.p = p;
		b = p.getBoundingBox();
	}

	public void compute() {
		for (int i = 0; i < RESOLUTION - 1; i++) {
			ArrayList<Double> scanLine = new ArrayList<Double>();
			double polyY = getPolygonY(i);
			ArrayList<Segment2D> involved = getInvolvedEdges(polyY);

			// We look for one intersection per involved segment
			// When a segment is horizontal, we must count its two points
			// When a segment is vertical, we manage the infinite slope
			for (Segment2D s : involved) {
				double slope = s.getSlope();
				double x;
				if (slope == 0) {
					// horizontal line : we zap
					// very few chance to meet this case because of the double precision
					logger.info("It happens.");
					x = Double.NaN;
				} else if (slope == Double.POSITIVE_INFINITY) {
					// vertical line
					x = s.getStart().x;
				} else {
					x = (polyY - s.getOrdinate()) / s.getSlope();
				}

				// There may be many times the same X. We have
				// to check if we must count it once or twice. If both segments are
				// above or beneath we count it twice. Else we count it once.
				boolean valid = true;
				for (int j = 0; j < scanLine.size(); j++) {
					if (x == scanLine.get(j) && !areOnTheSameSide(s, involved.get(j))) {
						valid = false;
						break;
					}
				}
				if (valid) {
					scanLine.add(x);
				}
			}

			double min = extractMin(scanLine);
			boolean full = false;
			for (int j = 0; j < RESOLUTION - 1; j++) {
				if (!Double.isNaN(min) && j >= getMapX(min)) {
					if (full == false) {
						full = true;
					} else {
						full = false;
					}
					min = extractMin(scanLine);
				}
				res[j][i] = full;
			}
		}
	}

	private double extractMin(ArrayList<Double> scanLine) {
		if (scanLine.isEmpty()) {
			return Double.NaN;
		}

		double min = scanLine.get(0);
		for (Double d : scanLine) {
			if (d < min) {
				min = d;
			}
		}
		scanLine.remove(min);
		return min;
	}

	private boolean areOnTheSameSide(Segment2D s1, Segment2D s2) {
		Point2D p = s1.getCommonEnd(s2);
		if (p == null) {
			throw new RuntimeException("Segments must have one common point.");
		}
		Point2D q1 = s1.getOppositeEnd(p);
		Point2D q2 = s2.getOppositeEnd(p);

		if (q1.y <= p.y && q2.y <= p.y) {
			return true;
		}
		if (q1.y >= p.y && q2.y >= p.y) {
			return true;
		}
		return false;
	}

	private double getPolygonY(int mapY) {
		return mapY * b.height / RESOLUTION + b.minY;
	}

	private int getMapX(double polygonX) {
		return (int) ((polygonX - b.minX) * RESOLUTION / b.width);
	}

	private ArrayList<Segment2D> getInvolvedEdges(double y) {
		ArrayList<Segment2D> res = new ArrayList<Segment2D>();
		for (Segment2D s : p.getEdges()) {
			if (s.getStart().y > y && s.getEnd().y < y || s.getStart().y < y && s.getEnd().y > y) {
				res.add(s);
			}
		}
		return res;
	}
}
