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

    /*
     * Fast Voxel Traversal Algorithm for Ray Tracing
     * John Amanatides
     * Andrew Woo
     */
    public boolean meetObstacle(Point2D p1, Point2D p2) {
      // calculate the direction of the ray (linear algebra)
        double dirX = p2.x-p1.x;
        double dirY = p2.y-p1.y;
        double length = Math.sqrt(dirX * dirX + dirY * dirY);
        dirX /= length; // normalize the direction vector
        dirY /= length;
        double tDeltaX = 1/Math.abs(dirX); // how far we must move in the ray direction before we encounter a new voxel in x-direction
        double tDeltaY = 1/Math.abs(dirY); // same but y-direction
 
        // start voxel coordinates
        int x = (int)Math.floor(p1.x);  // use your transformer function here
        int y = (int)Math.floor(p1.y);
 
        // end voxel coordinates
        int endX = (int)Math.floor(p2.x);
        int endY = (int)Math.floor(p2.y);
 
        // decide which direction to start walking in
        int stepX = (int) Math.signum(dirX);
        int stepY = (int) Math.signum(dirY);
 
        double tMaxX, tMaxY;
        // calculate distance to first intersection in the voxel we start from
        if(dirX < 0)
            tMaxX = ((double)x-p1.x)/dirX;
        else
            tMaxX = ((double)(x+1)-p1.x)/dirX;
 
        if(dirY < 0)
            tMaxY = ((double)y-p1.y)/dirY;
        else
            tMaxY = ((double)(y+1)-p1.y) / dirY;
 
        // check if first is occupied
        if(getTile(x, y).isCliff())
            return true;
        boolean reachedX = false, reachedY = false;
        while(!reachedX || !reachedY){
            if(tMaxX < tMaxY){
                tMaxX += tDeltaX;
                x += stepX;
            }else{
                tMaxY += tDeltaY;
                y += stepY;
            }
            if(getTile(x, y).isCliff())
                return true;

            if(stepX > 0){
                if (x >= endX)
                    reachedX = true;
            }else if (x <= endX)
                reachedX = true;
 
            if(stepY > 0){
                if (y >= endY)
                    reachedY = true;
            }else if (y <= endY)
                reachedY = true;
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
