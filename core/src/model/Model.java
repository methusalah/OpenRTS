package model;

import geometry.tools.LogUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import model.battlefield.Battlefield;
import model.battlefield.BattlefieldFactory;
import model.builders.definitions.BuilderLibrary;
import model.builders.definitions.DefParser;
import model.editor.ToolManager;

public class Model {
    public static final String BATTLEFIELD_UPDATED_EVENT = "mapupdatedevent";
    static final String CONFIG_PATH = "assets/data";
    public static final String DEFAULT_MAP_PATH = "assets/maps/";
    static final double UPDATE_DELAY = 1000;
	private static final int DEFAULT_WIDTH = 64;
	private static final int DEFAULT_HEIGHT = 32;
    
    
    public final BattlefieldFactory factory;
    public Battlefield battlefield;
    
    public Commander commander;
    public Reporter reporter;
    public ToolManager toolManager;
    

    public final BuilderLibrary lib;
    final DefParser parser;
    File confFile;
    double nextUpdate = 0;
    
    private ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public Model() {
        lib = new BuilderLibrary();
        parser = new DefParser(lib, CONFIG_PATH);
        
        factory = new BattlefieldFactory(lib);
        setNewBattlefield();
    }
    
    public void updateConfigs() {
        if(System.currentTimeMillis()>nextUpdate){
            nextUpdate = System.currentTimeMillis()+UPDATE_DELAY;
            parser.readFile();
        }
    }
    
    public void loadBattlefield(){
        Battlefield loadedBattlefield = factory.load();
        if(loadedBattlefield != null)
            setBattlefield(loadedBattlefield);
    }
    
    public void saveBattlefield(){
        factory.save(battlefield);
    }
    
    public void setNewBattlefield(){
        setBattlefield(factory.getNew(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }
    
    private void setBattlefield(Battlefield battlefield){
        this.battlefield = battlefield;
        commander = new Commander(battlefield.armyManager, battlefield.map);
        toolManager = new ToolManager(battlefield, lib);
        LogUtil.logger.info("Reseting view...");
        notifyListeners(BATTLEFIELD_UPDATED_EVENT);
        LogUtil.logger.info("Done.");
    }
    
    public void reload(){
    	saveBattlefield();
        Battlefield loadedBattlefield = factory.load(battlefield.fileName);
        if(loadedBattlefield != null)
            setBattlefield(loadedBattlefield);
    }
    
    public void addListener(ActionListener listener){
        listeners.add(listener);
    }
    
    public void notifyListeners(String eventCommand){
        for(ActionListener l : listeners)
            l.actionPerformed(new ActionEvent(this, 0, eventCommand));
        
    }
}
