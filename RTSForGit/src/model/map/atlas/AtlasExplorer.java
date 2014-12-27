/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.atlas;

import geometry.Point2D;
import java.util.ArrayList;
import model.map.Map;

/**
 *
 * @author Benoît
 */
public class AtlasExplorer {
    
    Map map;
    
    public AtlasExplorer(Map map){
        this.map = map;
    }
    
    public Point2D getInMapSpace(Point2D p){
        return p.getMult(map.width, map.height).getDivision(map.atlas.width, map.atlas.height);
    }

    public Point2D getInAtlasSpace(Point2D p){
        return p.getMult(map.atlas.width, map.atlas.height).getDivision(map.width, map.height);
    }
    
    public double getInAtlasSpace(double distance){
        return distance*map.atlas.width/map.width;
    }

    public double getInMapSpace(double distance){
        return distance*map.width/map.atlas.width;
    }
    
    public ArrayList<Point2D> getPixelsInMapSpaceSquare(Point2D center, double radius){
        center = getInAtlasSpace(center);
        radius = getInAtlasSpace(radius);
        ArrayList<Point2D> res = new ArrayList<>();
        int minX = (int)Math.round(Math.max(center.x-radius, 0));
        int maxX = (int)Math.round(Math.min(center.x+radius, map.atlas.width-1));
        int minY = (int)Math.round(Math.max(center.y-radius, 0));
        int maxY = (int)Math.round(Math.min(center.y+radius, map.atlas.height-1));
        for(int x=minX; x<maxX; x++)
            for(int y=minY; y<maxY; y++){
                Point2D p = new Point2D(x, y);
                res.add(p);
            }
        return res;
    }

    public ArrayList<Point2D> getPixelsInMapSpaceCircle(Point2D center, double radius){
        ArrayList<Point2D> res = new ArrayList<>();
        for(Point2D p : getPixelsInMapSpaceSquare(center, radius))
                if(p.getDistance(getInAtlasSpace(center)) < getInAtlasSpace(radius))
                    res.add(p);
        return res;
    }

    public ArrayList<Point2D> getPixelsInMapSpaceDiamond(Point2D center, double radius){
        radius *= 1.414;
        ArrayList<Point2D> res = new ArrayList<>();
        for(Point2D p : getPixelsInMapSpaceSquare(center, radius))
            if(p.getManathanDistance(getInAtlasSpace(center)) < getInAtlasSpace(radius))
                res.add(p);
        return res;
    }
}
