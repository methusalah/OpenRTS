/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.motion.pathfinding;

import geometry.Point2D;
import java.util.ArrayList;
import model.map.Map;
import model.map.Tile;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class FlowField {
    private Map map;
    public int[][] heatMap;
    Point2D[][] vectorMap;
    public int maxHeat = 0;
    int i = 0;
    ArrayList<Tile> toVisit = new ArrayList<>();
    public Point2D destination;
    
    public FlowField(Map map, Point2D destination){
        this.destination = destination;
        this.map = map;
        double start = System.currentTimeMillis();
        initHeatMap(map.width, map.height);
        Tile goalTile = map.getTile(destination);
        toVisit.add(goalTile);

        visitMap();

        vectorMap = new Point2D[map.width][map.height];
        generateVectors(map.width, map.height);
    }
    
    private void travelMapFrom(Tile t, int heat){
        if(t == null || t.isCliff() || heat >= getHeat(t))
            return;
        setHeat(t, heat);
        
        travelMapFrom(t.n, heat+1);
        travelMapFrom(t.s, heat+1);
        travelMapFrom(t.e, heat+1);
        travelMapFrom(t.w, heat+1);
    }
    
    private void visitMap(){
        int heat = 0;
        while(!toVisit.isEmpty()){
            ArrayList<Tile> toVisitThisTurn = new ArrayList<>();
            toVisitThisTurn.addAll(toVisit);
            toVisit.clear();
            for(Tile t : toVisitThisTurn){
                iterate();
                if(t != null && !t.isCliff() && getHeat(t) == Integer.MAX_VALUE){
                    setHeat(t, heat);
                    toVisit.add(t.n);
                    toVisit.add(t.s);
                    toVisit.add(t.e);
                    toVisit.add(t.w);
                }
            }
            heat++;
        }
        maxHeat = heat;
    }
    
    private void iterate(){
        i++;
    }
    
    private void initHeatMap(int width, int height) {
        heatMap = new int[width][height];
        for(int i=0; i<width; i++)
            for(int j=0; j<height; j++)
                heatMap[i][j] = Integer.MAX_VALUE;
    }
    
    private void setHeat(Tile t, int heat){
        heatMap[t.x][t.y] = heat;
        if(heat > maxHeat)
            maxHeat = heat;
    }

    
    public int getHeat(Tile t){
        return heatMap[t.x][t.y];
    }
    
    public int getHeat(int x, int y) {
        return heatMap[x][y];
    }
    
    public boolean hasNoHeat(int x, int y) {
        return getHeat(x, y) == Integer.MAX_VALUE;
    }
    
    private void generateVectors(int width, int height){
        for(int x=0; x<width; x++)
            for(int y=0; y<height; y++){
                if(hasNoHeat(x, y))
                    vectorMap[x][y] = Point2D.ORIGIN;
                else {
                    int north;
                    if(y == height-1 || hasNoHeat(x, y+1))
                        north = getHeat(x, y);
                    else
                        north = getHeat(x, y+1);

                    int south;
                    if(y == 0 || hasNoHeat(x, y-1))
                        south = getHeat(x, y);
                    else
                        south = getHeat(x, y-1);

                    int west;
                    if(x == 0 || hasNoHeat(x-1, y))
                        west = getHeat(x, y);
                    else
                        west = getHeat(x-1, y);

                    int east;
                    if(x == width-1 || hasNoHeat(x+1, y))
                        east = getHeat(x, y);
                    else
                        east = getHeat(x+1, y);

                    int vx;
                    int vy;
                    vx = west-east;
                    vy = south-north;
                    vectorMap[x][y] = new Point2D(vx, vy).getNormalized();
                }
            }
    }
    
    public Point2D getVector(Tile t){
        return vectorMap[t.x][t.y];
    }
    
    public Point2D getVector(Point2D p){
        Point2D res = Point2D.ORIGIN;
        
        if(p.getDistance(destination) < 1.5)
//            ||
//                !map.meetObstacle2(p, destination))
            return destination.getSubtraction(p).getNormalized();
        
        Tile t = map.getTile(p);
        return getVector(t);
//        if(p.getDistance(t.getPos2D()) != 0)
//            res = res.getAddition(getVector(t).getMult(1/p.getDistance(t.getPos2D())));
//        else
//            res = res.getAddition(getVector(t));
//        
//        if(t.n != null)
//            res = res.getAddition(getVector(t.n).getMult(1/p.getDistance(t.n.getPos2D())));
//        if(t.s != null)
//            res = res.getAddition(getVector(t.s).getMult(1/p.getDistance(t.s.getPos2D())));
//        if(t.e != null)
//            res = res.getAddition(getVector(t.e).getMult(1/p.getDistance(t.e.getPos2D())));
//        if(t.w != null)
//            res = res.getAddition(getVector(t.w).getMult(1/p.getDistance(t.w.getPos2D())));
//        return res.getNormalized();
//        
//        return vectorMap[(int)Math.floor(p.x)][(int)Math.floor(p.y)];
    }
}
