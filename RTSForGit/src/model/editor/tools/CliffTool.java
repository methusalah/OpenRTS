/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor.tools;

import java.util.ArrayList;
import model.map.Tile;
import model.map.cliff.Cliff;
import model.map.data.CliffShapeBuilder;
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

    CliffShapeBuilder actualBuilder;
    
    int maintainedlevel;

    public CliffTool(ToolManager manager, Pencil selector) {
        super(manager, selector, RAISE_LOW_OP, FLATTEN_OP);
        actualBuilder = manager.encounter.map.style.cliffShapes.get(0);
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

    @Override
    public void toggleSet() {
        ArrayList<CliffShapeBuilder> builders = manager.encounter.map.style.cliffShapes;
        int index = builders.indexOf(actualBuilder)+1;
        if(index == builders.size())
            index = 0;
        actualBuilder = builders.get(index);
        LogUtil.logger.info("Cliff tool toggled to set "+actualBuilder.getID()+".");
    }
    
    public void buildShape(Cliff cliff){
        actualBuilder.build(cliff);
    }


    
}
