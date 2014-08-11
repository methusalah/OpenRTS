/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import java.util.ArrayList;
import math.Angle;

/**
 *
 * @author Benoît
 */
public class Ramp {
    ArrayList<Tile> tiles;
    int minX = Integer.MAX_VALUE;
    int maxX = 0;
    int minY = Integer.MAX_VALUE;
    int maxY = 0;
    
    double maxLevel = 0;
    double minLevel = Integer.MAX_VALUE;
    
    int length;
    int width;
    double angle;
    boolean longitudinal;
    
    public Ramp(ArrayList<Tile> tiles) {
        this.tiles = tiles;
        Tile start = null;
        
        // find the start, the bounds, and the height
        for(Tile t : tiles){
            t.ramp = this;
            if(t.rampStart)
                start = t;
            if(t.getNeighborsMaxLevel() > maxLevel)
                maxLevel = t.getNeighborsMaxLevel();
            if(t.getNeighborsMinLevel() < minLevel)
                minLevel = t.getNeighborsMinLevel();
            
            if(t.x<minX)
                minX = t.x;
            if(t.x>maxX)
                maxX = t.x;
            
            if(t.y<minY)
                minY = t.y;
            if(t.y>maxY)
                maxY = t.y;
        }
        if (start == null)
            throw new IllegalArgumentException("Ramp has no start");
        
        // compute the direction
        if(start.x == minX && start.y == minY)
            angle = -Angle.RIGHT;
        else if(start.x == maxX && start.y == minY)
            angle = 0;
        else if(start.x == maxX && start.y == maxY)
            angle = Angle.RIGHT;
        else if(start.x == minX && start.y == maxY)
            angle = Angle.FLAT;
        
        if(angle == Angle.RIGHT || angle == -Angle.RIGHT)
            longitudinal = true;
        else
            longitudinal = false;
        
        // compute cliffs, length, width
        
        if(longitudinal) {
            length = maxY-minY+1;
            width = maxX-minX+1;
        } else {
            length = maxX-minX+1;
            width = maxY-minY+1;
        }
        
        for(Tile t : tiles){
            if(longitudinal && (t.x == minX || t.x == maxX)){
                t.cliff = new Cliff(t);
            } else if(!longitudinal && (t.y == minY || t.y == maxY)){
                t.cliff = new Cliff(t);
            }
        }
        
        // compute height
        for(Tile t : tiles) {
            if(angle == Angle.RIGHT && t.x>minX)
                t.z += (2*(1-(double)(maxY-t.y)/length));
            
            else if(angle == -Angle.RIGHT && t.x>minX)
                t.z += (2*(double)(maxY-t.y)/length);
            
            else if(angle == 0 && t.y>minY)
                t.z += (2*(1-(double)(maxX+1-t.x)/length));
            
            else if(angle == Angle.FLAT && t.y>minY)
                t.z += (2*(double)(maxX+1-t.x)/length);
        }
        
        
        
        
    }
}
