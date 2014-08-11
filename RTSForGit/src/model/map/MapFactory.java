/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import java.awt.Color;
import java.util.ArrayList;
import math.MyRandom;
import ressources.Image;
import ressources.ImageReader;

/**
 *
 * @author Benoît
 */
public class MapFactory {
    private static final Color h1Color = new Color(0, 150, 0, 255);
    private static final Color h2Color = new Color(0, 200, 0, 255);
    private static final Color h3Color = new Color(0, 250, 0, 255);
    private static final Color h1cliffColor = new Color(250, 150, 0, 255);
    private static final Color h2cliffColor = new Color(250, 200, 0, 255);
    private static final Color h3cliffColor = new Color(250, 250, 0, 255);
    private static final Color RampColor = new Color(0, 150, 250, 255);
    private static final Color RampStartColor = new Color(0, 0, 250, 255);

    public static Map buildMap(String mapPath) {
        Image i = ImageReader.read(mapPath);
        Map m = new Map(i.width, i.height);
        
        // Assign roles to tiles
        for(int x=0; x<i.width; x++)
            for(int y=0; y<i.height; y++){
                Color c = i.get(x, i.height-y-1);
                int level = 0;
                boolean isCliff = false;
                if(c.equals(h1Color))
                        level = 0;
                if(c.equals(h2Color))
                        level = 1;
                if(c.equals(h3Color))
                        level = 2;
                if(c.equals(h1cliffColor)){
                        isCliff = true;
                        level = 0;
                }
                if(c.equals(h2cliffColor)){
                        isCliff = true;
                        level = 1;
                }
                m.tiles[x][y] = new Tile(x, y, level, isCliff);
                if(c.equals(RampStartColor)){
                    m.tiles[x][y].rampStart = true;
                    m.tiles[x][y].rampComp = true;
                }
                if(c.equals(RampColor)){
                    m.tiles[x][y].rampComp = true;
                }
            }

        // link tiles
	for(int x=0; x<i.width; x++)
            for(int y=0; y<i.height; y++){
                Tile t = m.tiles[x][y];
                if(x>0)
                        t.w = m.tiles[x-1][y];
                if(x<i.width-1)
                        t.e = m.tiles[x+1][y];
                if(y>0)
                        t.s = m.tiles[x][y-1];
                if(y<i.height-1)
                        t.n = m.tiles[x][y+1];
            }
        
        
        // compute ramps
        for(Tile t : m.getTiles())
            if(t.rampStart) {
                ArrayList<Tile> rampTiles = new ArrayList<Tile>();
                findRampComp(t, rampTiles);
                m.ramps.add(new Ramp(rampTiles));
            }

        // add ground height noise
        for(Tile t : m.getTiles()) {
            if(!t.isCliff() &&
                t.w!=null && !t.w.isCliff() &&
                t.s!=null && !t.s.isCliff() &&
                t.w.s!= null && !t.w.s.isCliff())
            t.z += ((MyRandom.next()-0.5)/4);
        }

        // compute cliffs direction
        // warning, compute ramps before because it creates cliffs
        for(Tile t : m.getTiles()) {
            if(t.isCliff())
                t.cliff.ComputeAngle();
        }
        

        return m;
    }
    
    private static void findRampComp(Tile t, ArrayList<Tile> list) {
            if((t.rampComp || t.rampStart) && !list.contains(t)){
                list.add(t);
                findRampComp(t.n, list);
                findRampComp(t.s, list);
                findRampComp(t.e, list);
                findRampComp(t.w, list);
            }
        }

    
}
