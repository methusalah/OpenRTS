/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import model.map.cliff.Cliff;
import java.awt.Color;
import java.util.ArrayList;
import math.MyRandom;
import ressources.Image;
import ressources.ImageReader;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class MapFactory {
    private static final Color h1Color = new Color(0, 150, 0, 255);
    private static final Color h2Color = new Color(0, 200, 0, 255);
    private static final Color h3Color = new Color(0, 250, 0, 255);
    private static final Color RampColor = new Color(0, 150, 250, 255);
    private static final Color RampStartColor = new Color(0, 0, 250, 255);

    TileDef[][] tileDef;
    Map map;

    public MapFactory(String mapPath) {
        Image mapFile = ImageReader.read(mapPath);
        map = new Map(mapFile.width, mapFile.height);
        tileDef = new TileDef[map.width][map.height];

        defineTiles(mapFile);
        
        // inctanciate map's tiles
	for(int x=0; x<map.width; x++)
            for(int y=0; y<map.height; y++){
                TileDef def = tileDef[x][y];
                if(def.cliff){
                    map.add(new Cliff(def));
                } else {
                    map.add(new Tile(def));
                }
            }

        // link tiles
	for(int x=0; x<map.width; x++)
            for(int y=0; y<map.height; y++){
                Tile t = map.tiles[x][y];
                if(x>0)
                        t.w = map.tiles[x-1][y];
                if(x<map.width-1)
                        t.e = map.tiles[x+1][y];
                if(y>0)
                        t.s = map.tiles[x][y-1];
                if(y<map.height-1)
                        t.n = map.tiles[x][y+1];
                if(t.isCliff())
                    ((Cliff)t).correctGroundZ();
            }
        
        
        
        
        // add ground height noise
        for(Tile t : map.getTiles()) {
            if(!t.isBlocked() &&
                t.w!=null && !t.w.isBlocked() &&
                t.s!=null && !t.s.isBlocked() &&
                t.w.s!= null && !t.w.s.isBlocked())
            t.z += MyRandom.between(-0.1, 0.1);
        }

        // compute cliffs' shape
        // warning, compute ramps before because it creates cliffs
        for(Cliff c : map.cliffs)
            c.drawShape();
    }
    
    public Map getMap(){
        return map;
    }
    
    private void defineTiles(Image mapFile){
        ArrayList<TileDef> rampStarts = new ArrayList<>();

        // Assign roles to tiles
        // first we read the map to find level variations
        // with ground level and ramps
        for(int x=0; x<map.width; x++)
            for(int y=0; y<map.height; y++){
                TileDef def = new TileDef();
                def.x = x;
                def.y = y;
                
                Color c = mapFile.get(x, mapFile.height-y-1);
                if(c.equals(h1Color))
                        def.setLevel(0);
                if(c.equals(h2Color))
                        def.setLevel(1);
                if(c.equals(h3Color))
                        def.setLevel(2);
                if(c.equals(RampStartColor)){
                    def.rampStart = true;
                    rampStarts.add(def);
                }
                if(c.equals(RampColor))
                    def.rampComp = true;
                tileDef[x][y] = def;
            }
        // second we compare levels to find cliffs
        for(int x=0; x<map.width; x++)
            for(int y=0; y<map.height; y++){
                if(tileDef[x][y].rampComp)
                    continue;
                for(int i=-1; i<=1; i++)
                    for(int j=-1; j<=1; j++){
                        if(i==0 && j==0)
                            continue;
                        if(x+i>=map.width || x+i < 0 ||
                                y+j>=map.height || y+j < 0)
                            continue;
                        if(tileDef[x][y].level < tileDef[x+i][y+j].level)
                            tileDef[x][y].cliff = true;
                
                    }
            }
        // third and last, we read ramps to find the last cliffs
        for(TileDef def : rampStarts){
            Ramp ramp = new Ramp();
            feedRamp(def, ramp);
            ramp.finalise();
        }
    }
    
    private void feedRamp(TileDef def, Ramp ramp) {
        if(def.rampStart)
            ramp.start = def;
        if(!def.rampComp && !def.rampStart && def.level > ramp.maxLevel)
            ramp.maxLevel = def.level;
        if(!def.rampComp && !def.rampStart && def.level < ramp.minLevel)
            ramp.minLevel = def.level;
        if((def.rampComp || def.rampStart) && !ramp.defs.contains(def)){
            ramp.defs.add(def);
            if(def.y<map.height-1)
                feedRamp(tileDef[def.x][def.y+1], ramp);
            if(def.y>0)
                feedRamp(tileDef[def.x][def.y-1], ramp);
            if(def.x<map.width-1)
                feedRamp(tileDef[def.x+1][def.y], ramp);
            if(def.x>0)
                feedRamp(tileDef[def.x-1][def.y], ramp);
        }
    }

    
}
