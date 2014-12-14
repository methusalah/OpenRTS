/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import model.map.cliff.Cliff;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import math.MyRandom;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ressources.Image;
import ressources.ImageReader;
import ressources.definitions.BuilderLibrary;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class MapFactory {

    private BuilderLibrary lib;
    
    public MapFactory(BuilderLibrary lib) {
        this.lib = lib;
    }
    
    public Map getNew(int width, int height){
        Map res = new Map(width, height);
        for(int y=0; y<height; y++)
            for(int x=0; x<width; x++)
                res.tiles.add(new Tile(x, y, res));

        linkTiles(res);
        res.mapStyleID = "StdMapStyle";
        lib.getMapStyleBuilder(res.mapStyleID).build(res);
        return res;
    }
    
    public Map load(){
        Map res = null;
        final JFileChooser fc = new JFileChooser("assets/maps");
        int returnVal = fc.showOpenDialog(null);
        if (returnVal==JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    res = load(f);
                } catch (Exception e1) {
                        e1.printStackTrace();
                }
        }
        if(res == null){
            LogUtil.logger.info("Load failed");
            return null;
        }
        
        lib.getMapStyleBuilder(res.mapStyleID).build(res);
        linkTiles(res);
        
        for(Tile t : res.tiles)
            if(t.isCliff)
                t.setCliff();
        
        for(Tile t : res.tiles){
            t.correctElevation();
            if(t.isCliff)
                t.cliff.connect();
        }
        for(Tile t : res.tiles){
            if(t.isCliff)
                lib.getCliffShapeBuilder(t.cliffShapeID).build(t.cliff);
        }
        
        res.atlas.loadFromFile();
        
        
        
        return res;
    }
    
    public Map load(String fname) throws Exception {
        return load(new File(fname));
    }

    public static Map load(File file) throws Exception {
        Serializer serializer = new Persister();
        Map map = serializer.read(Map.class, file);
        map.fileName = file.getName();
        return map;
    }

//	public static void main(String[] args) throws Exception {
//		Serializer serializer = new Persister();
//		Model example = new Model();
//		File result = new File("c:\\temp\\xmlsimple\\example.xml");
//		serializer.write(example, result);
//		
//		serializer = new Persister();
//		File source = new File("c:\\temp\\xmlsimple\\example.xml");
//		Model exr = serializer.read(Model.class, source);
//		System.out.println(exr);
//	}

    public void save(Map map) {
            Serializer serializer = new Persister();
            try {
                serializer.write(map, new File("assets/maps/"+map.fileName));
            } catch (Exception ex) {
                Logger.getLogger(MapFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            map.atlas.saveToFile();
    }
    
    private void linkTiles(Map map){
        for(int x=0; x<map.width; x++)
            for(int y=0; y<map.height; y++){
                Tile t = map.getTile(x, y);
                t.map = map;
                if(x>0)
                        t.w = map.getTile(x-1, y);
                if(x<map.width-1)
                        t.e = map.getTile(x+1, y);
                if(y>0)
                        t.s = map.getTile(x, y-1);
                if(y<map.height-1)
                        t.n = map.getTile(x, y+1);
            }

    }
}
