/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import geometry.Point2D;
import java.util.ArrayList;
import static model.map.Tile.STAGE_HEIGHT;
import model.map.cliff.Cliff;
import model.map.parcel.ParcelManager;
import model.map.parcel.ParcelMesh;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class MapEditor {
    ArrayList<Tile> updatedTiles = new ArrayList<>();
    ArrayList<ParcelMesh> updatedParcels = new ArrayList<>();
    Map map;
    ParcelManager pm;

    public MapEditor(Map map, ParcelManager pm) {
        this.map = map;
        this.pm = pm;
    }
    
    public void levelUp(Point2D p){
        Tile tile = map.getTile(p);
        int level = tile.level+1;
        if(level > 2)
            level = 2;
        if(leadsToDoubleCliff(tile, level))
            return;
        
        ArrayList<Tile> group = new ArrayList<>();
        group.add(tile);
        for(Tile t : group)
            t.level = level;
        update(group);
    }
    
    public void levelDown(Point2D p){
        Tile tile = map.getTile(p);
        int level = tile.level-1;
        if(level < 0)
            level = 0;
        if(leadsToDoubleCliff(tile, level))
            return;
        
        ArrayList<Tile> group = new ArrayList<>();
        group.add(tile);
        for(Tile t : group)
            t.level = level;
        update(group);
    }
    
    private boolean leadsToDoubleCliff(Tile t, int level){
        for(Tile n : map.get8Around(t))
            if(n.isCliff() &&
                    (level > n.level+1 || level < n.level))
                return true;
        return false;
    }
    
    public void incHeight(Point2D p){
        Tile tile = map.getTile(p);
        tile.elevation+=0.1;
        updatedParcels.addAll(pm.getUpdatedParcelsFor(tile));
    }
    
    public void decHeight(Point2D p){
        Tile tile = map.getTile(p);
        tile.elevation-=0.1;
        updatedParcels.addAll(pm.getUpdatedParcelsFor(tile));
    }
    
    private void update(ArrayList<Tile> tiles){
        updatedTiles.clear();
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
        updatedParcels.addAll(pm.getUpdatedParcelsFor(updatedTiles));
    }
    
    public ArrayList<Tile> grabUpdatedTiles(){
        ArrayList<Tile> res = new ArrayList<>();
        res.addAll(updatedTiles);
        updatedTiles.clear();
        return res;
    }
    public ArrayList<ParcelMesh> grabUpdatedParcels(){
        ArrayList<ParcelMesh> res = new ArrayList<>();
        res.addAll(updatedParcels);
        updatedParcels.clear();
        return res;
    }
}
