/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor;

import geometry.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import model.map.Map;
import model.map.Tile;
import model.map.cliff.Cliff;
import model.editor.tools.AtlasTool;
import model.editor.tools.CliffTool;
import model.editor.tools.HeightTool;
import model.editor.tools.Tool;
import model.editor.tools.RampTool;
import model.editor.tools.UnitTool;
import model.battlefield.Battlefield;
import model.map.parcel.ParcelManager;
import ressources.definitions.BuilderLibrary;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ToolManager {
    public final Battlefield battlefield;
    
    public String pointedSpatialLabel;
    
    public HeightTool heightTool;
    public CliffTool cliffTool;
    public AtlasTool atlasTool;
    public RampTool rampTool;
    public UnitTool unitTool;
    
    public Tool actualTool;
    
    double delay = 0;
    long lastAction = 0;
    
    ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public ToolManager(Battlefield encounter, BuilderLibrary lib) {
        this.battlefield = encounter;
        heightTool = new HeightTool(this);
        cliffTool = new CliffTool(this);
        atlasTool = new AtlasTool(this);
        rampTool = new RampTool(this);
        unitTool = new UnitTool(this, lib.getAllUnitBuilders());
        
        actualTool = cliffTool;
    }
    
    public void setCliffTool(){
        actualTool = cliffTool;
        LogUtil.logger.info("Cliff tool set.");
        notifyListeners("tool");
    }
    public void setHeightTool(){
        actualTool = heightTool;
        LogUtil.logger.info("Height tool set.");
        notifyListeners("tool");
    }
    public void setAtlasTool(){
        actualTool = atlasTool;
        LogUtil.logger.info("Atlas tool set.");
        notifyListeners("tool");
    }
    public void setRampTool(){
        actualTool = rampTool;
        LogUtil.logger.info("Ramp tool set.");
        notifyListeners("tool");
    }
    public void setUnitTool(){
        actualTool = unitTool;
        LogUtil.logger.info("Unit tool set.");
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
            if(/*!t.isCliff() &&*/ diff)
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
                cliffTool.buildShape(t.cliff);
        }
        notifyListeners("tiles", updatedTiles);
        updateParcels(tiles);
    }
    
    public void updateParcels(ArrayList<Tile> tiles){
        battlefield.parcelManager.updateParcelsFor(tiles);
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
