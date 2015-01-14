/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield;

import model.battlefield.map.cliff.Cliff;
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
import model.battlefield.map.Map;
import model.battlefield.map.Ramp;
import model.battlefield.map.Tile;
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
public class BattlefieldFactory {
    private static final String BATTLEFIELD_FILE_EXTENSION = "bf";

    private BuilderLibrary lib;
    
    public BattlefieldFactory(BuilderLibrary lib) {
        this.lib = lib;
    }
    
    public Battlefield getNew(int width, int height){
        LogUtil.logger.info("Creating new battlefield...");
        Map m = new Map(width, height);
        for(int y=0; y<height; y++)
            for(int x=0; x<width; x++)
                m.tiles.add(new Tile(x, y, m));

        LogUtil.logger.info("   map builders");
        m.mapStyleID = "StdMapStyle";
        lib.getMapStyleBuilder(m.mapStyleID).build(m);

        LogUtil.logger.info("   map's tiles' links");
        linkTiles(m);
        
        Battlefield res = new Battlefield(m, lib);

        LogUtil.logger.info("Loading done.");
        return res;
    }
    
    public Battlefield load(){
        Battlefield res = null;
        final JFileChooser fc = new JFileChooser(Model.DEFAULT_MAP_PATH);
        fc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("RTS Battlefield file (*."+BATTLEFIELD_FILE_EXTENSION+")", BATTLEFIELD_FILE_EXTENSION);
        fc.addChoosableFileFilter(filter);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    LogUtil.logger.info("Loading battlefield "+f.getCanonicalPath()+"...");
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
        lib.getMapStyleBuilder(res.map.mapStyleID).build(res.map);

        LogUtil.logger.info("   tiles' links");
        linkTiles(res.map);
        
        LogUtil.logger.info("   ramps");
        for(Ramp r : res.map.ramps)
            r.connect(res.map);
        
        for(Tile t : res.map.tiles)
            if(t.isCliff)
                t.setCliff();
        
        LogUtil.logger.info("   cliffs' connexions");
        for(Tile t : res.map.tiles){
            t.correctElevation();
            if(t.isCliff)
                t.cliff.connect();
        }

        LogUtil.logger.info("   cliffs' shapes");
        for(Tile t : res.map.tiles){
            if(t.isCliff)
                lib.getCliffShapeBuilder(t.cliffShapeID).build(t.cliff);
        }

        res.buildParcels();
        res.engagement.battlefield = res;
        res.engagement.lib = lib;
        
        LogUtil.logger.info("   texture atlas");
        res.map.atlas.loadFromFile(res.fileName);
        LogUtil.logger.info("Loading done.");
        return res;
    }
    
    public Battlefield load(String fname) throws Exception {
        return load(new File(fname));
    }

    public static Battlefield load(File file) throws Exception {
        Serializer serializer = new Persister();
        Battlefield b = serializer.read(Battlefield.class, file);
        b.fileName = file.getCanonicalPath();
        return b;
    }

    public void save(Battlefield battlefield) {
            Serializer serializer = new Persister();
            try {
                if(battlefield.fileName != null) {
                    LogUtil.logger.info("Saving battlefield overwriting "+battlefield.fileName+"...");
                    serializer.write(battlefield, new File(battlefield.fileName));
                } else {
                    final JFileChooser fc = new JFileChooser(Model.DEFAULT_MAP_PATH);
                    fc.setAcceptAllFileFilterUsed(false);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("RTS Battlefield file (*."+BATTLEFIELD_FILE_EXTENSION+")", BATTLEFIELD_FILE_EXTENSION);
                    fc.addChoosableFileFilter(filter);
                    int returnVal = fc.showSaveDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File f = fc.getSelectedFile();
                        int i = f.getName().lastIndexOf('.');
                        if(i == 0 || !f.getName().substring(i+1).equals(BATTLEFIELD_FILE_EXTENSION))
                            f = new File(f.toString() + "."+BATTLEFIELD_FILE_EXTENSION);
                        
                        battlefield.fileName = f.getCanonicalPath();
                        LogUtil.logger.info("Saving map as "+battlefield.fileName+"...");
                        serializer.write(battlefield, new File(battlefield.fileName));
                    }					
                }
            } catch (Exception ex) {
                Logger.getLogger(BattlefieldFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            LogUtil.logger.info("Saving texture atlas...");
            battlefield.map.atlas.saveToFile(battlefield.fileName);
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
