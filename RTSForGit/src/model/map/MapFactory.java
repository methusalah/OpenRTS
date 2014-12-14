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
    public static Map getNew(int width, int height, BuilderLibrary lib){
        Map res = new Map(width, height);
        for(int y=0; y<height; y++)
            for(int x=0; x<width; x++)
                res.tiles.add(new Tile(x, y, res));

        // link tiles
	for(int x=0; x<width; x++)
            for(int y=0; y<height; y++){
                Tile t = res.getTile(x, y);
                if(x>0)
                        t.w = res.getTile(x-1, y);
                if(x<width-1)
                        t.e = res.getTile(x+1, y);
                if(y>0)
                        t.s = res.getTile(x, y-1);
                if(y<height-1)
                        t.n = res.getTile(x, y+1);
            }
        
        res.style = lib.getMapStyleBuilder("StdMapStyle").build();
        return res;
    }
    
    public static Map load(){
        final JFileChooser fc = new JFileChooser("assets/maps");
        int returnVal = fc.showOpenDialog(null);
        if (returnVal==JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    return load(f);
                } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                }
        }
        return null;
    }
    
    public static Map load(String fname) throws Exception {
        return load(new File(fname));
    }

    public static Map load(File file) throws Exception {
        Serializer serializer = new Persister();
        Map map = serializer.read(Map.class, file);
        map.fileName = file.getCanonicalPath();
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

	public static void save(Map map) {
		Serializer serializer = new Persister();
                try {
                    serializer.write(map, new File(map.fileName));
                } catch (Exception ex) {
                    Logger.getLogger(MapFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
	}    
}
