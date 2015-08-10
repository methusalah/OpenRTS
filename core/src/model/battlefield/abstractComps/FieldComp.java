package model.battlefield.abstractComps;

import geometry.geom2d.BoundingCircle;
import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;

import java.awt.Color;

import model.EntityManager;

/**
 * A basic abstract entity, containing all needed attributes and methods to set a static object on the battlefield
 */
public class FieldComp {
	public String modelPath = "";
	public Point3D pos;
	protected final double radius;
	public double scaleX = 1;
	public double scaleY = 1;
	public double scaleZ = 1;
	public Color color;
	protected double roll = 0;
	protected double pitch = 0;
	protected double yaw = 0;
	private final long id;

	private Point3D direction;
	private Point3D upDirection = Point3D.UNIT_Z;

	public FieldComp(Point3D pos, double yaw, double radius) {
		this.pos = pos;
		setOrientation(yaw);
		this.radius = radius;
		this.id = EntityManager.getNewEntityId();
	}

	public Point2D getCoord() {
		return pos.get2D();
	}

	public Point3D getPos() {
		return pos;
	}

	public double getRadius() {
		return radius;
	}

	public double getDistance(FieldComp o) {
		return pos.getDistance(o.pos);
	}

	public Point3D getVectorTo(FieldComp o) {
		return o.pos.getSubtraction(pos);
	}

	public FieldComp getNearest(FieldComp o1, FieldComp o2) {
		if (getDistance(o1) < getDistance(o2)) {
			return o1;
		}
		return o2;
	}

	public BoundingCircle getBounds() {
		return new BoundingCircle(new Point2D(pos), radius);
	}

	public double getSpacing(FieldComp o) {
		return radius + o.radius;
	}

	public double getBoundsDistance(FieldComp o) {
		return getDistance(o) - getSpacing(o);
	}

	public boolean collide(FieldComp o) {
		return getBounds().collide(o.getBounds());
	}

	public long getId() {
		return id;
	}

	public void setPos(Point3D newPos){
		pos = newPos;
	}

	public Point3D getDirection() {
		return direction;
	}

	public void setDirection(Point3D direction) {
		this.direction = direction;
		yaw = direction.get2D().getAngle();
	}

	public Point3D getUpDirection() {
		return upDirection;
	}

	public void setUpDirection(Point3D upDirection) {
		this.upDirection = upDirection;
	}

	public void setOrientation(double yaw) {
		this.yaw = yaw;
		direction = Point2D.ORIGIN.getTranslation(yaw, 1).get3D(0); 
	}
	
	public double getOrientation() {
		return yaw;
	}
	
	public double getRoll() {
		return roll;
	}

	public double getPitch() {
		return pitch;
	}
}
