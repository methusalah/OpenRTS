/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import model.map.cliff.Cliff;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import math.MyRandom;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ressources.Image;
import ressources.ImageReader;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class MapFactory {
    public static Map getNewMap(int width, int height){
        Map res = new Map(width, height);
        for(int x=0; x<width; x++)
            for(int y=0; y<height; y++)
                res.tiles[x][y] = new Tile(x, y, res);

        // link tiles
	for(int x=0; x<width; x++)
            for(int y=0; y<height; y++){
                Tile t = res.tiles[x][y];
                if(x>0)
                        t.w = res.tiles[x-1][y];
                if(x<width-1)
                        t.e = res.tiles[x+1][y];
                if(y>0)
                        t.s = res.tiles[x][y-1];
                if(y<height-1)
                        t.n = res.tiles[x][y+1];
            }
        return res;
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

	public static void write(Map map) throws Exception {
		Serializer serializer = new Persister();
		serializer.write(map, new File(map.fileName));
	}    
}
