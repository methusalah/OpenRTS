/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor;

import geometry.Point2D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.editor.tools.AtlasTool;
import model.editor.tools.CliffTool;
import model.editor.tools.HeightTool;
import model.editor.tools.Tool;
import model.editor.tools.RampTool;
import model.editor.tools.UnitTool;
import model.battlefield.Battlefield;
import model.editor.tools.TrinketTool;
import model.battlefield.map.Trinket;
import model.battlefield.map.parcel.ParcelManager;
import model.battlefield.map.parcel.ParcelMesh;
import model.builders.UnitBuilder;
import ressources.definitions.BuilderLibrary;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ToolManager {
    public final Battlefield battlefield;
    public final BuilderLibrary lib;
    
    public String pointedSpatialLabel;
    
    public HeightTool heightTool;
    public CliffTool cliffTool;
    public AtlasTool atlasTool;
    public RampTool rampTool;
    public UnitTool unitTool;
    public TrinketTool trinketTool;
    
    public Tool actualTool;
    
    double delay = 0;
    long lastAction = 0;
    
    List<ActionListener> listeners = new ArrayList<>();
    
    public ToolManager(Battlefield battlefield, BuilderLibrary lib) {
        this.battlefield = battlefield;
        this.lib = lib;
        heightTool = new HeightTool(this);
        cliffTool = new CliffTool(this);
        atlasTool = new AtlasTool(this);
        rampTool = new RampTool(this);
        unitTool = new UnitTool(this);
        trinketTool = new TrinketTool(this);
        
        actualTool = cliffTool;
    }
    
    public void setCliffTool(){
        actualTool = cliffTool;
        notifyListeners("tool");
    }
    public void setHeightTool(){
        actualTool = heightTool;
        notifyListeners("tool");
    }
    public void setAtlasTool(){
        actualTool = atlasTool;
        notifyListeners("tool");
    }
    public void setRampTool(){
        actualTool = rampTool;
        notifyListeners("tool");
    }
    public void setUnitTool(){
        actualTool = unitTool;
        notifyListeners("tool");
    }
    public void setTrinketTool(){
        actualTool = trinketTool;
        notifyListeners("tool");
    }

    public void toggleSet(){
        if(actualTool.hasSet())
            actualTool.getSet().toggle();
    }
    public void toggleOperation(){
        actualTool.toggleOperation();
    }
    
    public void analogPrimaryAction(){
        if(actualTool.isAnalog())
            if(lastAction+delay<System.currentTimeMillis()){
//                LogUtil.logger.info((System.currentTimeMillis()-lastAction)+" ms since last call");
                lastAction = System.currentTimeMillis();
                actualTool.primaryAction();
            }
    }
    
    public void primaryAction(){
        if(!actualTool.isAnalog())
            actualTool.primaryAction();
    }

    public void analogSecondaryAction(){
        if(actualTool.isAnalog())
            if(lastAction+delay<System.currentTimeMillis()){
                lastAction = System.currentTimeMillis();
                actualTool.secondaryAction();
            }
    }
    
    public void secondaryAction(){
        if(!actualTool.isAnalog())
            actualTool.secondaryAction();
    }

    
    public void updateTiles(ArrayList<Tile> tiles){
        ArrayList<Tile> updatedTiles = new ArrayList<>();
        updatedTiles.addAll(tiles);
        for(Tile t : tiles)
            for(Tile n : battlefield.map.get9Around(t))
                if(!updatedTiles.contains(n))
                    updatedTiles.add(n);
        
        for(Tile t : updatedTiles){
            boolean diff = false;
            for(Tile nn : battlefield.map.get8Around(t))
                if(t.level < nn.level){
                    diff = true;
                    break;
                }
            if(t.isCliff)
            	t.unsetCliff();
            
            if(diff)
            	t.setCliff();
        }

        for(Tile t : updatedTiles){
            t.correctElevation();
            if(t.isCliff())
                t.cliff.connect();
        }
        for(Tile t : updatedTiles){
            if(t.isCliff())
                cliffTool.buildShape(t.cliff);
        }
        notifyListeners("tiles", updatedTiles);
        updateParcels(tiles);
    }
    
    public void updateParcels(ArrayList<Tile> tiles){
        List<ParcelMesh> toUpdate = battlefield.parcelManager.updateParcelsFor(tiles);
        notifyListeners("parcels", toUpdate);
    }

    public void updateGroundAtlas(){
        notifyListeners("ground", new ArrayList<Tile>());
    }
    
    private void notifyListeners(String command, Object o){
        ActionEvent event = new ActionEvent(o, 0, command);
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
    public void removeListener(ActionListener l) {
        listeners.remove(l);
    }
    
    public void updatePencilsPos(Point2D pos){
        actualTool.pencil.setPos(pos);
    }
    public void releasePencils(){
        actualTool.pencil.release();
    }
    
}
