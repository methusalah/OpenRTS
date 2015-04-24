package model.battlefield.map.cliff.faces;

import geometry.collections.Ring;
import geometry.geom3d.Point3D;
import model.battlefield.map.cliff.Cliff;

/**
 * Abstract class that defines the base element for all type of Faces
 * 
 * A face build three elements :
 *  - the lower ground shape
 *  - the upper ground shape
 *  - its own shape
 * 
 */
public abstract class Face {
    final protected Cliff cliff;
	
    public Face(Cliff cliff) {
    	this.cliff = cliff;
	}
    public abstract Ring<Point3D> getLowerGround();
    public abstract Ring<Point3D> getUpperGround();
    
    public Ring<Point3D> getRotation(Ring<Point3D> ground){
    	Ring<Point3D> res = new Ring<>();
    	for(Point3D p : ground)
    		res.add(p.getRotationAroundZ(cliff.angle));
    	return res;
    	
    }
    public abstract String getType();
}
