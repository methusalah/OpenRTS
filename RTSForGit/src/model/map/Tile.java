package model.map;

import geometry.AlignedBoundingBox;
import geometry.Point2D;
import geometry.Segment2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import java.util.Collection;
import math.MyRandom;

public class Tile {
	public Tile n;
	public Tile s;
	public Tile e;
	public Tile w;

        public int x;
	public int y;
        public int level;
	public double z;
	public Cliff cliff = null;
	
        public Ramp ramp = null;
        public boolean rampStart = false;
        public boolean rampComp = false;
        
	
	public Tile(int x, int y, int level, boolean isCliff) {
		this.x = x;
		this.y = y;
                this.level = level;
		this.z = level*2;
		if(isCliff)
			this.cliff = new Cliff(this);
	}
	
	public double getNeighborsMaxLevel(){
		double res = 0;
		if(n != null && n.level>res)
			res = n.level;

		if(s != null && s.level>res)
			res = s.level;
		
		if(e != null && e.level>res)
			res = e.level;
		
		if(w != null && w.level>res)
			res = w.level;
		return res;
	}

        public double getNeighborsMinLevel(){
		double res = Integer.MAX_VALUE;
		if(n != null && n.level<res)
			res = n.level;

		if(s != null && s.level<res)
			res = s.level;
		
		if(e != null && e.level<res)
			res = e.level;
		
		if(w != null && w.level<res)
			res = w.level;
		return res;
	}
	
	public boolean isCliff(){
		return cliff != null;
	}
        
        public Point3D getPos(){
            return new Point3D(x, y, z);
        }

    public Point2D getPos2D() {
        return new Point2D(x, y);
    }
    
    public ArrayList<Segment2D> getOffsetedBounds(double offset){
        ArrayList<Segment2D> res = new ArrayList<>();
        Segment2D nWall = new Segment2D(new Point2D(x-offset, y+1+offset), new Point2D(x+1+offset, y+1+offset));
        Segment2D sWall = new Segment2D(new Point2D(x-offset, y-offset), new Point2D(x+1+offset, y-offset));
        
        Segment2D eWall = new Segment2D(new Point2D(x+1+offset, y-offset), new Point2D(x+1+offset, y+1+offset));
        Segment2D wWall = new Segment2D(new Point2D(x-offset, y-offset), new Point2D(x-offset, y+1+offset));

        res.add(nWall);
        res.add(sWall);
        res.add(eWall);
        res.add(wWall);
        return res;
    }

    public AlignedBoundingBox getBoundingBox() {
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(getPos2D());
        points.add(getPos2D().getAddition(1, 0));
        points.add(getPos2D().getAddition(1, 1));
        points.add(getPos2D().getAddition(0, 1));
        return new AlignedBoundingBox(points);
    }
}
