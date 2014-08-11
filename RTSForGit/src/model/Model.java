package model;

import java.io.File;
import model.map.Map;
import model.army.ArmyManager;
import model.army.data.BuilderLibrary;
import model.army.data.definitions.DefParser;
import tools.LogUtil;

public class Model {
    static final String CONFIG_PATH = "assets/data/units.xml";
    static final double UPDATE_DELAY = 1000;
    
    public Map map;
    public ArmyManager armyManager;
    
    public Commander commander;
    public Reporter reporter;
    

    public BuilderLibrary lib;;
    DefParser parser;
    File confFile;
    long lastModified = 0;
    double nextUpdate = 0;
    
    public Model(Map map) {
        this.map = map;
        armyManager = new ArmyManager();
        commander = new Commander(armyManager, map);
        
        lib = new BuilderLibrary(map, armyManager);
        parser = new DefParser(lib, new File(CONFIG_PATH));
        parser.addFile(new File("assets/data/weapons.xml"));
        parser.addFile(new File("assets/data/turrets.xml"));
        parser.addFile(new File("assets/data/movers.xml"));
        parser.addFile(new File("assets/data/effects.xml"));
        parser.addFile(new File("assets/data/projectiles.xml"));
        parser.readFile();
        
        armyManager.createTestArmy(lib);
        
        
    }
    
    public void updateConfigs() {
        if(System.currentTimeMillis()>nextUpdate){
            nextUpdate = System.currentTimeMillis()+UPDATE_DELAY;
            parser.readFile();
        }
    }
}
