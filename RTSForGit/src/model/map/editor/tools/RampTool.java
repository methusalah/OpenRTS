/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor.tools;

import java.util.ArrayList;
import model.map.Tile;
import model.map.cliff.Cliff;
import model.map.editor.MapToolManager;
import model.map.editor.Pencil;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class RampTool extends MapTool{

    public RampTool(MapToolManager manager, Pencil selector) {
        super(manager, selector);
    }

    @Override
    public void primaryAction() {
        ArrayList<Tile> changed = new ArrayList<>();
        Tile cliffTile = pencil.getCenterTile();
        if(!cliffTile.isCliff || cliffTile.cliff.type != Cliff.Type.Orthogonal)
            return;
        
        int rampLength = 7;
        changed.add(cliffTile);
        double rampZ = 0;//-Tile.STAGE_HEIGHT/(rampLength+1);
        cliffTile.level++;
        cliffTile.rampZ = rampZ;
        LogUtil.logger.info("applying z = "+rampZ);
        
        for(int i=0; i<rampLength; i++){
            Tile n = manager.map.getTile(cliffTile.getPos2D().getTranslation(cliffTile.cliff.angle, i));
            n.level = cliffTile.level;
            n.elevation += rampZ;
            n.rampZ = rampZ;
            rampZ -= Tile.STAGE_HEIGHT/(rampLength+1);
            changed.add(n);
        }
        
        
//        pencil.getCenterTile().isRamp = true;
        manager.updateTiles(changed);
    }

    @Override
    public void secondaryAction() {
    }

    @Override
    public void toggleSet() {
        LogUtil.logger.info("Unavailable for ramp tool.");
    }

    @Override
    public void toggleOperation() {
        LogUtil.logger.info("Unavailable for ramp tool.");
    }
    
}
