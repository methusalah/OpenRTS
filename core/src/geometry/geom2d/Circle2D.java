package geometry.geom2d;

public class Circle2D {

    public final Point2D center;
    public final double radius;
    
    public Circle2D(Point2D center, double radius){
        this.center = center;
        this.radius = radius;
    }
    
    private void check(){
		boolean valid = true;
		if (center == null)
			valid = false;
		if (radius <= 0)
			valid = false;

		if (!valid)
			throw new RuntimeException("Can't construct invalid "+this.getClass().getSimpleName()+" : " + this);
    }
    
    public boolean hasInside(Point2D p){
        if(p.getDistance(center) < radius)
            return true;
        return false;
    }

    public boolean isOnBound(Point2D p){
        if(p.getDistance(center) == radius)
            return true;
        return false;
    }
    
    public Circle2D getTransformed(Transform2D transform){
    	if(transform.scale.x != transform.scale.y)
    		throw new IllegalArgumentException("Can't scale a "+this.getClass().getSimpleName()+" with different x and y factors.");
    	return new Circle2D(center.getTransformed(transform), radius*transform.scale.x);
    }


}
