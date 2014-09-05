package model.map;

import geometry.AlignedBoundingBox;
import geometry.BoundingShape;
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
    
    public BoundingShape getBounds() {
        ArrayList<Point2D> points = new ArrayList<>();
        points.add(getPos2D());
        points.add(getPos2D().getAddition(1, 0));
        points.add(getPos2D().getAddition(1, 1));
        points.add(getPos2D().getAddition(0, 1));
        return new AlignedBoundingBox(points);
    }
}
