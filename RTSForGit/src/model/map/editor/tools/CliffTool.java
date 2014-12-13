/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor.tools;

import java.util.ArrayList;
import model.map.Tile;
import model.map.cliff.Cliff;
import model.map.data.CliffShapeBuilder;
import model.map.editor.MapToolManager;
import model.map.editor.Pencil;
import ressources.definitions.BuilderLibrary;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class CliffTool extends MapTool {
    CliffShapeBuilder builder1;
    CliffShapeBuilder builder2;
    CliffShapeBuilder actualBuilder;

    public CliffTool(MapToolManager manager, Pencil selector, BuilderLibrary lib) {
        super(manager, selector);
        builder1 = lib.getCliffShapeBuilder("StdRockyVegyDug");
        builder2 = lib.getCliffShapeBuilder("StdManmade");
        actualBuilder = builder2;
    }

    @Override
    public void primaryAction() {
        int level = selector.getCenterTile().level+1;
        if(level > 2)
            level = 2;
        
        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group)
            if(leadsToDoubleCliff(t, level))
                return;
        for(Tile t : group)
            t.level = level;
        manager.updateTiles(group);
    }

    @Override
    public void secondaryAction() {
        int level = selector.getCenterTile().level-1;
        if(level < 0)
            level = 0;

        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group)
            if(leadsToDoubleCliff(t, level))
                return;
        for(Tile t : group)
            t.level = level;
        manager.updateTiles(group);
    }
    
    private boolean leadsToDoubleCliff(Tile t, int level){
        for(Tile n : t.get8Neighbors())
            if(n.isCliff() &&
                    (level > n.level+1 || level < n.level))
                return true;
        return false;
    }

    @Override
    public void toggleSet() {
        if(actualBuilder == builder1){
            actualBuilder = builder2;
        } else {
            actualBuilder = builder1;
        }
        LogUtil.logger.info("Cliff tool toggled to set "+actualBuilder.getID()+".");
    }
    
    public void setCliff(Cliff cliff){
        actualBuilder.build(cliff);
    }


    
}
