package geometry.geom2d.algorithm;

import geometry.collections.EdgeRing;
import geometry.collections.PointRing;
import geometry.collections.Ring;
import geometry.geom2d.Line2D;
import geometry.geom2d.Point2D;
import geometry.geom2d.Polygon;
import geometry.geom2d.Segment2D;
import geometry.math.AngleUtil;

import java.util.ArrayList;
import java.util.logging.Logger;

public class OffsetOperator {

	private static final Logger logger = Logger.getLogger(OffsetOperator.class.getName());

	// inputs
	private Polygon p;
	private Ring<Double> offsets;

	// outputs
	private Polygon remainder;
	private ArrayList<Polygon> borders;
	private ArrayList<Polygon> loneBorders;
	public Ring<Integer> correspondences = new Ring<Integer>();

	// internal data
	private Ring<Line2D> lines;
	private EdgeRing segments;
	boolean computed;

	private Point2D borderStart;

	public OffsetOperator(Polygon p) {
		this.p = p;
		reset();
	}

	private void reset() {
		borders = new ArrayList<Polygon>();

		loneBorders = new ArrayList<Polygon>();

		lines = new Ring<Line2D>();
		segments = new EdgeRing();

		computed = false;
	}

	public void offsetAll(double offset) {
		offsets = new Ring<Double>();
		for (int i = 0; i < p.size(); i++) {
			offsets.add(offset);
		}
		reset();
	}

	public void setOffsets(Ring<Double> offsets) {
		this.offsets = offsets;
		reset();
	}

	public void setOffsets(EdgeSelector selector) {
		offsets = selector.edgeValues;
		reset();
	}

	public Polygon getRemainder() {
		compute();
		return remainder;
	}

	public ArrayList<Polygon> getBorders() {
		compute();
		if(borders.size() == 0) {
			logger.severe("Error : no border computed. " + p + p.toRefactoringString());
			logger.severe("offsets : ");
			for(Double v : offsets) {
				logger.severe("    " + v);
			}
		}
		return borders;
	}
	public ArrayList<Polygon> getLoneBorders() {
		compute();
		return loneBorders;
	}

	private void compute() {
		if(computed) {
			return;
		}

		// transforming to max offset
		// this method doesn't allow to much topology transform
		double maxSetBack = -100000;
		double maxSetOut = 100000;
		for(Segment2D s : p.getEdges()){
			Segment2D prev = p.getEdges().getPrevious(s);
			Segment2D next = p.getEdges().getNext(s);

			double prevBisAngle = AngleUtil.getBisector(prev.getAngle(), s.getAngle());
			double nextBisAngle = AngleUtil.getBisector(s.getAngle(), next.getAngle());

			Segment2D prevInnerBis = new Segment2D(s.getStart(), s.getStart().getTranslation(prevBisAngle, 100000));
			Segment2D nextInnerBis = new Segment2D(s.getEnd(), s.getEnd().getTranslation(nextBisAngle, 100000));

			Segment2D prevOutBis = new Segment2D(s.getStart(), s.getStart().getTranslation(prevBisAngle+AngleUtil.FLAT, 100000));
			Segment2D nextOutBis = new Segment2D(s.getEnd(), s.getEnd().getTranslation(nextBisAngle+AngleUtil.FLAT, 100000));

			Point2D innerIntersection = prevInnerBis.getAnyIntersection(nextInnerBis);
			if(innerIntersection!=null) {
				if(innerIntersection.getDistance(s.getStart())>maxSetBack) {
					maxSetBack = -innerIntersection.getDistance(s.getStart());
				}
			}

			Point2D outIntersection = prevOutBis.getAnyIntersection(nextOutBis);
			if(outIntersection!=null) {
				if(outIntersection.getDistance(s.getStart())<maxSetOut) {
					maxSetOut = outIntersection.getDistance(s.getStart());
				}
			}
		}

		for(Double o : offsets) {
			if(o > maxSetOut){
				offsets.set(offsets.indexOf(o), maxSetOut);
			}
			if(o < maxSetBack){
				offsets.set(offsets.indexOf(o), maxSetBack);
				logger.info("offset set to : " + maxSetBack);
			}
		}


		computeLines();
		computeSegments();

		EdgeRing edges = p.getEdges();
		EdgeRing remainderEdges = new EdgeRing();
		PointRing borderPoints = new PointRing();

		for (int i = 0; i < segments.size(); i++) {
			Segment2D s = segments.get(i);
			Segment2D edge = edges.get(i);
			double offset = offsets.get(i);

			if(s != null) {
				remainderEdges.add(s);
				if(offset == 0) {
					// In this test, we check if the segment is offset or not
					// we also test if two consecutive offsets have opposite directions.
					endBorder(borderPoints);
				} else {
					createLoneBorder(s, edge);
					completeBorder(borderPoints, s, edge);
				}
			} else {
				completeBorder(borderPoints, edge);
				createLoneBorder(getNextValidSegment(i).getStart(), edge);
			}
		}
		endBorder(borderPoints);
		remainder = new Polygon(remainderEdges);
		computed = true;
	}

	private void createLoneBorder(Segment2D segment, Segment2D edge) {
		PointRing points = new PointRing();
		points.add(edge.getStart());
		points.add(edge.getEnd());
		points.add(segment.getEnd());
		points.add(segment.getStart());
		loneBorders.add(new Polygon(points));
	}
	private void createLoneBorder(Point2D point, Segment2D edge) {
		PointRing points = new PointRing();
		points.add(edge.getStart());
		points.add(edge.getEnd());
		points.add(point);
		loneBorders.add(new Polygon(points));
	}

	private void completeBorder(PointRing points, Segment2D segment, Segment2D edge) {
		// We save the start point of the first original edge
		// We will shift the polygon to set this edge first
		if(points.isEmpty()) {
			borderStart = edge.getStart();
			points.add(segment.getStart());
			points.add(edge.getStart());
		}
		points.add(0, segment.getEnd());
		points.add(edge.getEnd());
	}

	private void completeBorder(PointRing points, Segment2D edge) {
		points.add(edge.getEnd());
	}

	private void endBorder(PointRing points) {
		if(points.isEmpty()) {
			return;
		}
		points.shiftTo(borderStart);
		borders.add(new Polygon(points));
		points.clear();
	}

	/*
	 * This method computes the lines offset to their offset value.
	 * One line for each edge of the polygon if created, whatever is it's offset value.
	 */
	private void computeLines() {
		EdgeRing edges = p.getEdges();
		for (int i = 0; i < edges.size(); i++) {
			Line2D line = new Line2D(edges.get(i));
			double offset = offsets.get(i);

			if(offset == 0) {
				lines.add(line);
				continue;
			}

			double normal = line.getAngle() - AngleUtil.RIGHT;
			lines.add(line.getTranslation(normal, offset));
		}
	}

	/*
	 * This method computes the edges of the remainder polygon, by intersecting the offset lines.
	 * The borders will be the difference between the remainder and the original polygon.
	 */
	private void computeSegments() {
		int correspondence = 0;
		for (Line2D line : lines) {
			Point2D start;
			Point2D end;
			// We create each segment with the intersection points between lines
			// We must check if the lines are collinear because polygon allow collinear consecutives edges.
			if(line.isCollinear(lines.getPrevious(line))) {
				start = line.getStart();
			} else {
				if(!line.intersectAtSinglePoint(lines.getPrevious(line))) {
					logger.warning("les lignes n'ont pas d'intersection ??" + line + lines.getPrevious(line));
				}
				start = line.getUniqueIntersection(lines.getPrevious(line));
			}

			if(line.isCollinear(lines.getNext(line))) {
				end = line.getEnd();
			} else {
				if(!line.intersectAtSinglePoint(lines.getNext(line))) {
					logger.warning("les lignes n'ont pas d'intersection ??" + line + lines.getNext(line));
				}
				end = line.getUniqueIntersection(lines.getNext(line));
			}

			if(!start.equals(end) && AngleUtil.areSimilar(end.getSubtraction(start).getAngle(), line.getAngle())) {
				segments.add(new Segment2D(start, end));
			} else {
				segments.add(null);
				correspondence--;
				if(correspondence <0) {
					correspondence = p.points.size()-1;
				}
			}
			correspondences.add(new Integer(correspondence));
			correspondence++;
		}


		// Now we have to manage the collapsed edges, for the surrounding edge to intersect properly
		// We find the first non null edge
		Segment2D lastValid = segments.getFirst();
		if(lastValid == null) {
			lastValid = getNextValidSegment(0);
		}

		// we throw an exception in the case where no valid edge can be found
		if(lastValid == null) {
			throw new RuntimeException("There is no valid edge.");
		}

		int start = segments.indexOf(lastValid);
		int index = start;
		do {
			// Now we get the next non null edge
			Segment2D nextValid = getNextValidSegment(index);

			// If the next valid edge is not the neighbor of the last valid edge,
			// it means that there is on or more collapsed edge between it.
			if(nextValid != segments.getNext(lastValid)) {
				Point2D intersection = new Line2D(lastValid).getUniqueIntersection(new Line2D(nextValid));

				int lastValidIndex = segments.indexOf(lastValid);
				int nextValidIndex = segments.indexOf(nextValid);
				segments.set(lastValidIndex, new Segment2D(lastValid.getStart(), intersection));
				segments.set(nextValidIndex, new Segment2D(intersection, nextValid.getEnd()));
				nextValid = segments.get(nextValidIndex);
			}

			// Else, there is no collapsed edge and we do nothing.
			index = segments.indexOf(nextValid);
			lastValid = segments.get(segments.indexOf(nextValid));
		} while(index != start);

		//debug
		//		for(Segment2D s : segments)
		//			LogUtil.logger.info("ahhh"+s);
	}

	// BEN ici, tant que je ne sais pas comparer les objets par leur reference, je doit fonctionner avec les index
	private Segment2D getNextValidSegment(int start) {
		Segment2D prev = segments.get(start);
		int index = start;
		Segment2D res = null;
		do {
			index++;
			if(index == segments.size()) {
				index = 0;
			}

			res = segments.get(index);
		} while(res == null && index != start);
		return res;
	}
}
