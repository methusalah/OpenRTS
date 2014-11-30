/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.parcel;

import java.util.ArrayList;
import model.map.Map;
import model.map.Tile;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ParcelManager {
    
    private static final int RESOLUTION = 10;
    
    Map map;
    public ArrayList<ParcelMesh> meshes = new ArrayList<>();
    
    public ParcelManager(Map map){
        this.map = map;
        createParcelMeshes();
        
    }
    
    private void createParcelMeshes(){
        int nbParcel = (int)(Math.ceil((double)map.width/RESOLUTION)*Math.ceil((double)map.height/RESOLUTION));
        for(int i=0; i<nbParcel; i++)
            meshes.add(new ParcelMesh(this));
        
        for(int i=0; i<map.width; i++)
            for(int j=0; j<map.height; j++){
                int index = (int)(Math.floor(j/RESOLUTION)*Math.ceil((double)map.width/RESOLUTION)+Math.floor(i/RESOLUTION));
                meshes.get(index).add(map.getTile(i, j));
            }
        
        for(ParcelMesh mesh : meshes)
            mesh.compute();
    }
    
    public ArrayList<ParcelMesh> getUpdatedParcelsFor(Tile t){
        ArrayList<Tile> tiles = new ArrayList<>();
        tiles.add(t);
        return getUpdatedParcelsFor(tiles);
    }

    public ArrayList<ParcelMesh> getUpdatedParcelsFor(ArrayList<Tile> tiles){
        ArrayList<ParcelMesh> res = new ArrayList<>();
        for(Tile t : tiles){
            int index = (int)(Math.floor(t.y/RESOLUTION)*Math.ceil((double)map.width/RESOLUTION)+Math.floor(t.x/RESOLUTION));
            if(!res.contains(meshes.get(index)))
                res.add(meshes.get(index));
        }
        
        for(ParcelMesh mesh : res)
            mesh.rebuild();
            
        return res;
    }
    
}
