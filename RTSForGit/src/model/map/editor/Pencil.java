/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor;

import collections.PointRing;
import geometry.BoundingCircle;
import geometry.Point2D;
import geometry.Polygon;
import geometry.Segment2D;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.map.Map;
import model.map.Tile;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Pencil {
    public static final int MAX_RADIUS = 6;
    public enum Shape {Square, Diamond, Circle}
    public enum Mode {Rough, Airbrush, Noise}
    
    Map map;
    private Point2D pos = Point2D.ORIGIN;
    public Point2D snappedPos;
    public Shape shape = Shape.Square;
    public Mode mode = Mode.Rough;
    public double radius = 2;
    public boolean snapPair = true;
    public boolean snapGrid = true;
    public boolean maintained = false;

    public Pencil(Map map) {
        this.map = map;
    }
    
    public void incRadius(){
        double increment = 1;
        if(!snapGrid)
            increment = 0.25;
        else if (!snapPair)
            increment = 0.5;
        radius = Math.min(MAX_RADIUS, radius+increment);
        LogUtil.logger.info("Pencil size : "+radius*2);
    }
    public void decRadius(){
        double increment = 1;
        if(!snapGrid)
            increment = 0.25;
        else if (!snapPair || radius == 1)
            increment = 0.5;
        radius = Math.max(increment, radius-increment);
        LogUtil.logger.info("Pencil size : "+radius*2);
    }
    
    private void setSquare(){
        shape = Shape.Square;
    }
    private void setDiamond(){
        shape = Shape.Diamond;
    }
    private void setCircle(){
        shape = Shape.Circle;
    }
    
    public void setPos(Point2D pos){
        this.pos = pos;
        snappedPos = null;
    }
    public Point2D getPos(){
        return pos;
    }
    
    public void toggleShape(){
        switch(shape){
            case Circle : setSquare(); break;
            case Square : setDiamond(); break;
            case Diamond : setCircle(); break;
                default: throw new RuntimeException();
        }
        LogUtil.logger.info("Pencil shape : "+shape);
    }
    
    public void toggleMode(){
        switch(mode){
            case Rough : mode = Mode.Airbrush; break;
            case Airbrush : mode = Mode.Noise; break;
            case Noise : mode = Mode.Rough; break;
                default: throw new RuntimeException();
        }
        LogUtil.logger.info("Pencil mode : "+mode);
    }
    
    public ArrayList<Tile> getTiles(){
        switch(shape){
            case Circle : return getTilesInCircle();
            case Diamond : 
            case Square : return getTilesInQuad();
                default: throw new RuntimeException();
        }
    }
    
    public Tile getCenterTile(){
        return map.getTile(getSnappedPos());
    }
    
    private Point2D getSnappedPos(){
        if(snappedPos == null){
            int x = (int)Math.floor(pos.x);
            int y = (int)Math.floor(pos.y);
            if(radius > 1 && snapPair){
                if(x%2 != 0)
                    x--;
                if(y%2 != 0)
                    y--;
            }
            snappedPos = new Point2D(x, y);
        }
        return snappedPos;
    }
    
    private ArrayList<Tile> getTilesInCircle() {
        ArrayList<Tile> res = new ArrayList<>();
        BoundingCircle circle = new BoundingCircle(getSnappedPos(), radius);
        for(Tile t : map.getTiles()){
            if(circle.contains(t.getPos2D()))
                res.add(t);
        }
        return res;
    }

    private ArrayList<Tile> getTilesInQuad() {
        ArrayList<Tile> res = new ArrayList<>();
        Polygon p = getOrientedQuad();
        for(Tile t : map.getTiles()){
            boolean inside = true;
            for(Segment2D s: p.getEdges())
                if(Angle.getTurn(s.getStart(), s.getEnd(), t.getPos2D().getAddition(0.5, 0.5)) != Angle.COUNTERCLOCKWISE){
                    inside = false;
                    break;
                }
            if(inside)
                res.add(t);
        }
        return res;
    }

    private Polygon getOrientedQuad(){
        Point2D alignedPos = getSnappedPos().getAddition(1, 1);
        PointRing pr = new PointRing();
        pr.add(alignedPos.getAddition(-radius, -radius));
        pr.add(alignedPos.getAddition(radius, -radius));
        pr.add(alignedPos.getAddition(radius, radius));
        pr.add(alignedPos.getAddition(-radius, radius));
        switch(shape){
            case Square : return new Polygon(pr);
            case Diamond : return new Polygon(pr).getRotation(Angle.RIGHT/2, alignedPos);
                default: throw new RuntimeException();
        }
    }
    
    public double getShapeAngle(){
        if(shape == Shape.Diamond)
            return Angle.RIGHT/2;
        return 0;
    }
    
    public double getElevation(){
        return map.getTile(getSnappedPos()).getZ();
    }
    
    private double getEccentricity(Point2D p){
        switch(shape) {
            case Square :
                double xDist = Math.abs(p.x-pos.x);
                double yDist = Math.abs(p.y-pos.y);
                return (radius-Math.max(xDist, yDist))/radius;
            case Diamond :
                xDist = Math.abs(p.x-pos.x);
                yDist = Math.abs(p.y-pos.y);
                return (radius*1.414-xDist-yDist)/(radius*1.414);
            case Circle :
                return (radius-p.getDistance(pos))/radius;
        }
        return 0;
    }
    
    public double getApplicationRatio(Point2D p){
        switch(mode){
            case Rough : return 1;
            case Airbrush : 
                double x = getEccentricity(p);
                x = x*10;
                x-=5;
                double localFalloff = 1/(1+Math.exp(-x));
                return localFalloff;
            case Noise : return MyRandom.next();
                default:throw new RuntimeException();
        }
    }
    
    public void release(){
        maintained = false;
    }
    
    public void maintain(){
        maintained = true;
    }

}
