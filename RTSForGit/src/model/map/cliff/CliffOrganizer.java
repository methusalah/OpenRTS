/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff;

import model.map.cliff.faces.natural.NaturalFace;
import model.map.cliff.faces.natural.Dug1Corner;
import model.map.cliff.faces.natural.Dug1Ortho;
import model.map.cliff.faces.natural.Dug1Salient;
import geometry.Point2D;
import math.Angle;
import model.map.Tile;
import static model.map.Tile.STAGE_HEIGHT;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class CliffOrganizer {
    
    public static void organize(Cliff c){
        Tile t = c.tile;
        Tile n = c.tile.n;
        Tile s = c.tile.s;
        Tile e = c.tile.e;
        Tile w = c.tile.w;

        if(n == null || s == null || e == null || w == null){
            c.type = Cliff.Type.Border;
            return;
        }
        
        if(c.getUpperGrounds().size()>5){
            c.type = Cliff.Type.Bugged;
            return;
        }
        
        switch(c.getConnectedCliffs()){
            // orthogonal
            case "ns" :
                if(e.level>w.level){
                        c.angle = Angle.FLAT;
                        c.setParent(s.cliff);
                } else {
                        c.angle = 0;
                        c.setParent(n.cliff);
                }
                c.type = Cliff.Type.Orthogonal;
                break;
            case "ew" :
                if(n.level>s.level){
                        c.angle = -Angle.RIGHT;
                        c.setParent(e.cliff);
                } else {
                        c.angle = Angle.RIGHT;
                        c.setParent(w.cliff);
                }
                c.type = Cliff.Type.Orthogonal;
                break;

                
            // digonal
            case "sw" :
                c.angle = 0;
                if(w.getNeighborsMaxLevel()>t.getNeighborsMaxLevel()){
                        c.setParent(w.cliff);
                        c.type = Cliff.Type.Salient;
                } else {
                        c.setParent(s.cliff);
                        c.type = Cliff.Type.Corner;
                }
                break;
            case "se" :
                c.angle = Angle.RIGHT;
                if(s.getNeighborsMaxLevel()>t.getNeighborsMaxLevel()){
                        c.setParent(s.cliff);
                        c.type = Cliff.Type.Salient;
                } else {
                        c.setParent(e.cliff);
                        c.type = Cliff.Type.Corner;
                }
                break;
            case "ne" :
                c.angle = Angle.FLAT;
                if(e.getNeighborsMaxLevel()>t.getNeighborsMaxLevel()){
                        c.setParent(e.cliff);
                        c.type = Cliff.Type.Salient;
                } else {
                        c.setParent(n.cliff);
                        c.type = Cliff.Type.Corner;
                }
                break;
            case "nw" :
                c.angle = -Angle.RIGHT;
                if(n.getNeighborsMaxLevel()>t.getNeighborsMaxLevel()){
                        c.setParent(n.cliff);
                        c.type = Cliff.Type.Salient;
                } else {
                        c.setParent(w.cliff);
                        c.type = Cliff.Type.Corner;
                }
                break;
                
                
            // ending cliff (for ramp end)
            case "n" :
                if(e.level>w.level){
                        c.angle = Angle.FLAT;
                } else {
                        c.angle = 0;
                        c.setParent(n.cliff);
                }
                c.type = Cliff.Type.Orthogonal;
                break;
            case "s" :
                if(e.level>w.level){
                        c.angle = Angle.FLAT;
                        c.setParent(s.cliff);
                } else {
                        c.angle = 0;
                }
                c.type = Cliff.Type.Orthogonal;
                break;
            case "e" :
                if(n.level>s.level){
                        c.angle = -Angle.RIGHT;
                        c.setParent(e.cliff);
                } else {
                        c.angle = Angle.RIGHT;
                }
                c.type = Cliff.Type.Orthogonal;
                break;
            case "w" :
                if(n.level>s.level){
                        c.angle = -Angle.RIGHT;
                } else {
                        c.angle = Angle.RIGHT;
                        c.setParent(w.cliff);
                }
                c.type = Cliff.Type.Orthogonal;
                break;
            default : LogUtil.logger.info("Cliff neighboring is strange at "+c.tile.getPos2D()+" : "+c.getConnectedCliffs());
                c.type = Cliff.Type.Bugged;
        }
    }
}
