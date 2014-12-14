/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor.tools;

import collections.Map2D;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import geometry.Point2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import model.map.Tile;
import model.map.editor.MapToolManager;
import model.map.editor.Pencil;
import model.map.atlas.DoubleMap;
import model.map.atlas.GroundAtlas;
import tools.LogUtil;
import view.mapDrawing.MapRenderer;

/**
 *
 * @author Benoît
 */
public class AtlasTool extends MapTool {

    GroundAtlas atlas;
    int actualLayer = 1;
    double increment = 20;

    
    public AtlasTool(MapToolManager manager, Pencil selector, GroundAtlas atlas) {
        super(manager, selector);
        this.atlas = atlas;
    }

    @Override
    public void primaryAction() {
        ArrayList<Point2D> pixels;
        switch(selector.shape){
            case Circle : pixels = getPixelsInCircle(); break;
            case Diamond : pixels = getPixelsInDiamond(); break;
            case Square : pixels = getPixelsInSquare(); break;
                default:throw new RuntimeException();
        }
        increment(pixels);
        manager.updateGroundAtlas();
    }

    @Override
    public void secondaryAction() {
        ArrayList<Point2D> pixels;
        switch(selector.shape){
            case Circle : pixels = getPixelsInCircle(); break;
            case Diamond : pixels = getPixelsInDiamond(); break;
            case Square : pixels = getPixelsInSquare(); break;
                default:throw new RuntimeException();
        }
        decrement(pixels);
        manager.updateGroundAtlas();
    }

    @Override
    public void toggleSet() {
        actualLayer++;
        if(actualLayer > 7)
            actualLayer = 0;
        LogUtil.logger.info("Atlas tool toggled to texture "+actualLayer+".");
    }
    
    private ArrayList<Point2D> getPixelsInCircle(){
        ArrayList<Point2D> res = new ArrayList<>();
        Point2D relativeCenter = selector.getPos().getMult(atlas.width, atlas.height).getDivision(manager.map.width, manager.map.height);
        double relativeRadius = selector.radius*atlas.width/manager.map.width;
        int minX = (int)Math.round(Math.max(relativeCenter.x-relativeRadius, 0));
        int maxX = (int)Math.round(Math.min(relativeCenter.x+relativeRadius, atlas.width-1));
        int minY = (int)Math.round(Math.max(relativeCenter.y-relativeRadius, 0));
        int maxY = (int)Math.round(Math.min(relativeCenter.y+relativeRadius, atlas.height-1));
        for(int x=minX; x<maxX; x++)
            for(int y=minY; y<maxY; y++){
                Point2D p = new Point2D(x, y);
                if(p.getDistance(relativeCenter) < relativeRadius)
                    res.add(p);
            }
        return res;
    }

    private ArrayList<Point2D> getPixelsInSquare(){
        ArrayList<Point2D> res = new ArrayList<>();
        Point2D relativeCenter = selector.getPos().getMult(atlas.width, atlas.height).getDivision(manager.map.width, manager.map.height);
        double relativeRadius = selector.radius*atlas.width/manager.map.width;
        int minX = (int)Math.round(Math.max(relativeCenter.x-relativeRadius, 0));
        int maxX = (int)Math.round(Math.min(relativeCenter.x+relativeRadius, atlas.width-1));
        int minY = (int)Math.round(Math.max(relativeCenter.y-relativeRadius, 0));
        int maxY = (int)Math.round(Math.min(relativeCenter.y+relativeRadius, atlas.height-1));
        for(int x=minX; x<maxX; x++)
            for(int y=minY; y<maxY; y++){
                Point2D p = new Point2D(x, y);
                res.add(p);
            }
        return res;
    }

    private ArrayList<Point2D> getPixelsInDiamond(){
        ArrayList<Point2D> res = new ArrayList<>();
        Point2D relativeCenter = selector.getPos().getMult(atlas.width, atlas.height).getDivision(manager.map.width, manager.map.height);
        double relativeRadius = selector.radius*1.414*atlas.width/manager.map.width;
        int minX = (int)Math.round(Math.max(relativeCenter.x-relativeRadius, 0));
        int maxX = (int)Math.round(Math.min(relativeCenter.x+relativeRadius, atlas.width-1));
        int minY = (int)Math.round(Math.max(relativeCenter.y-relativeRadius, 0));
        int maxY = (int)Math.round(Math.min(relativeCenter.y+relativeRadius, atlas.height-1));
        for(int x=minX; x<maxX; x++)
            for(int y=minY; y<maxY; y++){
                Point2D p = new Point2D(x, y);
                if(p.getManathanDistance(relativeCenter) < relativeRadius)
                    res.add(p);
            }
        return res;
    }
    
    private void increment(ArrayList<Point2D> pixels){
        for(Point2D p : pixels)
            increment((int)Math.round(p.x), (int)Math.round(p.y));
    }
    
    private void increment(int x, int y){
        double contextualIncrement = increment*selector.getApplicationRatio(new Point2D(x, y).getMult(manager.map.width, manager.map.height).getDivision(atlas.width, atlas.height));
        
        double valueToDitribute=contextualIncrement;
        ArrayList<DoubleMap> availableLayers = new ArrayList<>();
        for(DoubleMap l : atlas.layers)
            if(atlas.layers.indexOf(l) == actualLayer)
                valueToDitribute -= add(l, x, y, contextualIncrement);
            else
                availableLayers.add(l);
        
        int secur = -1;
        while(valueToDitribute > 0 && !availableLayers.isEmpty() && secur++ <50){
            ArrayList<DoubleMap> unavailableLayers = new ArrayList<>();
            double shared = valueToDitribute/availableLayers.size();
            valueToDitribute = 0;
            for(DoubleMap m : availableLayers){
                valueToDitribute += subtract(m, x, y, shared);
                if(m.get(x, y) == 0){
                    unavailableLayers.add(m);
                }
            }
            availableLayers.removeAll(unavailableLayers);
        }
        if(secur>40)
            LogUtil.logger.warning("Impossible to ditribute value");
        updateAtlasPixel(x, y);
    }
    
    private void decrement(ArrayList<Point2D> pixels){
        for(Point2D p : pixels)
            decrement((int)Math.round(p.x), (int)Math.round(p.y));
    }
    
    private void decrement(int x, int y){
        double contextualIncrement = increment*selector.getApplicationRatio(new Point2D(x, y).getMult(manager.map.width, manager.map.height).getDivision(atlas.width, atlas.height));
        
        double valueToDitribute=contextualIncrement;
        ArrayList<DoubleMap> availableLayers = new ArrayList<>();
        for(DoubleMap l : atlas.layers)
            if(atlas.layers.indexOf(l) == actualLayer)
                valueToDitribute -= subtract(l, x, y, contextualIncrement);
            else if(l.get(x, y) > 0)
                availableLayers.add(l);
        if(availableLayers.isEmpty())
            availableLayers.add(atlas.layers.get(0));
        
        int secur = -1;
        while(valueToDitribute > 0 && !availableLayers.isEmpty() && secur++ <50){
            ArrayList<DoubleMap> unavailableLayers = new ArrayList<>();
            double shared = valueToDitribute/availableLayers.size();
            valueToDitribute = 0;
            for(DoubleMap m : availableLayers){
                valueToDitribute += add(m, x, y, shared);
                if(m.get(x, y) == 255){
                    unavailableLayers.add(m);
                }
            }
            availableLayers.removeAll(unavailableLayers);
        }
        if(secur>40)
            LogUtil.logger.warning("Impossible to ditribute value");
        updateAtlasPixel(x, y);
    }
    
    
    private void updateAtlasPixel(int x, int y){
        for(int i=0; i<2; i++){
            ByteBuffer buffer = atlas.getBuffer(i);
                int r = (int)Math.round(atlas.layers.get(i).get(x, y)) << 24;
                int g = (int)Math.round(atlas.layers.get(i+1).get(x, y)) << 16;
                int b = (int)Math.round(atlas.layers.get(i+2).get(x, y)) << 8;
                int a = (int)Math.round(atlas.layers.get(i+3).get(x, y));
                buffer.asIntBuffer().put(y*atlas.width+x, r+g+b+a);
        }
        atlas.toUpdate = true;
    }
    
    private double add(DoubleMap map, int x, int y, double val){
        double rest = 0;
        double newVal = map.get(x, y)+val;
        if(newVal>255){
            rest = newVal-255;
            newVal = 255;
        }
        map.set(x, y, newVal);
        return rest;
    }
    private double subtract(DoubleMap map, int x, int y, double val){
        double rest = 0;
        double newVal = map.get(x, y)-val;
        if(newVal<0){
            rest = -newVal;
            newVal = 0;
        }
        map.set(x, y, newVal);
        return rest;
    }

    
    
}
