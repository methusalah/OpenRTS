/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor.tools;

import java.util.ArrayList;
import model.map.Tile;
import model.map.editor.MapToolManager;
import model.map.editor.Pencil;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class HeightTool extends MapTool {

    double amplitude = 0.2;
    
    public HeightTool(MapToolManager manager, Pencil selector) {
        super(manager, selector);
    }

    @Override
    public void primaryAction() {
        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group)
            t.elevation+=amplitude*selector.getApplicationRatio(t.getPos2D());
        manager.updateParcels(group);
    }

    @Override
    public void secondaryAction() {
        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group)
            t.elevation-=amplitude*selector.getApplicationRatio(t.getPos2D());
        manager.updateParcels(group);
    }

    @Override
    public void toggleSet() {
        LogUtil.logger.info("Height tool has no set.");
    }
    
    
    
    
}
