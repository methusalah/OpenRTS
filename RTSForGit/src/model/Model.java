package model;

import geometry.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import model.map.Map;
import model.army.ArmyManager;
import model.lighting.SunLight;
import ressources.definitions.BuilderLibrary;
import model.map.editor.MapToolManager;
import model.map.MapFactory;
import model.map.parcel.ParcelManager;
import ressources.definitions.DefParser;
import tools.LogUtil;

public class Model {
    public static final String MAP_UPDATED_EVENT = "mapupdatedevent";
    static final String CONFIG_PATH = "assets/data";
    public static final String DEFAULT_MAP_PATH = "assets/maps/";
    static final double UPDATE_DELAY = 1000;
    
    public MapFactory factory;
    public Map map;
    public SunLight sunLight;
    public ArmyManager armyManager;
    
    public Commander commander;
    public Reporter reporter;
    public MapToolManager toolManager;
    public ParcelManager parcelManager;
    

    public BuilderLibrary lib;
    DefParser parser;
    File confFile;
    double nextUpdate = 0;
    
    private ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public Model() {
        lib = new BuilderLibrary();
        parser = new DefParser(lib);

        ArrayList<File> files = getFiles(CONFIG_PATH);
        while(!files.isEmpty()){
            ArrayList<File> toAdd = new ArrayList<>();
            for(File f : files)
                if(f.isFile())
                    parser.addFile(f);
                else if(f.isDirectory())
                    toAdd.addAll(getFiles(f.getAbsolutePath()));
            files.clear();
            files.addAll(toAdd);
        }
        parser.readFile();
        
        factory = new MapFactory(lib);
        this.map = factory.getNew(128, 128);
        sunLight = new SunLight();
        parcelManager = new ParcelManager(map);
        lib.map = map;

        armyManager = new ArmyManager();
        lib.am = armyManager;
        
        commander = new Commander(armyManager, map);
        toolManager = new MapToolManager(map, parcelManager, lib);
//        armyManager.createTestArmy(lib);
    }
    
    private ArrayList<File> getFiles(String folderPath){
        ArrayList<File> res = new ArrayList<>();
        File folder = new File(folderPath);
        for(File f : folder.listFiles())
            res.add(f);
        return res;
    }
    
    public void updateConfigs() {
        if(System.currentTimeMillis()>nextUpdate){
            nextUpdate = System.currentTimeMillis()+UPDATE_DELAY;
            parser.readFile();
        }
    }
    
    public void load(){
        Map newMap = factory.load();
        if(newMap != null){
            LogUtil.logger.info("Reseting model...");
            map = newMap;
            lib.map = map;
            commander = new Commander(armyManager, map);

            LogUtil.logger.info("Reseting parcels...");
            parcelManager = new ParcelManager(map);
            toolManager = new MapToolManager(map, parcelManager, lib);
            LogUtil.logger.info("Reseting view...");
            notifyListeners(MAP_UPDATED_EVENT);
            LogUtil.logger.info("Done.");
        }
    }
    
    public void save(){
        factory.save(map);
    }
    
    public void newMap(){
        Map newMap = factory.getNew(128, 128);
        if(newMap != null){
            LogUtil.logger.info("Reseting model...");
            map = newMap;
            lib.map = map;
            commander = new Commander(armyManager, map);

            LogUtil.logger.info("Reseting parcels...");
            parcelManager = new ParcelManager(map);
            toolManager = new MapToolManager(map, parcelManager, lib);
            LogUtil.logger.info("Reseting view...");
            notifyListeners(MAP_UPDATED_EVENT);
            LogUtil.logger.info("Done.");
        }
    }
    
    public void addListener(ActionListener listener){
        listeners.add(listener);
    }
    
    public void notifyListeners(String eventCommand){
        for(ActionListener l : listeners)
            l.actionPerformed(new ActionEvent(this, 0, eventCommand));
        
    }
}
