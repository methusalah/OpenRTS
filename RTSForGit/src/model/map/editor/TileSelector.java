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
import model.map.Map;
import model.map.Tile;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class TileSelector {
    enum Shape {Square, Diamond, Circle}
    
    Map map;
    private Point2D pos;
    public Point2D alignedPos;
    public Shape shape = Shape.Square;
    public double radius = 2;

    public TileSelector(Map map) {
        this.map = map;
    }
    
    public void incRadius(){
        radius = Math.min(6, radius+0.25);
        LogUtil.logger.info("Selection size : "+radius*2);
    }
    public void decRadius(){
        radius = Math.max(0, radius-0.25);
        LogUtil.logger.info("Selection size : "+radius*2);
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
        alignedPos = null;
    }
    
    public void toggleShape(){
        switch(shape){
            case Circle : setSquare(); break;
            case Square : setDiamond(); break;
            case Diamond : setCircle(); break;
                default: throw new RuntimeException();
        }
        LogUtil.logger.info("Selection shape set to "+shape);
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
        return map.getTile(getAlignedPos());
    }
    
    private Point2D getAlignedPos(){
        if(alignedPos == null){
            if(radius > 1){
                int x = (int)Math.round(pos.x);
                int y = (int)Math.round(pos.y);
                if(x%2 != 0)
                    x--;
                if(y%2 != 0)
                    y--;
                alignedPos = new Point2D(x, y);
            }
        }
        return alignedPos;
    }
    
    private ArrayList<Tile> getTilesInCircle() {
        ArrayList<Tile> res = new ArrayList<>();
        BoundingCircle circle = new BoundingCircle(getAlignedPos(), radius);
        for(Tile t : map.getTiles()){
            if(circle.contains(t.getPos2D().getAddition(0.5, 0.5)))
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
        Point2D alignedPos = getAlignedPos();
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
        return map.getTile(getAlignedPos()).getZ();
    }
    
    public double getCenteringRatio(Tile t){
        switch(shape) {
            case Square :
                int xDist = (int)Math.abs(t.x-getAlignedPos().x);
                int yDist = (int)Math.abs(t.y-getAlignedPos().y);
                return (radius-Math.max(xDist, yDist))/radius;
            case Diamond :
                xDist = (int)Math.abs(t.x-getAlignedPos().x);
                yDist = (int)Math.abs(t.y-getAlignedPos().y);
                return (radius*2*1.414-xDist+yDist)/(radius*2*1.414);
            case Circle :
                return (radius-t.getPos2D().getDistance(getAlignedPos()))/radius;
        }
        return 0;
    }

}
