package geometry.geom2d;

import java.util.ArrayList;

/**
 *
 * @author hcn
 */
abstract public class BoundingShape {
    abstract public boolean collide(BoundingShape s);
    
    public boolean collide(ArrayList<BoundingShape> shapes){
        for(BoundingShape s : shapes)
            if(collide(s))
                    return true;
        return false;
    }
    
    abstract public Point2D getCenter();
}
