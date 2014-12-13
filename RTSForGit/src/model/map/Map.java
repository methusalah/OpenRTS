package model.map;

import geometry.Line2D;
import geometry.Point2D;
import geometry3D.Point3D;
import geometry3D.Triangle3D;
import java.awt.Color;
import java.awt.List;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.map.cliff.Cliff;
import model.map.ground.GroundAtlas;
import model.map.parcel.ParcelManager;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import ressources.Image;
import tools.LogUtil;

@Root
public class Map {
	
    @ElementArray
    public Tile[][] tiles;

    @Element
    public GroundAtlas atlas;
    
    @Element
    public int width;
    @Element
    public int height;

    public String fileName = "hop.xml";

    public Map(int width, int height){
        this.width = width;
        this.height = height;
        atlas = new GroundAtlas(1024, 1024);
        tiles = new Tile[width][height];
    }

    public void add(Tile t){
        tiles[t.x][t.y] = t;
    }

    public ArrayList<Tile> getTiles() {
        ArrayList<Tile> res = new ArrayList<>();
        for(int x=0; x<width; x++)
                for(int y=0; y<height; y++)
                        res.add(tiles[x][y]);
        return res;
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
        if(getTile(x, y).isBlocked())
            return true;
        return false;
    }

    public Tile getTile(Point2D p) {
        if(p.x<0 || p.x>width ||
                p.y<0 || p.y>height)
            return null;
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
        if(getTile(x, y).isBlocked())
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
            if(getTile(x, y).isBlocked())
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
        if(getTile(p).isBlocked())
            return false;
        return true;
    }
    
    public ArrayList<Tile> get8Around(Tile t){
        ArrayList<Tile> res = new ArrayList<>();
        for(int i=-1; i<=1; i++)
            for(int j=-1; j<=1; j++){
                if(i==0 && j==0)
                    continue;
                if(t.x+i>=width || t.x+i < 0 ||
                        t.y+j>=height || t.y+j < 0)
                    continue;
                res.add(tiles[t.x+i][t.y+j]);
            }
        return res;
    }
    public ArrayList<Tile> get9Around(Tile t){
        ArrayList<Tile> res = new ArrayList<>();
        for(int i=-1; i<=1; i++)
            for(int j=-1; j<=1; j++){
                if(t.x+i>=width || t.x+i < 0 ||
                        t.y+j>=height || t.y+j < 0)
                    continue;
                res.add(tiles[t.x+i][t.y+j]);
            }
        return res;
        
    }
    public ArrayList<Tile> get16Around(Tile t){
        ArrayList<Tile> res = new ArrayList<>();
        for(int i=-2; i<=2; i++)
            for(int j=-2; j<=2; j++){
                if(i==0 && j==0)
                    continue;
                if(t.x+i>=width || t.x+i < 0 ||
                        t.y+j>=height || t.y+j < 0)
                    continue;
                res.add(tiles[t.x+i][t.y+j]);
            }
        res.removeAll(get8Around(t));
        return res;
    }
    public ArrayList<Tile> get4Around(Tile t){
        ArrayList<Tile> res = new ArrayList<>();
        if(t.n != null)
            res.add(t.n);
        if(t.s != null)
            res.add(t.s);
        if(t.e != null)
            res.add(t.e);
        if(t.w != null)
            res.add(t.w);
        return res;
    }
    
    public ArrayList<Tile> getTilesWithCliff(){
        ArrayList<Tile> res = new ArrayList<>();
        for(Tile t : getTiles())
            if(t.isCliff())
                res.add(t);
        return res;
    }
}
