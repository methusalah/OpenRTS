package model.battlefield.army.motion;

import geometry.geom3d.Point3D;

public class Motion {
	public double distance = 0;
	public double angle = Double.NaN;
	public Point3D velocity = Point3D.ORIGIN;
	
	public boolean isEmpty(){
		return distance == 0 &&
				angle == 0 &&
				velocity.isOrigin();
	}
	
	public boolean is3D(){
		return !velocity.isOrigin();
	}
	
	public boolean hasRotation(){
		return !Double.isNaN(angle);
	}
}
