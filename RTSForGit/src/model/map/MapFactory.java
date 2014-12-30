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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import math.MyRandom;
import model.Model;
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
    private static final String MAP_FILE_EXTENSION = "map";

    private BuilderLibrary lib;
    
    public MapFactory(BuilderLibrary lib) {
        this.lib = lib;
    }
    
    public Map getNew(int width, int height){
        LogUtil.logger.info("Creating new map...");
        Map res = new Map(width, height);
        for(int y=0; y<height; y++)
            for(int x=0; x<width; x++)
                res.tiles.add(new Tile(x, y, res));

        LogUtil.logger.info("   builders");
        res.mapStyleID = "StdMapStyle";
        lib.getMapStyleBuilder(res.mapStyleID).build(res);

        LogUtil.logger.info("   tiles' links");
        linkTiles(res);

        LogUtil.logger.info("Loading done.");
        return res;
    }
    
    public Map load(){
        Map res = null;
        final JFileChooser fc = new JFileChooser(Model.DEFAULT_MAP_PATH);
        fc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("RTS Map file (*."+MAP_FILE_EXTENSION+")", MAP_FILE_EXTENSION);
        fc.addChoosableFileFilter(filter);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    LogUtil.logger.info("Loading map "+f.getCanonicalPath()+"...");
                    res = load(f);
                } catch (Exception e1) {
                        e1.printStackTrace();
                }
        }
        if(res == null){
            LogUtil.logger.info("Load failed");
            return null;
        }
        
        LogUtil.logger.info("   builders");
        lib.getMapStyleBuilder(res.mapStyleID).build(res);

        LogUtil.logger.info("   tiles' links");
        linkTiles(res);
        
        LogUtil.logger.info("   ramps");
        for(Ramp r : res.ramps)
            r.connect(res);
        
        for(Tile t : res.tiles)
            if(t.isCliff)
                t.setCliff();
        
        LogUtil.logger.info("   cliffs' connexions");
        for(Tile t : res.tiles){
            t.correctElevation();
            if(t.isCliff)
                t.cliff.connect();
        }

        LogUtil.logger.info("   cliffs' shapes");
        for(Tile t : res.tiles){
            if(t.isCliff)
                lib.getCliffShapeBuilder(t.cliffShapeID).build(t.cliff);
        }
        
        LogUtil.logger.info("   texture atlas");
        res.atlas.loadFromFile(res.fileName);
        LogUtil.logger.info("Loading done.");
        return res;
    }
    
    public Map load(String fname) throws Exception {
        return load(new File(fname));
    }

    public static Map load(File file) throws Exception {
        Serializer serializer = new Persister();
        Map map = serializer.read(Map.class, file);
        map.fileName = file.getCanonicalPath();
        return map;
    }

    public void save(Map map) {
            Serializer serializer = new Persister();
            try {
                if(map.fileName!=null) {
                    LogUtil.logger.info("Saving map overwriting "+map.fileName+"...");
                    serializer.write(map, new File(map.fileName));
                } else {
                    final JFileChooser fc = new JFileChooser(Model.DEFAULT_MAP_PATH);
                    fc.setAcceptAllFileFilterUsed(false);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("RTS Map file (*."+MAP_FILE_EXTENSION+")", MAP_FILE_EXTENSION);
                    fc.addChoosableFileFilter(filter);
                    int returnVal = fc.showSaveDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        int i = f.getName().lastIndexOf('.');
                        if(i == 0 || !f.getName().substring(i+1).equals(MAP_FILE_EXTENSION))
                            f = new File(f.toString() + "."+MAP_FILE_EXTENSION);
                        
                        map.fileName = f.getCanonicalPath();
                        LogUtil.logger.info("Saving map as "+map.fileName+"...");
                        serializer.write(map, new File(map.fileName));
                    }					
                }
            } catch (Exception ex) {
                Logger.getLogger(MapFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            LogUtil.logger.info("Saving texture atlas...");
            map.atlas.saveToFile(map.fileName);
            LogUtil.logger.info("Done.");
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
