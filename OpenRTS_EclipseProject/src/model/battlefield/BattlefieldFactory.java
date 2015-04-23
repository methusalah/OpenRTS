package model.battlefield;

import geometry.tools.LogUtil;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Model;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;
import model.builders.MapStyleBuilder;
import model.builders.definitions.BuilderLibrary;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * this class serializes and deserializes a battlefield into and from files
 * everything is translated in and from XML format,
 * except for the texture atlas of the ground (buffer array)
 * 
 */
public class BattlefieldFactory {
    private static final String BATTLEFIELD_FILE_EXTENSION = "bf";

    private BuilderLibrary lib;
    
    public BattlefieldFactory(BuilderLibrary lib) {
        this.lib = lib;
    }
    
    public Battlefield getNew(int width, int height){
        LogUtil.logger.info("Creating new battlefield...");
        MapStyleBuilder styleBuilder = lib.getMapStyleBuilder("StdMapStyle");
        Map m = new Map(styleBuilder.width, styleBuilder.height);
        styleBuilder.build(m);

        for(int y=0; y<m.height; y++)
            for(int x=0; x<m.width; x++)
                m.tiles.add(new Tile(x, y, m));
        LogUtil.logger.info("   map builders");
        

        LogUtil.logger.info("   map's tiles' links");
        linkTiles(m);
        
        Battlefield res = new Battlefield(m, lib);
        lib.battlefield = res;

        LogUtil.logger.info("Loading done.");
        return res;
    }
    
    
    
    public Battlefield load(){
        final JFileChooser fc = new JFileChooser(Model.DEFAULT_MAP_PATH);
        fc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("RTS Battlefield file (*."+BATTLEFIELD_FILE_EXTENSION+")", BATTLEFIELD_FILE_EXTENSION);
        fc.addChoosableFileFilter(filter);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                return load(f);
        }
        return null;
    }
    
    public Battlefield load(String fname) {
        return load(new File(fname));
    }

    public Battlefield load(File file) {
        Battlefield res = null;

        try {
            LogUtil.logger.info("Loading battlefield "+file.getCanonicalPath()+"...");
            Serializer serializer = new Persister();
            res = serializer.read(Battlefield.class, file);
            res.fileName = file.getCanonicalPath();
        } catch (Exception e1) {
                e1.printStackTrace();
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
        
        for(Tile t : res.map.tiles){
        	int minLevel = t.level;
        	int maxLevel = t.level;
            for(Tile n : res.map.get8Around(t))
            	maxLevel = Math.max(maxLevel, n.level);
            if(minLevel != maxLevel)
            	t.setCliff(minLevel, maxLevel);
        }
        
        LogUtil.logger.info("   cliffs' connexions");
        for(Tile t : res.map.tiles){
            for(Cliff c : t.getCliffs())
            	c.connect();
        }

        int i = 0;
        LogUtil.logger.info("   cliffs' shapes");
        for(Tile t : res.map.tiles){
            for(Cliff c : t.getCliffs()){
                lib.getCliffShapeBuilder(t.cliffShapeID).build(c);
                i++;
            }
        }
        LogUtil.logger.info("   cliffs' shapes "+i);

        lib.battlefield = res;
        res.buildParcels();
        res.engagement.battlefield = res;
        res.engagement.lib = lib;
        res.map.resetTrinkets(lib);
        res.engagement.resetEngagement();
        
        LogUtil.logger.info("   texture atlas");
        res.map.atlas.loadFromFile(res.fileName);
        LogUtil.logger.info("Loading done.");
        return res; 
    }

    public void save(Battlefield battlefield) {
    	battlefield.engagement.saveEngagement();
    	battlefield.map.saveTrinkets();
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
