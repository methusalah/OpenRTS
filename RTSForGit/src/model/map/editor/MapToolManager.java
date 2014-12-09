/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor;

import geometry.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import model.map.Map;
import model.map.Tile;
import static model.map.Tile.STAGE_HEIGHT;
import model.map.cliff.Cliff;
import model.map.data.CliffShapeBuilder;
import model.map.editor.tools.CliffTool;
import model.map.editor.tools.HeightTool;
import model.map.editor.tools.MapTool;
import model.map.parcel.ParcelManager;
import model.map.parcel.ParcelMesh;
import ressources.definitions.BuilderLibrary;
import tools.LogUtil;
import view.mapDrawing.MapRenderer;

/**
 *
 * @author Benoît
 */
public class MapToolManager {
    final Map map;
    final ParcelManager pm;
    public final TileSelector selector;
    
    public HeightTool heightTool;
    public CliffTool cliffTool;
    MapTool actualTool;
    
    double delay = 50;
    double lastAction = 0;
    
    ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public MapToolManager(Map map, ParcelManager pm, BuilderLibrary lib) {
        this.map = map;
        this.pm = pm;
        selector = new TileSelector(map);
        heightTool = new HeightTool(this, selector);
        cliffTool = new CliffTool(this, selector, lib);
        actualTool = cliffTool;
        selector.snapPair = true;
    }
    
    public void setCliffTool(){
        actualTool = cliffTool;
        selector.snapPair = true;
        LogUtil.logger.info("Cliff tool set.");
    }
    public void setHeightTool(){
        actualTool = heightTool;
        selector.snapPair = false;
        LogUtil.logger.info("Height tool set.");
    }
    
    public void primaryAction(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            actualTool.primaryAction();
        }
    }
    public void secondaryAction(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            actualTool.secondaryAction();
        }
    }
    
    public void updateTiles(ArrayList<Tile> tiles){
        ArrayList<Tile> updatedTiles = new ArrayList<>();
        updatedTiles.addAll(tiles);
        for(Tile t : tiles)
            for(Tile n : map.get9Around(t))
                if(!updatedTiles.contains(n))
                    updatedTiles.add(n);
        
        for(Tile t : updatedTiles){
            boolean diff = false;
            for(Tile nn : map.get8Around(t))
                if(t.level < nn.level){
                    diff = true;
                    break;
                }
            if(!t.isCliff() && diff)
                t.setCliff();
            if(t.isCliff()){
                if(!diff)
                    t.unsetCliff();
                else if(t.cliff.type == Cliff.Type.Bugged)
                    t.cliff.type = null;
            }
        }

        for(Tile n : updatedTiles){
            n.correctElevation();
            if(n.isCliff())
                n.cliff.connect();
        }
        for(Tile n : updatedTiles){
            if(n.isCliff())
                cliffTool.setCliff(n.cliff);
        }
        notifyListeners("tiles", updatedTiles);
        updateParcels(tiles);
    }
    
    public void updateParcels(ArrayList<Tile> tiles){
        pm.updateParcelsFor(tiles);
        notifyListeners("parcels", tiles);
        
    }
    
    private void notifyListeners(String command, ArrayList<Tile> tiles){
        ActionEvent event = new ActionEvent(tiles, 0, command);
        for(ActionListener l : listeners)
            l.actionPerformed(event);
    }

    public void addListener(ActionListener l) {
        listeners.add(l);
    }
    
}
