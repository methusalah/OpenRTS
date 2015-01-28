package model.battlefield.map;

import geometry.AlignedBoundingBox;
import geometry.BoundingShape;
import geometry.Point2D;
import geometry.Segment2D;
import geometry3D.Point3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.RuntimeErrorException;

import math.MyRandom;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;

import tools.LogUtil;
import collections.Ring;

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
    @Element(required=false)
    public String cliffShapeID = "";

    public boolean elevatedForCliff = false;
    private Cliff cliff0;
    private Cliff cliff1;
    private Cliff cliff2;
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
            @Element(name="cliffShapeID") String cliffShapeID){
        this.x = x;
        this.y = y;
        this.level = level;
        this.elevation = elevation;
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
        return hasCliff() || hasBlockingTrinket;
    }

    public boolean hasCliff(){
    	for(int i = 0; i<3; i++)
    		if(hasCliffOnLevel(i))
    			return true;
    	return false;
    }
    public boolean hasCliffOnLevel(int level){
        return getCliff(level) != null;
    }

    public Point3D getPos(){
        return new Point3D(x, y, getZ());
    }

    public Point2D getCoord() {
        return new Point2D(x, y);
    }
    
    public BoundingShape getBounds() {
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(getCoord());
        points.add(getCoord().getAddition(1, 0));
        points.add(getCoord().getAddition(1, 1));
        points.add(getCoord().getAddition(0, 1));
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
    
    public void setCliff(int minLevel, int maxLevel){
        if(ramp != null && ramp.getCliffSlopeRate(this) == 1)
            return;
        for(int level = minLevel; level < maxLevel; level++)
        	if(getCliff(level) == null)
        		setCliff(level, new Cliff(this, level));
        
    }
    
    public Cliff getCliff(int level){
    	switch(level){
		case 0 : return cliff0; 
		case 1 : return cliff1; 
		case 2 : return cliff2;
		default : throw new IllegalArgumentException(level +" is not valid cliff level ");
    	}
    }
    
    private void setCliff(int level, Cliff cliff){
    	switch(level){
		case 0 : cliff0 = cliff; break;
		case 1 : cliff1 = cliff; break;
		case 2 : cliff2 = cliff; break;
		default : throw new IllegalArgumentException(level +" is not valid cliff level ");
    	}
    }
    
    public void unsetCliff(){
    	for(int level = 0; level<3; level++)
    		if(getCliff(level) != null){
    			getCliff(level).removeFromBattlefield();
    			setCliff(level, null);
    		}
    }
    
    public void correctElevation(){
        if(elevatedForCliff)
            elevatedForCliff = false;
        
        if(hasCliff() &&
                (w!=null && w.level > level ||
                s!=null && s.level > level ||
                w!=null && w.s!=null && w.s.level > level))
            elevatedForCliff = true;
        if(elevatedForCliff && ramp != null)
            elevation = -Tile.STAGE_HEIGHT*ramp.getSlopeRate(this);

    }
    
    public double getZ(){
    	if(hasCliff())
    		if(elevatedForCliff)
    			return (getUpperCliff().level+1)*STAGE_HEIGHT+elevation;
    		else
    			return (getLowerCliff().level)*STAGE_HEIGHT+elevation;
    			
        else
            return level*STAGE_HEIGHT+elevation;
    }
    
    public Cliff getLowerCliff(){
    	for(int i = 0; i<3; i++)
    		if(getCliff(i) != null)
    			return getCliff(i);
    	throw new RuntimeException("Tile as no cliff "+this);
    }
    public Cliff getUpperCliff(){
    	Cliff res = getLowerCliff();
    	for(int i = res.level+1; i<3; i++)
    		if(getCliff(i) != null)
    			res = getCliff(i);
    	return res;
    }
    public List<Cliff> getCliffs(){
    	List<Cliff> res = new ArrayList<>();
    	for(int i = 0; i<3; i++)
    		if(getCliff(i) != null)
    			res.add(getCliff(i));
    	return res;
    	
    }
}
