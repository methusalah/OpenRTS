/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import java.util.ArrayList;
import math.Angle;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Ramp {
    int minX = Integer.MAX_VALUE;
    int maxX = 0;
    int minY = Integer.MAX_VALUE;
    int maxY = 0;
    
    int maxLevel = 0;
    int minLevel = Integer.MAX_VALUE;
    
    int length;
    int width;
    double angle;
    boolean longitudinal;
    boolean urban;
    
    
//    public void finalise() {
//        // find the bounds
//        for(TileDef t : defs){
//            if(t.x<minX)
//                minX = t.x;
//            if(t.x>maxX)
//                maxX = t.x;
//            
//            if(t.y<minY)
//                minY = t.y;
//            if(t.y>maxY)
//                maxY = t.y;
//        }
//        
//        // determine ramp direction
//        // ramp direction represent the rise of the slope
//        if(start.x == minX && start.y == minY)
//            angle = -Angle.RIGHT;
//        else if(start.x == maxX && start.y == minY)
//            angle = 0;
//        else if(start.x == maxX && start.y == maxY)
//            angle = Angle.RIGHT;
//        else if(start.x == minX && start.y == maxY)
//            angle = Angle.FLAT;
//        
//        if(angle == Angle.RIGHT || angle == -Angle.RIGHT)
//            longitudinal = true;
//        else
//            longitudinal = false;
//        
//        // compute sizes
//        if(longitudinal) {
//            length = maxY-minY+1;
//            width = maxX-minX+1;
//        } else {
//            length = maxX-minX+1;
//            width = maxY-minY+1;
//        }
//        
//        // declare cliffs in tile definitions
//        for(TileDef def : defs){
//            if(longitudinal && (def.x == minX || def.x == maxX) ||
//                    !longitudinal && (def.y == minY || def.y == maxY)){
//                def.cliff = true;
//                def.setLevel(minLevel);
//                def.urban = urban;
//            } else
//                def.setLevel(maxLevel);
//                
//        }
//        
//        // compute ground z
//        for(TileDef def : defs) {
//            if(angle == Angle.RIGHT && def.x>minX+1)
//                def.z -= Tile.STAGE_HEIGHT*(double)(maxY-def.y)/length;
//            
//            else if(angle == -Angle.RIGHT && def.x>minX+1)
//                def.z -= Tile.STAGE_HEIGHT*(1-(double)(maxY-def.y)/length);
//            
//            else if(angle == 0 && def.y>minY+1)
//                def.z -= Tile.STAGE_HEIGHT*(double)(maxX+1-def.x)/length;
//            
//            else if(angle == Angle.FLAT && def.y>minY+1)
//                def.z -= Tile.STAGE_HEIGHT*(1-(double)(maxX+1-def.x)/length);
//        }
//    }
}
