package geometry.geom2d.intersection;

import geometry.geom2d.Circle2D;
import geometry.geom2d.Line2D;
import geometry.geom2d.Point2D;
import geometry.geom2d.Ray2D;

public class LineCircleIntersector {
	
	Line2D line;
	Point2D p0, p1;
	Circle2D circle;

	public Intersection intersection = new Intersection();
	
	public LineCircleIntersector(Line2D l, Circle2D c) {
		this.line = l;
		p0 = line.getStart();
		p1 = line.getEnd();
		Class cl = line.getClass();
		if(cl.equals(Line2D.class))
			p0 = p0.getTranslation(line.getAngle(), -1000000);
		if(cl.equals(Line2D.class) || cl.equals(Ray2D.class))
			p1 = p1.getTranslation(line.getAngle(), 1000000);
		this.circle = c;
		compute();
	}

    /**
     * look for the intersection between segment and a circle
     * 
     * two points are computed : the first is always the nearest of p0 (start of the segment
     * 
     * if tangent, the intersection is the first.
     * @param center
     * @param radius
     * @return 
     */
    public void compute() {
    	Point2D i1 = null, i2 = null;
    	
        // d = direction vector of the segment
        Point2D d = p1.getSubtraction(p0);
        // f = vector from center to ray start
        Point2D f = p0.getSubtraction(circle.center);
        
        double a = d.getDotProduct(d);
        double b = 2*f.getDotProduct(d);
        double c = f.getDotProduct(f)-circle.radius*circle.radius;

        double discriminant = b*b-4*a*c;
        if(discriminant >= 0){
        	// ray didn't totally miss sphere,
        	// so there is a solution to
        	// the equation.
        	discriminant = Math.sqrt(discriminant);

        	// either solution may be on or off the ray so need to test both
        	// t1 is always the smaller value, because BOTH discriminant and
        	// a are nonnegative.
        	double t1=(-b-discriminant)/(2*a);
        	double t2=(-b+discriminant)/(2*a);

        	// 3x HIT cases:
        	//          -o->             --|-->  |            |  --|->
        	// Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit), 

        	// 3x MISS cases:
        	//       ->  o                     o ->              | -> |
        	// FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)

        	if(t1 >= 0 && t1 <= 1)
        		// t1 is the intersection, and it's closer than t2
        		// (since t1 uses -b - discriminant)
        		// Impale, Poke
        		i1 = p0.getTranslation(line.getAngle(), t1*p0.getDistance(p1));

        	// here t1 didn't intersect so we are either started
        	// inside the sphere or completely past it
        	if(t2 >= 0 && t2 <= 1)
        		// ExitWound
        		i2 = p0.getTranslation(line.getAngle(), t2*p0.getDistance(p1));
        	// no intn: FallShort, Past, CompletelyInside
        }
        
        if(i1 != null)
        	if(i2 != null){
        		intersection.points.add(i1);
        		intersection.points.add(i2);
        	} else
        		intersection.point = i1;
    }
}



























