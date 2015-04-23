package geometry.geom3d;

import geometry.collections.PointRing;
import geometry.collections.Ring;
import geometry.geom2d.Polygon;

/**
 *
 */
public class Polygon3D {
    
    public Ring<Point3D> points;
    public PointRing pr = new PointRing();
    public Polygon proj;

    public Polygon3D(Ring<Point3D> points) {
        this.points = points;
        
        for(Point3D p : points){
            pr.add(p.get2D());
        }
        proj = new Polygon(pr);
    }
    
    public Polygon3D getRotationAroundZ(double angle){
        Ring<Point3D> res = new Ring<>();
        for(Point3D p : points)
            res.add(p.get2D().getRotation(angle).get3D(p.z));
        return new Polygon3D(res);
    }
    
    public Polygon3D getTranslation(double x, double y, double z){
        Ring<Point3D> res = new Ring<>();
        for(Point3D p : points)
            res.add(p.getAddition(x, y, z));
        return new Polygon3D(res);
    }
    public Polygon3D getTranslation(Point3D o){
        return getTranslation(o.x, o.y, o.z);
    }

    
}
