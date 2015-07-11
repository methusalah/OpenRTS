package geometry.geom2d;

import geometry.math.AngleUtil;

import java.util.ArrayList;

/**
 *
 * @author Benoît
 */
public class BoundingCircle extends BoundingShape {
 
    public Point2D center;
    public double radius;
    
    public BoundingCircle(Point2D center, double radius){
        this.center = center;
        this.radius = radius;
    }
    
    @Override
    public boolean collide(BoundingShape shape){
        if(shape instanceof BoundingCircle)
            return collideCircle((BoundingCircle)shape);
        if(shape instanceof AlignedBoundingBox)
            return collideABB((AlignedBoundingBox)shape);
        throw new IllegalArgumentException(shape.getClass().getSimpleName()+" is not yet supported.");
    }
    
    private boolean collideCircle(BoundingCircle o){
        if(center.getDistance(o.center) < radius+o.radius)
            return true;
        else
            return false;
    }
    
    public boolean contains(Point2D p){
        if(p.getDistance(center) < radius)
            return true;
        else
            return false;
    }
    
    private boolean collideABB(AlignedBoundingBox box) {
        // first we check collision between boxes
        if(box.collide(getABB())){
            // first case : the box has one of its sum inside the circle
            for(Point2D p : box.getPoints())
                if(contains(p))
                    return true;
            
            // second case : the box contains the center of the circle
            if(box.contains(center))
                return true;
            
            // third case : the projection of the center is on one of the box segment
            Segment2D hor = box.getEdges().get(0);
            Segment2D vert = box.getEdges().get(1);
            if(hor.containsProjected(center) ||
                    vert.containsProjected(center))
                return true;
            
            // if not, the bounding box of the circle touch, but the circle itself doesn't
            return false;
            
            
        }
        return false;
    }
    
    public AlignedBoundingBox getABB(){
        ArrayList<Point2D> bounds = new ArrayList<>();
        bounds.add(center.getTranslation(0, radius));
        bounds.add(center.getTranslation(AngleUtil.RIGHT, radius));
        bounds.add(center.getTranslation(-AngleUtil.RIGHT, radius));
        bounds.add(center.getTranslation(AngleUtil.FLAT, radius));
        return new AlignedBoundingBox(bounds);
    }

    @Override
    public Point2D getCenter() {
        return new Point2D(center);
    }
    
    
}
