package geometry.geom3d;



import geometry.geom2d.Point2D;
import geometry.geom2d.Polygon;
import geometry.geom2d.algorithm.Triangulator;
import geometry.math.AngleUtil;

public class PolygonRevolutionizer {
	private static final double NO_SMOOTH_ANGLE = AngleUtil.toRadians(30);

	public MyMesh mesh;
	Polygon polygon;
	private double startAngle;
	private double endAngle;
	private Point2D pivot;
	private int nbFaces;
	public double maxChord = 0;
	private double faceAngle;
	
	double uOffset = 0;
	double vOffset = 0;
	double uScale = 1;
	double vScale = 1;
	
	
	public PolygonRevolutionizer(Polygon polygon, double startAngle, double endAngle, Point2D pivot) {
		mesh = new MyMesh();
		this.polygon = polygon;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.pivot = pivot;
		setNbFaces(5);
	}
	
	public PolygonRevolutionizer(Polygon p, double startAngle, double endAngle) {
		this(p, startAngle, endAngle, Point2D.ORIGIN);
	}
	
	public void setU(double uOffset, double uScale) {
		this.uOffset = uOffset;
		this.uScale = uScale;
	}
	
	public void setNbFaces(int nbFaces) {
		this.nbFaces = nbFaces;
		maxChord = 0;
		faceAngle = (endAngle-startAngle)/nbFaces;
		for(Point2D p : polygon.points) {
			double chord = AngleUtil.getChord(faceAngle)*nbFaces*p.x;
			if(chord > 0 && chord > maxChord ||
					chord < 0 && chord < maxChord)
				maxChord = chord;
		}
	}
	
	public void setVFromPointToPoint(Point2D start, Point2D end) {
		Polygon p = polygon;
				
		double startLength = p.getLengthTo(start);
		double endLength = p.getLengthTo(end);
		double length;
		
		if(endLength > startLength)
			length = endLength-startLength;
		else if(startLength > endLength)
			length = p.getLength()-startLength+endLength;
		else 
			length = p.getLength();

		vOffset = startLength;
		vScale = length/p.getLength();
	}

	public void setVFromPointWithSize(Point2D start, double size) {
		Polygon p = polygon;
				
		double startLength = p.getLengthTo(start);

		vOffset = startLength;
		vScale = size/p.getLength();
	}

	public void extrude() {

		for(int faceIndex = 0; faceIndex<nbFaces; faceIndex++) {
			double faceStartAngle = startAngle+faceAngle*faceIndex;
			double faceEndAngle = startAngle+faceAngle*faceIndex+faceAngle;
			
			for(Point2D p : polygon.points) {
				add6Indices(mesh.vertices.size());
				
				Point2D prev = polygon.points.getPrevious(p);
				Point2D next = polygon.points.getNext(p);
				Point2D nextNext = polygon.points.getNext(next);
				
				// add 4 vertices
				mesh.vertices.add(new Point3D(p, faceEndAngle, 2, pivot));
				mesh.vertices.add(new Point3D(next, faceEndAngle, 2, pivot));
				mesh.vertices.add(new Point3D(p, faceStartAngle, 2, pivot));
				mesh.vertices.add(new Point3D(next, faceStartAngle, 2, pivot));

				// add 4 normals
				double wPrevNormal = getSmoothedNormal(prev, p, next, 0);
				double wNextNormal = getSmoothedNormal(p, next, nextNext, 1);

				mesh.normals.add(new Point3D(Point2D.ORIGIN.getTranslation(wPrevNormal, 1), faceEndAngle, 2));
				mesh.normals.add(new Point3D(Point2D.ORIGIN.getTranslation(wNextNormal, 1), faceEndAngle, 2));
				mesh.normals.add(new Point3D(Point2D.ORIGIN.getTranslation(wPrevNormal, 1), faceStartAngle, 2));
				mesh.normals.add(new Point3D(Point2D.ORIGIN.getTranslation(wNextNormal, 1), faceStartAngle, 2));
				
				// add 4 textCoord
				double pLength = polygon.getLengthTo(p);
				double nextLength = polygon.getLengthTo(next);
				double pTextureVCoord = 1-(pLength-vOffset)/polygon.getLength()/vScale;
				double nextTextureVCoord = 1-(nextLength-vOffset)/polygon.getLength()/vScale;

				double pChord = AngleUtil.getChord(faceAngle)*faceIndex*p.x;
				double nextChord = AngleUtil.getChord(faceAngle)*faceIndex*next.x;
				double endPChord = AngleUtil.getChord(faceAngle)*(faceIndex+1)*p.x;
				double endNextChord = AngleUtil.getChord(faceAngle)*(faceIndex+1)*next.x;
				
				double pTotalChord = AngleUtil.getChord(faceAngle)*nbFaces*p.x;
				double nextTotalChord = AngleUtil.getChord(faceAngle)*nbFaces*next.x;
				
				mesh.textCoord.add(new Point2D((endPChord-uOffset/maxChord*pTotalChord)/pTotalChord/uScale, pTextureVCoord));
				mesh.textCoord.add(new Point2D((endNextChord-uOffset/maxChord*nextTotalChord)/nextTotalChord/uScale, nextTextureVCoord));
				mesh.textCoord.add(new Point2D((pChord-uOffset/maxChord*pTotalChord)/pTotalChord/uScale, pTextureVCoord));
				mesh.textCoord.add(new Point2D((nextChord-uOffset/maxChord*nextTotalChord)/nextTotalChord/uScale, nextTextureVCoord));
			}
		}
	}
	
	public void closeBase() {
		Triangulator t = new Triangulator(polygon);
		double startNormal = startAngle-AngleUtil.RIGHT;
		double startCos = Math.cos(startNormal);
		double startSin = Math.sin(startNormal);

		// Start face
		int lastIndex = mesh.vertices.size();
		for (Point2D p : polygon.points) {
			mesh.vertices.add(new Point3D(p, startAngle, 2, pivot));
			mesh.normals.add(new Point3D(startCos, startSin, 0));
		}

		for (int i = (t.getIndices().size() - 1); i >= 0; i--)
			mesh.indices.add(t.getIndices().get(i) + lastIndex);
		
		mesh.textCoord.addAll(polygon.getTextureMap());
	}
	
	public void closeSum() {
		Triangulator t = new Triangulator(polygon);
		double endNormal = endAngle+AngleUtil.RIGHT;
		double endCos = Math.cos(endNormal);
		double endSin = Math.sin(endNormal);

		int lastIndex = mesh.vertices.size();
		for (Point2D p : polygon.points) {
			mesh.vertices.add(new Point3D(p, endAngle, 2, pivot));
			mesh.normals.add(new Point3D(endCos, endSin, 0));
		}
		
		for (int i = 0; i < t.getIndices().size(); i++)
			mesh.indices.add(t.getIndices().get(i) + lastIndex);
		
		mesh.textCoord.addAll(polygon.getTextureMap());
	}
	
	private void add6Indices(int lastIndex) {
		mesh.indices.add(lastIndex); // topLeftCorner
		mesh.indices.add(lastIndex + 1); // topRightCorner
		mesh.indices.add(lastIndex + 2); // bottomLeftCorner

		mesh.indices.add(lastIndex + 2); // bottomLeftCorner
		mesh.indices.add(lastIndex + 1); // topRightCorner
		mesh.indices.add(lastIndex + 3); // bottomRightCorner
	}
	
	private double getSmoothedNormal(Point2D prev, Point2D p, Point2D next, int normalIndex) {
		double n = AngleUtil.normalize(next.getSubtraction(p).getAngle()-AngleUtil.RIGHT);
		double prevN = AngleUtil.normalize(p.getSubtraction(prev).getAngle()-AngleUtil.RIGHT);

		double diff = AngleUtil.getSmallestDifference(n, prevN);
		double bissector = AngleUtil.getBisector(prevN, n);
		double res;
		
		if(diff > NO_SMOOTH_ANGLE) {
			if(normalIndex == 0)
				res = n;
			else
				res = prevN;
		} else {
			res = bissector;
		}

		return res;
	}

	

}
