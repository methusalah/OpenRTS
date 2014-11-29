package model.map;

import geometry.AlignedBoundingBox;
import geometry.BoundingShape;
import geometry.Point2D;
import geometry.Segment2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import java.util.Collection;
import math.MyRandom;
import model.map.cliff.Cliff;

public class Tile {
    
    public static final double STAGE_HEIGHT = 2;
    public Tile n;
    public Tile s;
    public Tile e;
    public Tile w;

    public int x;
    public int y;
    public int level;
    public double z;

    public Cliff cliff;

    public Tile(TileDef def){
        x = def.x;
        y = def.y;
        z = def.z;
        level = def.level;
    }
    public Tile(int x, int y){
        this.x = x;
        this.y = y;
        z = 0;
        level = 0;
    }

    public int getNeighborsMaxLevel(){
            int res = 0;
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

    public int getNeighborsMinLevel(){
            int res = Integer.MAX_VALUE;
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

    public boolean isBlocked(){
        return isCliff();
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
    
    public ArrayList<Tile> get4Neighbors(){
        ArrayList<Tile> res = new ArrayList<>();
        if(n!=null)
            res.add(n);
        if(s!=null)
            res.add(s);
        if(e!=null)
            res.add(e);
        if(w!=null)
            res.add(w);
        return res;
    }
    
    public ArrayList<Tile> get8Neighbors(){
        ArrayList<Tile> res = new ArrayList<>();
        if(n!=null){
            res.add(n);
            if(n.e!=null)
                res.add(n.e);
            if(n.w!=null)
                res.add(n.w);
        }
        if(s!=null){
            res.add(s);
            if(s.e!=null)
                res.add(s.e);
            if(s.w!=null)
                res.add(s.w);
        }
        if(e!=null)
            res.add(e);
        if(w!=null)
            res.add(w);
        return res;
        
    }
    
    public void setCliff(){
        if(!isCliff())
            cliff = new Cliff(this);
    }
}
