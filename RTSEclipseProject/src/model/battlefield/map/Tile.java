package model.battlefield.map;

import geometry.AlignedBoundingBox;
import geometry.BoundingShape;
import geometry.Point2D;
import geometry.Segment2D;
import geometry3D.Point3D;

import java.util.ArrayList;
import java.util.Collection;

import math.MyRandom;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;

@Root
public class Tile {
    public static final double STAGE_HEIGHT = 2;

    public Map map;

    public Tile n;
    public Tile s;
    public Tile e;
    public Tile w;

    @Element
    public int x;
    @Element
    public int y;
    @Element
    public int level;
    @Element
    public double elevation = 0;
    @Element
    public boolean isCliff = false;
    @Element(required=false)
    public String cliffShapeID = "";

    public boolean elevatedForCliff = false;
    public Cliff cliff;
    public Ramp ramp;
    public boolean hasBlockingTrinket = false;

    public Tile(int x, int y, Map map){
        this.map = map;
        this.x = x;
        this.y = y;
        level = 0;
    }
    
    public Tile(@Element(name="x") int x,
            @Element(name="y") int y,
            @Element(name="level") int level,
            @Element(name="elevation") double elevation,
            @Element(name="isCliff") boolean isCliff,
            @Element(name="cliffShapeID") String cliffShapeID){
        this.x = x;
        this.y = y;
        this.level = level;
        this.elevation = elevation;
        this.isCliff = isCliff;
        this.cliffShapeID = cliffShapeID;
    }

    public int getNeighborsMaxLevel(){
        int res = Integer.MIN_VALUE;
        for(Tile n : map.get4Around(this))
            if( n.level>res)
                res = n.level;
        return res;
    }

    public int getNeighborsMinLevel(){
        int res = Integer.MAX_VALUE;
        for(Tile n : map.get4Around(this))
            if( n.level<res)
                res = n.level;
        return res;
    }

    public boolean isBlocked(){
        return isCliff() || hasBlockingTrinket;
    }

    public boolean isCliff(){
        return cliff != null;
    }

    public Point3D getPos(){
        return new Point3D(x, y, getZ());
    }

    public Point2D getPos2D() {
        return new Point2D(x, y);
    }
    
    public BoundingShape getBounds() {
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(getPos2D());
        points.add(getPos2D().getAddition(1, 0));
        points.add(getPos2D().getAddition(1, 1));
        points.add(getPos2D().getAddition(0, 1));
        return new AlignedBoundingBox(points);
    }
    
    public ArrayList<Tile> get4Neighbors(){
        return map.get4Around(this);
    }
    
    public ArrayList<Tile> get8Neighbors(){
        return map.get8Around(this);
    }

    public ArrayList<Tile> get9Neighbors(){
        return map.get9Around(this);
    }
    
    public void setCliff(){
        if(ramp != null && ramp.getCliffSlopeRate(this) == 1)
            return;
        if(!isCliff()){
            cliff = new Cliff(this);
            isCliff = true;
        }
    }
    public void unsetCliff(){
    	cliff.removeFromBattlefield();
        cliff = null;
        isCliff = false;
    }
    
    public void correctElevation(){
        if(elevatedForCliff)
            elevatedForCliff = false;
        
        if(isCliff() &&
                (w!=null && w.level > level ||
                s!=null && s.level > level ||
                w!=null && w.s!=null && w.s.level > level))
            elevatedForCliff = true;
        if(elevatedForCliff && ramp != null)
            elevation = -Tile.STAGE_HEIGHT*ramp.getSlopeRate(this);

    }
    
    public double getZ(){
        if(elevatedForCliff)
            return (level+1)*STAGE_HEIGHT+elevation;
        else
            return level*STAGE_HEIGHT+elevation;
    }
}
