package geometry.geom2d.algorithm;



public class InnerRectangleOperator {
//	public ArrayList<Segment2D> sides;
//	/*
//	 * Get the first edge's aligned rectangle that fit in the polygon and have the maximum area.
//	 */
//	public Polygon getInnerRectangle() {
//		return getInnerRectangle(getFirstEdge());
//	}
//
//	/*
//	 * Get the given edge's aligned rectangle that fit in the polygon and have the maximum area.
//	 * The given edge must be one of the polygon edge.
//	 */
//	public Polygon getInnerRectangle(Segment2D edge) {
//		Segment2D front = edge;
//		sides = new ArrayList<Segment2D>();
//		computeSides(front, sides);
//		double bestArea = 0;
//		Polygon bestRectangle = null;
//		
//		for (int i = 0; i < sides.size() - 1; i++) {
//			for (int j = i + 1; j < sides.size(); j++) {
//				// we first compute all values for this combinaison of sides
//				Segment2D left = sides.get(i);
//				Segment2D right = sides.get(j);
//				Segment2D base = new Segment2D(left.getStart(), right.getStart());
//				double shortestSideLength;
//				if(left.getLength() < right.getLength())
//					shortestSideLength = left.getLength();
//				else
//					shortestSideLength = right.getLength();
//				double area = base.getLength() * shortestSideLength;
//				
//				// Then we check if the combinaison has best area.
//				// In this case, we construct the rectangle and store it as best inner rectangle.
//				if(area > bestArea) {
//					bestArea = area;
//					bestRectangle = new Polygon();
//					bestRectangle.add(left.getStart());
//					bestRectangle.add(right.getStart());
//					
//					Point2D rightProjection = new Point2D(right.getStart());
//					Point2D leftProjection = new Point2D(left.getStart());
//					rightProjection.translate(right.getAngle(), shortestSideLength);
//					leftProjection.translate(left.getAngle(), shortestSideLength);
//					
//					bestRectangle.add(rightProjection);
//					bestRectangle.add(leftProjection);
//				}
//			}
//		}
//		return bestRectangle;
//	}
//	
//	private void computeSides(Segment2D front, ArrayList<Segment2D> sides) {
//		Point2D slidingPoint = new Point2D(front.getStart());
//		
//		do {
//			// TODO Ugly break loop conditioning
//			if(front.getStart().getDistance(slidingPoint) > front.getLength())
//				slidingPoint = new Point2D(front.getEnd());
//			Point2D base = new Point2D(slidingPoint);
//			Point2D projection = new Point2D(base);
//			projection.translate(front.getAngle() + Angle.RIGHT, 1);
//			Line2D sideLine = new Line2D(base, projection);
//			Segment2D shortestSide = null;
//			
//			for (Segment2D edge : getEdges()) {
//				if(edge.coincide(front))
//					continue;
//				if(edge.intersectAtSinglePoint(sideLine)) {
//					Point2D intersection = edge.getUniqueIntersection(sideLine);
//					if(intersection.occupySameSpace(base))
//						continue;
//					Segment2D side = new Segment2D(sideLine.getStart(), intersection);
//					if(shortestSide == null)
//						shortestSide = side;
//					else if(side.getLength() < shortestSide.getLength())
//						shortestSide = side;
//				}
//			}
//			if(shortestSide != null)
//				sides.add(shortestSide);
//
//			if(!slidingPoint.occupySameSpace(front.getEnd()))
//				slidingPoint.translate(front.getAngle(), 3);
//		} while(!slidingPoint.occupySameSpace(front.getEnd()));
//	}
}
