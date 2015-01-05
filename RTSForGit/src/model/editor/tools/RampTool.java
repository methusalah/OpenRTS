/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor.tools;

import java.util.ArrayList;
import model.map.Ramp;
import model.map.Tile;
import model.map.cliff.Cliff;
import model.editor.ToolManager;
import model.editor.Pencil;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class RampTool extends Tool{
    private static final String ADD_DELETE_OP = "add/delete";

    public RampTool(ToolManager manager) {
        super(manager, ADD_DELETE_OP);
    }

    @Override
    protected void createPencil() {
        pencil = new Pencil(manager.battlefield.map);
        pencil.sizeIncrement = 0;
        pencil.setUniqueMode();
        pencil.strengthIncrement = 0;
    }
    
    @Override
    public void primaryAction() {
        Tile t = pencil.getCenterTile();
        if(t.ramp != null){
            t.ramp.grow(t);
        } else {
            if(!t.isCliff)
                return;
            new Ramp(t, manager.battlefield.map);
        }
        
        ArrayList<Tile> changed = new ArrayList<>();
        changed.addAll(t.ramp.tiles);
        for(Tile t1 : t.ramp.tiles)
            for(Tile n : t1.get8Neighbors()){
                if(!changed.contains(n))
                    changed.add(n);
                if(n.isCliff)
                    n.unsetCliff();
            }
        manager.updateTiles(changed);
    }

    @Override
    public void secondaryAction() {
    }
    
    @Override
    public boolean isAnalog() {
        return false;
    }
}
