/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor.tools;

import java.util.ArrayList;
import model.map.Tile;
import model.map.editor.MapToolManager;
import model.map.editor.TileSelector;

/**
 *
 * @author Benoît
 */
public class HeightTool extends MapTool {

    public HeightTool(MapToolManager manager, TileSelector selector) {
        super(manager, selector);
    }

    @Override
    public void primaryAction() {
        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group){
            double x = selector.getCenteringRatio(t);
            x = x*10;
            x-=5;
            double localFalloff = 1/(1+Math.exp(-x));
            t.elevation+=0.1*localFalloff;
        }
        manager.updateParcels(group);
    }

    @Override
    public void secondaryAction() {
        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group){
            double x = selector.getCenteringRatio(t);
            x = x*10;
            x-=5;
            double localFalloff = 1/(1+Math.exp(-x));
            t.elevation-=0.1*localFalloff;
        }
        manager.updateParcels(group);
    }
    
    
}
