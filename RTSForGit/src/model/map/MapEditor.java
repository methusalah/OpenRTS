/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import geometry.Point2D;
import java.util.ArrayList;
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
        Tile t = map.getTile(p);
        if(t.level < 2){
            t.level++;
            t.z = t.level*Tile.STAGE_HEIGHT;
        }
        update(t);
    }
    
    public void levelDown(Point2D p){
        Tile t = map.getTile(p);
        if(t.level >0){
            t.level--;
            t.z -= t.level*Tile.STAGE_HEIGHT;
        }
        update(t);
    }
    
    public void incHeight(Point2D p){
        map.getTile(p).z+=0.1;
    }
    
    public void decHeight(Point2D p){
        map.getTile(p).z-=0.1;
    }
    
    private void update(Tile t){
        updatedTiles.addAll(map.get9Around(t));
        for(Tile n : updatedTiles){
            boolean diff = false;
            for(Tile nn : map.get8Around(n))
                if(n.level < nn.level){
                    diff = true;
                    break;
                }
            if(!n.isCliff() && diff)
                n.setCliff();
            if(n.isCliff() && !diff)
                n.cliff = null;
        }

        for(Tile n : updatedTiles){
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
