package model.map;

import geometry.Line2D;
import geometry.Point2D;
import geometry3D.Point3D;
import geometry3D.Triangle3D;
import java.awt.Color;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;

import ressources.Image;
import tools.LogUtil;

public class Map {
	
	Tile[][] tiles;
        ArrayList<Tile> tileList = null;
        ArrayList<Ramp> ramps = new ArrayList<Ramp>();
	public int width;
	public int height;
	
	public Map(int width, int height){
            this.width = width;
            this.height = height;
            tiles = new Tile[width][height];
	}
        
	public ArrayList<Tile> getTiles() {
            if(tileList == null){
                tileList = new ArrayList<Tile>();
                for(int x=0; x<width; x++)
                        for(int y=0; y<height; y++)
                                tileList.add(tiles[x][y]);
            }
            return tileList;
	}
        
        public Tile getTile(int x, int y) {
            return tiles[x][y];
        }
        
        public double getGroundAltitude(Point2D pos) {
            Tile t = getTile(pos);
            if(t.n==null || t.s==null || t.e==null || t.w== null)
                return 0;

            Point2D tPos2D = new Point2D(t.x, t.y);
            Point2D tnePos2D = new Point2D(t.e.n.x, t.e.n.y);
            
            Point3D nw = t.n.getPos();
            Point3D ne = t.n.e.getPos();
            Point3D sw = t.getPos();
            Point3D se = t.e.getPos();
            Triangle3D tr;
            
            if(Angle.getTurn(tPos2D, tnePos2D, pos) == Angle.CLOCKWISE)
                tr = new Triangle3D(sw, se, ne);
            else
                tr = new Triangle3D(sw, ne, nw);
//            LogUtil.logger.info("z = "+tr.getElevated(pos).z);
            
            return tr.getElevated(pos).z;
        }

    public boolean isBlocked(int x, int y) {
        if(getTile(x, y).isCliff())
            return true;
        return false;
    }

    public Tile getTile(Point2D p) {
        return getTile((int)Math.floor(p.x), (int)Math.floor(p.y));
    }

    public boolean meetObstacle(Point2D p1, Point2D p2) {
        Line2D l = new Line2D(p2, p1);
        double a = l.getSlope();
        double b = p1.y-a*p1.x;
        
        double minX = Math.min(p1.x, p2.x);
        double maxX = Math.max(p1.x, p2.x);
        // ugly test
        if(minX == maxX)
            return true;
        
        for(double i = minX; i<maxX; i+=0.1){
            double y = a*i+b;
            try {
                Tile t = getTile((int)Math.floor(i), (int)Math.floor(y));
                if(t.isCliff())
                    return true;
            } catch (Exception e) {
                LogUtil.logger.info("bug avec la droite à l'index : "+i+" ou y = "+y);
                LogUtil.logger.info(""+p1+" et "+p2);
                LogUtil.logger.info("pente :"+a+" et b : "+b);
                throw new RuntimeException();
            }
        }

            
        return false;
    }
    
    public boolean isInBounds(Point2D p){
        if(p.x < 0 ||
                p.y < 0 ||
                p.x > width-1 ||
                p.y > height-1)
            return false;
        return true;

    }
    public boolean isWalkable(Point2D p){
        if(!isInBounds(p))
            return false;
        if(getTile(p).isCliff())
            return false;
        return true;
        
    }
}
