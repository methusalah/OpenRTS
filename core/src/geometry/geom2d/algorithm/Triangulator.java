package geometry.geom2d.algorithm;

import geometry.collections.PointRing;
import geometry.geom2d.Point2D;
import geometry.geom2d.Polygon;
import geometry.geom2d.Segment2D;
import geometry.geom3d.Polygon3D;
import geometry.geom3d.Triangle3D;

import java.util.ArrayList;


public class Triangulator {

	private Polygon p;
	private Polygon3D p3D;
	
	boolean computed;
	private ArrayList<Integer> indices = new ArrayList<Integer>();

	public Triangulator(Polygon p) {
		this.p = p;
		computed = false;
	}

        public Triangulator(Polygon3D p) {
		this.p = p.proj;
		computed = false;
                p3D = p;
	}
	
	public ArrayList<Integer> getIndices(){
		compute();
		return indices;
	}

        public ArrayList<Triangle3D> getTriangles(){
            if(p3D == null)
                throw new RuntimeException("Triangles are available only for Polygon 3D triangluation.");
            
            ArrayList<Triangle3D> res = new ArrayList<>();
            compute();
            for(int i=0; i<indices.size(); i+=3){
                res.add(new Triangle3D(p3D.points.get(indices.get(i)),
                        p3D.points.get(indices.get(i+2)),
                        p3D.points.get(indices.get(i+1))));
            }
            return res;
	}

	public void compute() {
		if(computed)
			return;
		
		Polygon work = new Polygon(p);
		while (work.size() > 3) {
//			PointRing remainingPoints = work.points;
			PointRing remainingPoints = new PointRing(work.points);
			Point2D ear = null;
			for (Point2D point : remainingPoints) {
				if (isValidEar(point, work)) {
					ear = point;
					indices.add(p.points.indexOf(ear));
					indices.add(p.points.indexOf(work.points.getPrevious(ear)));
					indices.add(p.points.indexOf(work.points.getNext(ear)));
					
					break;
				}
			}
			if(ear == null) {
				throw new RuntimeException("Polygon has no more ear which is impossible.");
			}
			remainingPoints.remove(ear);
			work = new Polygon(remainingPoints);
		}

		if (work.size() == 3) {
			indices.add(p.points.indexOf(work.points.get(0)));
			indices.add(p.points.indexOf(work.points.get(2)));
			indices.add(p.points.indexOf(work.points.get(1)));
		}
		computed = true;
	}

	private boolean isValidEar(Point2D ear, Polygon polygon) {
		Segment2D diagonal = new Segment2D(polygon.points.getPrevious(ear), polygon.points.getNext(ear));
		return polygon.hasFullyInternalDiagonal(diagonal);
	}
}
