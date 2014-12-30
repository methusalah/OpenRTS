/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor.tools;

import java.util.ArrayList;
import model.map.Tile;
import model.map.atlas.Atlas;
import model.map.atlas.AtlasExplorer;
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
    enum Operation {RaiseLow, Flatten}
    
    Operation actualOp = Operation.RaiseLow;
    CliffShapeBuilder actualBuilder;
    
    int maintainedlevel;

    public CliffTool(MapToolManager manager, Pencil selector, BuilderLibrary lib) {
        super(manager, selector);
        actualBuilder = manager.map.style.cliffShapes.get(0);
    }

    @Override
    public void primaryAction() {
        switch (actualOp){
            case RaiseLow : raise(); break;
            case Flatten : flatten(); break;
        }
    }

    @Override
    public void secondaryAction() {
        switch (actualOp){
            case RaiseLow : low(); break;
            case Flatten : break;
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
        ArrayList<CliffShapeBuilder> builders = manager.map.style.cliffShapes;
        int index = builders.indexOf(actualBuilder)+1;
        if(index == builders.size())
            index = 0;
        actualBuilder = builders.get(index);
        LogUtil.logger.info("Cliff tool toggled to set "+actualBuilder.getID()+".");
    }
    
    @Override
    public void toggleOperation() {
        switch (actualOp){
            case RaiseLow :
                actualOp = Operation.Flatten;
                LogUtil.logger.info("Atlas tool operation toggled to flatten.");
                break;
            case Flatten :
                actualOp = Operation.RaiseLow;
                LogUtil.logger.info("Atlas tool operation toggled to raise/low.");
                break;
        }
    }

    
    public void buildShape(Cliff cliff){
        actualBuilder.build(cliff);
    }


    
}
