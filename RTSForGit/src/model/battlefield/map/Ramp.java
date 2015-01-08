/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map;

import java.util.ArrayList;
import java.util.List;
import math.Angle;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */

@Root
public class Ramp {
    
    public Map map;
    public ArrayList<Tile> tiles = new ArrayList<>();
    int minX, maxX, minY, maxY;

    @Element
    int level;
    @Element
    double angle;
    @ElementList
    List<Integer> tilesRef = new ArrayList<>();
    
    public Ramp(Tile t, Map map){
        this.map = map;
        map.ramps.add(this);
        if(!t.isCliff)
            throw new IllegalArgumentException("Ramp must be first created on a cliff.");
        angle = t.cliff.angle;
        level = t.getNeighborsMaxLevel();
        tiles.add(t);
        compute();
    }
    
    public Ramp(@Element(name="level") int level,
            @Element(name="angle") double angle,
            @ElementList(name="tilesRef") List<Integer> tilesRef){
        this.level = level;
        this.angle = angle;
        this.tilesRef = tilesRef;
    }
    
    /**
     * for deserialization purpose
     * @param map 
     */
    public void connect(Map map){
        this.map = map;
        for(Integer ref : tilesRef){
            tiles.add(map.getTile(ref));
        }
        compute();
    }
    
    private void compute(){
        minX = Integer.MAX_VALUE;
        maxX = 0;
        minY = Integer.MAX_VALUE;
        maxY = 0;
        for(Tile t : tiles){
            minX = t.x<minX? t.x: minX;
            maxX = t.x>maxX? t.x: maxX;
            minY = t.y<minY? t.y: minY;
            maxY = t.y>maxY? t.y: maxY;
        }
        tilesRef.clear();
        for(Tile t : tiles){
            tilesRef.add(map.getRef(t));
            t.ramp = this;
            t.level = level;
            t.elevation = -Tile.STAGE_HEIGHT*getSlopeRate(t);
            for(Tile n : t.get8Neighbors())
                n.ramp = this;
        }
        
    }
    
    public void add(ArrayList<Tile> tiles){
        this.tiles.addAll(tiles);
        compute();
    }
    
    public void grow(Tile t){
        if(angle == 0){
            if(t.x>maxX)
                growEast();
            if(t.y>maxY)
                growNorth();
            if(t.y<minY)
                growSouth();
        } else if(angle == Angle.FLAT){
            if(t.x<minX)
                growWest();
            if(t.y>maxY)
                growNorth();
            if(t.y<minY)
                growSouth();
        } else if(angle == Angle.RIGHT){
            if(t.x<minX)
                growWest();
            if(t.x>maxX)
                growEast();
            if(t.y>maxY)
                growNorth();
        } else if(angle == -Angle.RIGHT){
            if(t.x<minX)
                growWest();
            if(t.x>maxX)
                growEast();
            if(t.y<minY)
                growSouth();
        }
    }
    private void growNorth(){
        ArrayList<Tile> grown = new ArrayList<>();
        for(Tile t : tiles)
            if(t.n != null
                    && !tiles.contains(t.n))
                grown.add(t.n);
        add(grown);
    }
    private void growSouth(){
        ArrayList<Tile> grown = new ArrayList<>();
        for(Tile t : tiles)
            if(t.s != null
                    && !tiles.contains(t.s))
                grown.add(t.s);
        add(grown);
    }
    private void growEast(){
        ArrayList<Tile> grown = new ArrayList<>();
        for(Tile t : tiles)
            if(t.e != null
                    && !tiles.contains(t.e))
                grown.add(t.e);
        add(grown);
    }
    private void growWest(){
        ArrayList<Tile> grown = new ArrayList<>();
        for(Tile t : tiles)
            if(t.w != null
                    && !tiles.contains(t.w))
                grown.add(t.w);
        add(grown);
        
    }
    
    /**
     * Get the slope rate at the given tile coords.
     * At the top of the ramp, slope is 0, and 1 at the bottom.
     * @param t
     * @return 
     */
    public double getSlopeRate(Tile t){
        if(t.ramp != this)
            return 0;
        if(angle == 0){
            if(t.x>maxX)
                return 1;
            else if(t.x<minX)
                return 0;
            else
                return (double)(t.x-minX)/(maxX-minX+1);
        } else if(angle == Angle.FLAT){
            if(t.x>maxX)
                return 0;
            else if(t.x<minX)
                return 1;
            else
                return (double)(maxX-t.x+1)/(maxX-minX+1);
            
        } else if(angle == Angle.RIGHT){
            if(t.y>maxY)
                return 1;
            else if(t.y<minY)
                return 0;
            else
                return (double)(t.y-minY)/(maxY-minY+1);
        } else if(angle == -Angle.RIGHT){
            if(t.y>maxY)
                return 0;
            else if(t.y<minY)
                return 1;
            else
                return (double)(maxY-t.y+1)/(maxY-minY+1);
        }
        throw new RuntimeException();
    }
    
    public double getCliffSlopeRate(Tile t){
        if(angle == 0)
            return getSlopeRate(t);
        else if(angle == Angle.FLAT)
            return getSlopeRate(t.e);
        else if(angle == Angle.RIGHT)
            return getSlopeRate(t);
        else if(angle == -Angle.RIGHT)
            return getSlopeRate(t.n);
        throw new RuntimeException();
    }
    
    public ArrayList<Tile> destroy(){
        ArrayList<Tile> res = new ArrayList<>();
        res.addAll(tiles);
        for(Tile t : tiles){
            t.ramp = null;
            t.level--;
            t.elevation = 0;
            for(Tile n : t.get8Neighbors())
                if(!res.contains(n)){
                    res.add(n);
                    
                    n.ramp = null;
                    n.elevation = 0;
                }
        }
        map.ramps.remove(this);
        return res;
        
    }
    

}
