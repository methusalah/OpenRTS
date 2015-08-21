package model.battlefield.army.motion;

import geometry.geom3d.Point3D;

public class Motion {
	private double distance = 0;
	private double angle = Double.NaN;
	private Point3D velocity = Point3D.ORIGIN;
	
	
	public Motion() {
	}

	public boolean isEmpty(){
		return distance == 0 &&
				!hasRotation() &&
				velocity.isOrigin();
	}
	
	public boolean is3D(){
		return !velocity.isOrigin();
	}
	
	public boolean hasRotation(){
		return !Double.isNaN(angle);
	}
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public Point3D getVelocity() {
		return velocity;
	}

	public void setVelocity(Point3D velocity) {
		this.velocity = velocity;
	}

}
