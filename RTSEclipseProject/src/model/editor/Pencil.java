/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor;

import collections.PointRing;
import geometry.BoundingCircle;
import geometry.Point2D;
import geometry.Polygon;
import geometry.Segment2D;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Pencil {
    public static final int MAX_SIZE = 12;
    public enum Shape {Square, Diamond, Circle}
    public enum Mode {Unique, Rough, Airbrush, Noise}
    
    Map map;
    private Point2D pos = Point2D.ORIGIN;
    public Point2D snappedPos;
    public Shape shape = Shape.Square;
    public Mode mode = Mode.Rough;
    
    public double size = 1;
    public double sizeIncrement = 1;

    public double strength = 1;
    public double strengthIncrement = 0.1;
    
    public boolean snapPair = false;
    
    
    public boolean maintained = false;

    public Pencil(Map map) {
        this.map = map;
    }
    
    public void incRadius(){
        size = Math.min(MAX_SIZE, size+sizeIncrement);
    }
    public void decRadius(){
        size = Math.max(sizeIncrement, size-sizeIncrement);
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
    public Point2D getCoord(){
        return pos;
    }
    
    public void setSquareShape(){
        shape = Shape.Square;
    }
    
    public void setDiamondShape(){
        shape = Shape.Diamond;
    }
    
    public void setCircleShape(){
        shape = Shape.Circle;
    }
    
    public void setRoughMode(){
        mode = Mode.Rough;
    }
    public void setAirbrushMode(){
        mode = Mode.Airbrush;
    }
    public void setNoiseMode(){
        mode = Mode.Noise;
    }
    public void setUniqueMode(){
        mode = Mode.Unique;
    }
    public void toggleShape(){
        switch(shape){
            case Circle : setSquare(); break;
            case Square : setDiamond(); break;
            case Diamond : setCircle(); break;
                default: throw new RuntimeException();
        }
    }
    
    public void toggleMode(){
        if(mode == Mode.Unique)
            return;
        switch(mode){
            case Rough : mode = Mode.Airbrush; break;
            case Airbrush : mode = Mode.Noise; break;
            case Noise : mode = Mode.Rough; break;
                default: throw new RuntimeException();
        }
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
            if(size > 1 && snapPair){
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
        BoundingCircle circle = new BoundingCircle(getSnappedPos(), size/2);
        for(int x=-(int)size; x < (int)size; x++)
            for(int y=-(int)size; y < (int)size; y++){
            	Point2D p = new Point2D(x, y).getAddition(circle.center);
	            if(map.isInBounds(p) && circle.contains(p))
	                res.add(map.getTile(p));
            }
        return res;
    }

    private ArrayList<Tile> getTilesInQuad() {
        ArrayList<Tile> res = new ArrayList<>();
        Polygon p = getOrientedQuad();
        for(Tile t : map.getTiles()){
            boolean inside = true;
            for(Segment2D s: p.getEdges())
                if(Angle.getTurn(s.getStart(), s.getEnd(), t.getCoord().getAddition(0.5, 0.5)) != Angle.COUNTERCLOCKWISE){
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
        pr.add(alignedPos.getAddition(-size/2, -size/2));
        pr.add(alignedPos.getAddition(size/2, -size/2));
        pr.add(alignedPos.getAddition(size/2, size/2));
        pr.add(alignedPos.getAddition(-size/2, size/2));
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
                return ((size/2)-Math.max(xDist, yDist))/(size/2);
            case Diamond :
                xDist = Math.abs(p.x-pos.x);
                yDist = Math.abs(p.y-pos.y);
                return ((size/2)*1.414-xDist-yDist)/((size/2)*1.414);
            case Circle :
                return ((size/2)-p.getDistance(pos))/(size/2);
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
            case Unique : return 1;
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
