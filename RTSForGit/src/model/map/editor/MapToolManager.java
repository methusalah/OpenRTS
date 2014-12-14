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
import model.map.editor.tools.AtlasTool;
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
    public final Map map;
    final ParcelManager pm;
    public final Pencil pencil;
    
    public HeightTool heightTool;
    public CliffTool cliffTool;
    public AtlasTool atlasTool;
    public MapTool actualTool;
    
    double delay = 0;
    long lastAction = 0;
    
    ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public MapToolManager(Map map, ParcelManager pm, BuilderLibrary lib) {
        this.map = map;
        this.pm = pm;
        pencil = new Pencil(map);
        heightTool = new HeightTool(this, pencil);
        cliffTool = new CliffTool(this, pencil, lib);
        atlasTool = new AtlasTool(this, pencil, map.atlas);
        actualTool = cliffTool;
        pencil.snapPair = true;
    }
    
    public void setCliffTool(){
        actualTool = cliffTool;
        pencil.snapPair = true;
        pencil.tileDependant = true;
        LogUtil.logger.info("Cliff tool set.");
        notifyListeners("tool");
    }
    public void setHeightTool(){
        actualTool = heightTool;
        pencil.snapPair = false;
        pencil.tileDependant = true;
        LogUtil.logger.info("Height tool set.");
        notifyListeners("tool");
    }
    public void setAtlasTool(){
        actualTool = atlasTool;
        pencil.snapPair = false;
        pencil.tileDependant = false;
        LogUtil.logger.info("Atlas tool set.");
        notifyListeners("tool");
    }
    public void toggleSet(){
        actualTool.toggleSet();
    }
    
    public void primaryAction(){
        if(lastAction+delay<System.currentTimeMillis()){
//            LogUtil.logger.info((System.currentTimeMillis()-lastAction)+" ms since last call");
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

        for(Tile t : updatedTiles){
            t.correctElevation();
            if(t.isCliff())
                t.cliff.connect();
        }
        for(Tile t : updatedTiles){
            if(t.isCliff())
                cliffTool.setCliff(t.cliff);
        }
        notifyListeners("tiles", updatedTiles);
        updateParcels(tiles);
    }
    
    public void updateParcels(ArrayList<Tile> tiles){
        pm.updateParcelsFor(tiles);
        notifyListeners("parcels", tiles);
    }

    public void updateGroundAtlas(){
        notifyListeners("ground", new ArrayList<Tile>());
    }
    
    private void notifyListeners(String command, ArrayList<Tile> tiles){
        ActionEvent event = new ActionEvent(tiles, 0, command);
        for(ActionListener l : listeners)
            l.actionPerformed(event);
    }

    private void notifyListeners(String command){
        ActionEvent event = new ActionEvent(this, 0, command);
        for(ActionListener l : listeners)
            l.actionPerformed(event);
    }

    public void addListener(ActionListener l) {
        listeners.add(l);
    }
    
}
