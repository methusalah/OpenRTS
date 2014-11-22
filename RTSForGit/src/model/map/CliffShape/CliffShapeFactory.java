/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.CliffShape;

import geometry.Point2D;
import math.Angle;
import model.map.Cliff;
import static model.map.Tile.STAGE_HEIGHT;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class CliffShapeFactory {
    
    public static CliffShape getSpecialised(Cliff c){
        double angle;
        Point2D pivot = c.getPos2D();

        if(c.n == null || c.s == null || c.e == null || c.w == null)
            return null;
        
        // orthogonal
        if(c.n.isCliff() && c.s.isCliff()){
                if(c.e.z>c.w.z){
                        angle = Angle.FLAT;
                        c.parent = (Cliff)c.s;
                } else {
                        angle = 0;
                        c.parent = (Cliff)c.n;
                }
                return new OrthogonalCliffShape(c, angle, pivot);
        } else if(c.e.isCliff() && c.w.isCliff()) {
                if(c.n.z>c.s.z){
                        angle = -Angle.RIGHT;
                        c.parent = (Cliff)c.e;
                } else {
                        angle = Angle.RIGHT;
                        c.parent = (Cliff)c.w;
                }
                return new OrthogonalCliffShape(c, angle, pivot);
        // digonal	
        } else if(c.w.isCliff() && c.s.isCliff()) {
                angle = 0;
                if(c.w.getNeighborsMaxLevel()>c.getNeighborsMaxLevel()){
                        c.parent = (Cliff)c.w;
                        return new SalientCliffShape(c, angle, pivot);
                } else {
                        c.parent = (Cliff)c.s;
                        return new CornerCliffShape(c, angle, pivot);
                }
        } else if(c.s.isCliff() && c.e.isCliff()) {
                angle = Angle.RIGHT;
                pivot = pivot.getAddition(1, 0);
                if(c.s.getNeighborsMaxLevel()>c.getNeighborsMaxLevel()){
                        c.parent = (Cliff)c.s;
                        return new SalientCliffShape(c, angle, pivot);
                } else {
                        c.parent = (Cliff)c.e;
                        return new CornerCliffShape(c, angle, pivot);
                }
        } else if(c.e.isCliff() && c.n.isCliff()) {
                angle = Angle.FLAT;
                pivot = pivot.getAddition(1, 1);
                if(c.e.getNeighborsMaxLevel()>c.getNeighborsMaxLevel()){
                        c.parent = (Cliff)c.e;
                        return new SalientCliffShape(c, angle, pivot);
                } else {
                        c.parent = (Cliff)c.n;
                        return new CornerCliffShape(c, angle, pivot);
                }
        } else if(c.n.isCliff() && c.w.isCliff()) {
                angle = -Angle.RIGHT;
                pivot = pivot.getAddition(0, 1);
                if(c.n.getNeighborsMaxLevel()>c.getNeighborsMaxLevel()){
                        c.parent = (Cliff)c.n;
                        return new SalientCliffShape(c, angle, pivot);
                } else {
                        c.parent = (Cliff)c.w;
                        return new CornerCliffShape(c, angle, pivot);
                }
        } else throw new RuntimeException("Error with cliff neighboring."+c.getPos2D());
    }
}
