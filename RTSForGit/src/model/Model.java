package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import model.map.Map;
import model.battlefield.Battlefield;
import ressources.definitions.BuilderLibrary;
import model.editor.ToolManager;
import model.map.MapFactory;
import ressources.definitions.DefParser;
import tools.LogUtil;

public class Model {
    public static final String MAP_UPDATED_EVENT = "mapupdatedevent";
    static final String CONFIG_PATH = "assets/data";
    public static final String DEFAULT_MAP_PATH = "assets/maps/";
    static final double UPDATE_DELAY = 1000;
    
    public MapFactory factory;
    public Battlefield battlefield;
    
    public Commander commander;
    public Reporter reporter;
    public ToolManager toolManager;
    

    public BuilderLibrary lib;
    DefParser parser;
    File confFile;
    double nextUpdate = 0;
    
    private ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public Model() {
        lib = new BuilderLibrary();
        parser = new DefParser(lib, CONFIG_PATH);
        
        factory = new MapFactory(lib);
        setNewBattlefield();

//        armyManager.createTestArmy(lib);
    }
    
    public void updateConfigs() {
        if(System.currentTimeMillis()>nextUpdate){
            nextUpdate = System.currentTimeMillis()+UPDATE_DELAY;
            parser.readFile();
        }
    }
    
    public void loadBattlefield(){
        Map loadedMap = factory.load();
        if(loadedMap != null)
            setEncounter(loadedMap);
    }
    
    public void saveBattlefield(){
        factory.save(battlefield.map);
    }
    
    public void setNewBattlefield(){
        setEncounter(factory.getNew(128, 128));
    }
    
    private void setEncounter(Map map){
        battlefield = new Battlefield(map, Battlefield.Instanciation.New);
        
        lib.map = battlefield.map;
        lib.armyManager = battlefield.armyManager;
        
        commander = new Commander(battlefield.armyManager, battlefield.map);
        toolManager = new ToolManager(battlefield, lib);
        
        LogUtil.logger.info("Reseting view...");
        notifyListeners(MAP_UPDATED_EVENT);
        LogUtil.logger.info("Done.");
    }
    
    public void addListener(ActionListener listener){
        listeners.add(listener);
    }
    
    public void notifyListeners(String eventCommand){
        for(ActionListener l : listeners)
            l.actionPerformed(new ActionEvent(this, 0, eventCommand));
        
    }
}
