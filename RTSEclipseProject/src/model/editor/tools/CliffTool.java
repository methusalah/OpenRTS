/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor.tools;

import model.editor.Set;
import java.util.ArrayList;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.builders.CliffShapeBuilder;
import model.editor.ToolManager;
import model.editor.Pencil;
import ressources.definitions.BuilderLibrary;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class CliffTool extends Tool {
    private static final String RAISE_LOW_OP = "raise/low";
    private static final String FLATTEN_OP = "flatten";

    int maintainedlevel;

    public CliffTool(ToolManager manager) {
        super(manager, RAISE_LOW_OP, FLATTEN_OP);
        ArrayList<String> iconPaths = new ArrayList<>();
        for(CliffShapeBuilder b : manager.battlefield.map.style.cliffShapeBuilders)
            iconPaths.add(b.getIconPath());
        set = new Set(iconPaths, true);
    }
    
    @Override
    protected void createPencil() {
        pencil = new Pencil(manager.battlefield.map);
        pencil.snapPair = true;
        pencil.size = 4;
        pencil.sizeIncrement = 2;
        pencil.setUniqueMode();
        pencil.strengthIncrement = 0;
    }


    @Override
    public void primaryAction() {
        switch (actualOp){
            case RAISE_LOW_OP : raise(); break;
            case FLATTEN_OP : flatten(); break;
        }
    }

    @Override
    public void secondaryAction() {
        switch (actualOp){
            case RAISE_LOW_OP : low(); break;
            case FLATTEN_OP : break;
        }
    }
    
    private void raise(){
        if(!pencil.maintained){
            pencil.maintain();
            maintainedlevel = pencil.getCenterTile().level+1;
            if(maintainedlevel > 2)
                maintainedlevel = 2;
        }
        changeLevel();
    }
    
    private void low(){
        if(!pencil.maintained){
            pencil.maintain();
            maintainedlevel = pencil.getCenterTile().level-1;
            if(maintainedlevel < 0)
                maintainedlevel = 0;
        }
        changeLevel();
    }
    
    private void flatten(){
        if(!pencil.maintained){
            pencil.maintain();
            maintainedlevel = pencil.getCenterTile().level;
        }
        changeLevel();
    }
    
    private void changeLevel(){
        ArrayList<Tile> group = pencil.getTiles();
        for(Tile t : group)
            if(leadsToDoubleCliff(t, maintainedlevel)){
                LogUtil.logger.info("double cliff detected");
                return;
            }
        ArrayList<Tile> toUpdate = new ArrayList<>();
        for(Tile t : group){
            t.level = maintainedlevel;
            if(t.ramp != null)
                toUpdate.addAll(t.ramp.destroy());
        }
        group.addAll(toUpdate);
        manager.updateTiles(group);
    }
    

    private boolean leadsToDoubleCliff(Tile t, int level){
        for(Tile n : t.get8Neighbors())
            if(//n.isCliff() &&
                    (level > n.level+1 || level < n.level-1))
                return true;
        return false;
    }

    public void buildShape(Cliff cliff){
        manager.battlefield.map.style.cliffShapeBuilders.get(set.actual).build(cliff);
    }


    
}
