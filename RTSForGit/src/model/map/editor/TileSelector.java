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
    public Point2D pos;
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
    
    private Point2D getAlignedPos(){
        Point2D res = pos;
        if(radius > 1){
            int x = (int)Math.round(res.x);
            int y = (int)Math.round(res.y);
            if(x%2 != 0)
                x--;
            if(y%2 != 0)
                y--;
            res = new Point2D(x, y);
        }
        return res;
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

}
