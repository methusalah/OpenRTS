/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff;

import model.map.cliff.faces.NaturalFace;
import model.map.cliff.faces.CornerNaturalFace;
import model.map.cliff.faces.OrthogonalNaturalFace;
import model.map.cliff.faces.SalientNaturalFace;
import geometry.Point2D;
import math.Angle;
import static model.map.Tile.STAGE_HEIGHT;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class CliffOrganizer {
    
    public static NaturalFace createShape(Cliff c){
        double angle;
        Point2D pivot = c.getPos2D();

        if(c.n == null || c.s == null || c.e == null || c.w == null)
            return null;
        
        String s = c.getConnectedCliffs();
        switch(c.getConnectedCliffs()){
            // orthogonal
            case "ns" :
                if(c.e.z>c.w.z){
                        angle = Angle.FLAT;
                        c.parent = (Cliff)c.s;
                } else {
                        angle = 0;
                        c.parent = (Cliff)c.n;
                }
                c.type = Cliff.Type.Orthogonal;
                return new OrthogonalNaturalFace(c, angle, pivot);
            case "ew" :
                if(c.n.z>c.s.z){
                        angle = -Angle.RIGHT;
                        c.parent = (Cliff)c.e;
                } else {
                        angle = Angle.RIGHT;
                        c.parent = (Cliff)c.w;
                }
                c.type = Cliff.Type.Orthogonal;
                return new OrthogonalNaturalFace(c, angle, pivot);
            // digonal
            case "sw" :
                angle = 0;
                if(c.w.getNeighborsMaxLevel()>c.getNeighborsMaxLevel()){
                        c.parent = (Cliff)c.w;
                        c.type = Cliff.Type.Salient;
                        return new SalientNaturalFace(c, angle, pivot);
                } else {
                        c.parent = (Cliff)c.s;
                        c.type = Cliff.Type.Corner;
                        return new CornerNaturalFace(c, angle, pivot);
                }
            case "se" :
                angle = Angle.RIGHT;
                pivot = pivot.getAddition(1, 0);
                if(c.s.getNeighborsMaxLevel()>c.getNeighborsMaxLevel()){
                        c.parent = (Cliff)c.s;
                        c.type = Cliff.Type.Salient;
                        return new SalientNaturalFace(c, angle, pivot);
                } else {
                        c.parent = (Cliff)c.e;
                        c.type = Cliff.Type.Corner;
                        return new CornerNaturalFace(c, angle, pivot);
                }
            case "ne" :
                angle = Angle.FLAT;
                pivot = pivot.getAddition(1, 1);
                if(c.e.getNeighborsMaxLevel()>c.getNeighborsMaxLevel()){
                        c.parent = (Cliff)c.e;
                        c.type = Cliff.Type.Salient;
                        return new SalientNaturalFace(c, angle, pivot);
                } else {
                        c.parent = (Cliff)c.n;
                        c.type = Cliff.Type.Corner;
                        return new CornerNaturalFace(c, angle, pivot);
                }
            case "nw" :
                angle = -Angle.RIGHT;
                pivot = pivot.getAddition(0, 1);
                if(c.n.getNeighborsMaxLevel()>c.getNeighborsMaxLevel()){
                        c.parent = (Cliff)c.n;
                        c.type = Cliff.Type.Salient;
                        return new SalientNaturalFace(c, angle, pivot);
                } else {
                        c.parent = (Cliff)c.w;
                        c.type = Cliff.Type.Corner;
                        return new CornerNaturalFace(c, angle, pivot);
                }
            // ending cliff (for ramp end)
            case "n" :
                if(c.e.z>c.w.z){
                        angle = Angle.FLAT;
                } else {
                        angle = 0;
                        c.parent = (Cliff)c.n;
                }
                c.type = Cliff.Type.Orthogonal;
                return new OrthogonalNaturalFace(c, angle, pivot);
            case "s" :
                if(c.e.z>c.w.z){
                        angle = Angle.FLAT;
                        c.parent = (Cliff)c.s;
                } else {
                        angle = 0;
                }
                c.type = Cliff.Type.Orthogonal;
                return new OrthogonalNaturalFace(c, angle, pivot);
            case "e" :
                if(c.n.z>c.s.z){
                        angle = -Angle.RIGHT;
                        c.parent = (Cliff)c.e;
                } else {
                        angle = Angle.RIGHT;
                }
                c.type = Cliff.Type.Orthogonal;
                return new OrthogonalNaturalFace(c, angle, pivot);
            case "w" :
                if(c.n.z>c.s.z){
                        angle = -Angle.RIGHT;
                } else {
                        angle = Angle.RIGHT;
                        c.parent = (Cliff)c.w;
                }
                c.type = Cliff.Type.Orthogonal;
                return new OrthogonalNaturalFace(c, angle, pivot);
            default : LogUtil.logger.info("Cliff neighboring is strange for "+c);
                return null;
        }
    }
}
