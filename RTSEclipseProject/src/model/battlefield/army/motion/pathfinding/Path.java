/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.motion.pathfinding;

import geometry.Point2D;
import java.util.ArrayList;

/**
 *
 * @author Benoît
 */
public class Path extends ArrayList<Point2D>{
    
    Point2D lastDiscarded;
    
    public Point2D getFirstWaypoint(){
        if(isEmpty())
            throw new IllegalStateException("Empty path");
        return get(0);
    }
    
    public Point2D getLastWaypoint(){
        if(isEmpty())
            throw new IllegalStateException("Empty path");
        return get(size()-1);
    }
    
    public void discardFirstWaypoint(){
        lastDiscarded = get(0);
        remove(0);
    }

    public Point2D getLastDiscarded() {
        return lastDiscarded;
    }
    
}
