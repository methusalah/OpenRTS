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
import model.map.parcel.ParcelManager;
import model.map.parcel.ParcelMesh;
import tools.LogUtil;
import view.mapDrawing.MapRenderer;

/**
 *
 * @author Benoît
 */
public class MapEditor {
    final Map map;
    final ParcelManager pm;
    public final TileSelector selector;
    
    ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public MapEditor(Map map, ParcelManager pm) {
        this.map = map;
        this.pm = pm;
        selector = new TileSelector(map);
    }
    
    public void levelUp(Point2D p){
        Tile tile = map.getTile(p);
        int level = tile.level+1;
        if(level > 2)
            level = 2;
        if(leadsToDoubleCliff(tile, level))
            return;
        
        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group)
            t.level = level;
        updateTiles(group);
    }
    
    public void levelDown(Point2D p){
        Tile tile = map.getTile(p);
        int level = tile.level-1;
        if(level < 0)
            level = 0;

        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group)
            if(leadsToDoubleCliff(t, level))
                return;
        for(Tile t : group)
            t.level = level;
        updateTiles(group);
    }
    
    private boolean leadsToDoubleCliff(Tile t, int level){
        for(Tile n : map.get8Around(t))
            if(n.isCliff() &&
                    (level > n.level+1 || level < n.level))
                return true;
        return false;
    }
    
    public void incHeight(Point2D p){
        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group)
            t.elevation+=0.1;
        updateParcels(group);
    }
    
    public void decHeight(Point2D p){
        ArrayList<Tile> group = selector.getTiles();
        for(Tile t : group)
            t.elevation-=0.1;
        updateParcels(group);
    }
    
    private void updateTiles(ArrayList<Tile> tiles){
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
                n.cliff.buildFace();
        }
        notifyListeners("tiles", updatedTiles);
        updateParcels(tiles);
    }
    
    private void updateParcels(ArrayList<Tile> tiles){
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
