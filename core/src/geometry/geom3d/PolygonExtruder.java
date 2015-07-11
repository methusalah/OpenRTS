package geometry.geom3d;

import geometry.collections.Ring;
import geometry.geom2d.Point2D;
import geometry.geom2d.Polygon;
import geometry.geom2d.algorithm.OffsetOperator;
import geometry.geom2d.algorithm.Triangulator;
import geometry.math.AngleUtil;

import java.util.logging.Logger;

public class PolygonExtruder {

	private static final Logger logger = Logger.getLogger(PolygonExtruder.class.getName());
	private static final double NO_SMOOTH_ANGLE = AngleUtil.toRadians(30);

	public MyMesh mesh;
	Polygon bottomPolygon;
	Polygon topPolygon;
	double bottom = 0;
	double top = 0;
	double bottomHorAngle = 0;
	double topHorAngle = 0;

	boolean horizontalNormalSet = false;
	double topNormalAngle = 0;
	double bottomNormalAngle = 0;


	double uOffset = 0;
	double vOffset = 0;
	double uScale = 1;
	double vScale = 1;

	public PolygonExtruder(Polygon p, double height) {
		mesh = new MyMesh();
		bottomPolygon = p;
		topPolygon = p;
		this.bottom = height;
		this.top = height;
	}


	public void setUV(double uOffset, double uScale, double vOffset, double vScale) {
		this.uOffset = uOffset;
		this.uScale = uScale;
		this.vOffset = vOffset;
		this.vScale = vScale;
	}

	public void setVFromPointToPoint(Point2D start, Point2D end) {
		// TODO attention ne marhce pas avec certains frustum
		Polygon p = bottomPolygon;

		double startLength = p.getLengthTo(start);
		double endLength = p.getLengthTo(end);
		double length;

		if(endLength > startLength) {
			length = endLength-startLength;
		} else if(startLength > endLength) {
			length = p.getLength()-startLength+endLength;
		} else {
			length = p.getLength();
		}

		vOffset = startLength;
		vScale = length/p.getLength();
	}

	public void setVFromPointWithSize(Point2D start, double size) {
		// TODO attention ne marhce pas avec certains frustum
		Polygon p = bottomPolygon;

		double startLength = p.getLengthTo(start);

		vOffset = startLength;
		vScale = size/p.getLength();
	}

	public void extrude(double height) {
		if(height < top && height > bottom) {
			throw new IllegalArgumentException("Specified height is inside mesh.");
		}

		Polygon polygon;
		double base;
		double sum;
		if(height > top) {
			polygon = topPolygon;
			base = top;
			sum = height;
		} else {
			polygon = bottomPolygon;
			base = height;
			sum = bottom;
		}

		for (Point2D p : polygon.points) {
			add6Indices(mesh.vertices.size());

			Point2D prev = polygon.points.getPrevious(p);
			Point2D next = polygon.points.getNext(p);
			Point2D nextNext = polygon.points.getNext(next);

			// add 4 vertices
			mesh.vertices.add(new Point3D(p, sum, 1));
			mesh.vertices.add(new Point3D(next, sum, 1));
			mesh.vertices.add(new Point3D(p, base, 1));
			mesh.vertices.add(new Point3D(next, base, 1));

			// add 4 normals
			double wPrevNormal = getSmoothedNormal(prev, p, next, 0);
			double wNextNormal = getSmoothedNormal(p, next, nextNext, 1);

			mesh.normals.add(new Point3D(Math.cos(wPrevNormal), 0, Math.sin(wPrevNormal)));
			mesh.normals.add(new Point3D(Math.cos(wNextNormal), 0, Math.sin(wNextNormal)));
			mesh.normals.add(new Point3D(Math.cos(wPrevNormal), 0, Math.sin(wPrevNormal)));
			mesh.normals.add(new Point3D(Math.cos(wNextNormal), 0, Math.sin(wNextNormal)));


			// add 4 textCoord
			double pLength = polygon.getLengthTo(p);
			double nextLength = polygon.getLengthTo(next);

			//			Logutil.logger.info("sum : "+sum+"base : "+base);
			mesh.textCoord.add(new Point2D((-uOffset+sum-base)/(sum-base)/uScale, 1-(pLength-vOffset)/polygon.getLength()/vScale));
			mesh.textCoord.add(new Point2D((-uOffset+sum-base)/(sum-base)/uScale, 1-(nextLength-vOffset)/polygon.getLength()/vScale));
			mesh.textCoord.add(new Point2D(-uOffset/(sum-base)/uScale, 1-(pLength-vOffset)/polygon.getLength()/vScale));
			mesh.textCoord.add(new Point2D(-uOffset/(sum-base)/uScale, 1-(nextLength-vOffset)/polygon.getLength()/vScale));
		}
		prepare(polygon, height);
	}

	/**
	 * This method is to give horizontal smooth lightning to extruded faces.
	 * must be called before extrusion.
	 * @param topNormalAngle
	 * @param bottomNormalAngle
	 */
	public void setHorizontalNormal(double topNormalAngle, double bottomNormalAngle) {
		this.topNormalAngle = topNormalAngle;
		this.bottomNormalAngle = bottomNormalAngle;
		horizontalNormalSet = true;
	}

	public void extrudeToFrustum(double offset, double height) {
		Ring<Double> offsets = new Ring<Double>();
		// TODO aie
		for(int i=0; i<bottomPolygon.size(); i++) {
			offsets.add(offset);
		}
		extrudeToFrustum(offsets, height);

	}

	public void extrudeToFrustum(Ring<Double> offsets, double height) {
		Polygon polygon;
		double base;
		double sum;
		if(height > top) {
			polygon = topPolygon;
			base = top;
			sum = height;
		} else {
			polygon = bottomPolygon;
			base = height;
			sum = bottom;
		}

		OffsetOperator op = new OffsetOperator(polygon);
		op.setOffsets(offsets);
		Polygon newPolygon = op.getRemainder();
		Ring<Integer> correspondences = op.correspondences;

		if(height < top && height > bottom) {
			throw new IllegalArgumentException("Specified height is inside mesh.");
		}


		for (int i = 0; i < polygon.points.size(); i++) {
			add6Indices(mesh.vertices.size());

			Point2D prev = polygon.points.getPrevious(i);
			Point2D p = polygon.points.get(i);
			Point2D next = polygon.points.getNext(i);
			Point2D nextNext = polygon.points.getNext(next);

			double offset = offsets.get(i);

			if(!horizontalNormalSet) {
				if(height > top) {
					topNormalAngle = new Point2D(offset, sum-base).getAngle()-AngleUtil.RIGHT;
				} else {
					topNormalAngle = new Point2D(offset, sum-base).getAngle()+AngleUtil.RIGHT;
				}
				bottomNormalAngle = topNormalAngle;
			}

			// add 4 vertices
			if(height > top) {
				mesh.vertices.add(new Point3D(newPolygon.points.get(correspondences.get(i)), sum, 1));
				mesh.vertices.add(new Point3D(newPolygon.points.get(correspondences.getNext(i)), sum, 1));
				mesh.vertices.add(new Point3D(p, base, 1));
				mesh.vertices.add(new Point3D(next, base, 1));
			} else {
				mesh.vertices.add(new Point3D(p, sum, 1));
				mesh.vertices.add(new Point3D(next, sum, 1));
				mesh.vertices.add(new Point3D(newPolygon.points.get(correspondences.get(i)), base, 1));
				mesh.vertices.add(new Point3D(newPolygon.points.get(correspondences.getNext(i)), base, 1));
			}

			// add 4 normals

			double wPrevNormal = getSmoothedNormal(prev, p, next, 0);
			double wNextNormal = getSmoothedNormal(p, next, nextNext, 1);

			mesh.normals.add(new Point3D(Point2D.ORIGIN.getTranslation(wPrevNormal, 1), topNormalAngle, 3));
			mesh.normals.add(new Point3D(Point2D.ORIGIN.getTranslation(wNextNormal, 1), topNormalAngle, 3));
			mesh.normals.add(new Point3D(Point2D.ORIGIN.getTranslation(wPrevNormal, 1), bottomNormalAngle, 3));
			mesh.normals.add(new Point3D(Point2D.ORIGIN.getTranslation(wNextNormal, 1), bottomNormalAngle, 3));

			// add 4 textCoord
			double width = p.getDistance(next);
			double heigth = sum-base;
			mesh.textCoord.add(new Point2D(0, heigth));
			mesh.textCoord.add(new Point2D(width, heigth));
			mesh.textCoord.add(new Point2D(0, 0));
			mesh.textCoord.add(new Point2D(width, 0));
		}
		prepare(newPolygon, height);
	}

	private void prepare(Polygon newPolygon, double height) {
		if(height>top) {
			top = height;
			topPolygon = newPolygon;
		} else if(height<bottom){
			bottom = height;
			bottomPolygon = newPolygon;
		} else {
			logger.info("bizarre !");
		}

		topNormalAngle = 0;
		bottomNormalAngle = 0;
		horizontalNormalSet = false;
	}

	public void closeBase() {
		Triangulator t = new Triangulator(bottomPolygon);
		int lastIndex = mesh.vertices.size();
		for (int i = t.getIndices().size() - 1; i >= 0; i--) {
			mesh.indices.add(t.getIndices().get(i) + lastIndex);
		}

		for (Point2D point : bottomPolygon.points) {
			mesh.vertices.add(new Point3D(point, bottom, 1));
			mesh.normals.add(new Point3D(0, -1, 0));
		}

		mesh.textCoord.addAll(bottomPolygon.getTextureMap());
	}

	public void closeSum() {
		Triangulator t = new Triangulator(topPolygon);
		int lastIndex = mesh.vertices.size();
		for (int i = 0; i < t.getIndices().size(); i++) {
			mesh.indices.add(t.getIndices().get(i) + lastIndex);
		}

		for (Point2D point : topPolygon.points) {
			mesh.vertices.add(new Point3D(point, top, 1));
			mesh.normals.add(new Point3D(0, 1, 0));
		}

		mesh.textCoord.addAll(topPolygon.getTextureMap());
	}

	private void add6Indices(int lastIndex) {
		mesh.indices.add(lastIndex); // topLeftCorner
		mesh.indices.add(lastIndex + 1); // topRightCorner
		mesh.indices.add(lastIndex + 2); // bottomLeftCorner

		mesh.indices.add(lastIndex + 2); // bottomLeftCorner
		mesh.indices.add(lastIndex + 1); // topRightCorner
		mesh.indices.add(lastIndex + 3); // bottomRightCorner
	}

	public void closeBaseWithChamfer(double startAngle, double endAngle, double size, boolean rounded) {
		if(endAngle < -AngleUtil.RIGHT || startAngle < endAngle) {
			throw new IllegalArgumentException();
		}

		double angle = endAngle-startAngle;

		int nbFaces;
		if(rounded) {
			nbFaces = 5;
		} else {
			nbFaces = 1;
		}

		double currentHorOffset = -size*(1-Math.cos(startAngle));;
		double currentVertOffset = size*Math.sin(startAngle);
		for(int i=0; i<nbFaces; i++) {
			double horOffset = -size*(1-Math.cos(startAngle+angle/nbFaces*(i+1)));
			double verOffset = size*Math.sin(startAngle+angle/nbFaces*(i+1));

			if(nbFaces != 1) {
				setHorizontalNormal(startAngle+angle/nbFaces*i, startAngle+angle/nbFaces*(i+1));
			}
			extrudeToFrustum(horOffset-currentHorOffset, bottom+verOffset-currentVertOffset);

			currentHorOffset = horOffset;
			currentVertOffset = verOffset;
		}

		closeBase();
	}

	public void closeSumWithChamfer(double startAngle, double endAngle, double size, boolean rounded) {
		if(endAngle > AngleUtil.RIGHT || startAngle > endAngle) {
			throw new IllegalArgumentException();
		}

		double angle = endAngle-startAngle;

		int nbFaces;
		if(rounded) {
			nbFaces = 5;
		} else {
			nbFaces = 1;
		}

		double currentHorOffset = -size*(1-Math.cos(startAngle));
		double currentVertOffset = size*Math.sin(startAngle);
		for(int i=0; i<nbFaces; i++) {
			double horOffset = -size*(1-Math.cos(startAngle+angle/nbFaces*(i+1)));
			double vertOffset = size*Math.sin(startAngle+angle/nbFaces*(i+1));

			if(nbFaces != 1) {
				setHorizontalNormal(startAngle+angle/nbFaces*(i+1), startAngle+angle/nbFaces*i);
			}
			extrudeToFrustum(horOffset-currentHorOffset, top+vertOffset-currentVertOffset);

			currentHorOffset = horOffset;
			currentVertOffset = vertOffset;
		}
		closeSum();
	}

	private double getSmoothedNormal(Point2D prev, Point2D p, Point2D next, int normalIndex) {
		double n = AngleUtil.normalize(next.getSubtraction(p).getAngle()-AngleUtil.RIGHT);
		double prevN = AngleUtil.normalize(p.getSubtraction(prev).getAngle()-AngleUtil.RIGHT);

		double diff = AngleUtil.getSmallestDifference(n, prevN);
		double bissector = AngleUtil.getBisector(prevN, n);
		double res;

		if(diff > NO_SMOOTH_ANGLE) {
			if(normalIndex == 0) {
				res = n;
			} else {
				res = prevN;
			}
		} else {
			res = bissector;
		}

		return res;
	}



}
