/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.parcel;

import java.util.ArrayList;
import model.map.Map;
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
//                LogUtil.logger.info("tile "+i+","+j+" : index = "+index);
                meshes.get(index).add(map.getTile(i, j));
            }
        
        for(ParcelMesh mesh : meshes)
            mesh.compute();
    }
    
}
