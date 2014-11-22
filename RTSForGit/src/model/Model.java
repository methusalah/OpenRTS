package model;

import java.io.File;
import java.util.ArrayList;
import model.map.Map;
import model.army.ArmyManager;
import model.army.data.BuilderLibrary;
import model.army.data.definitions.DefParser;
import tools.LogUtil;

public class Model {
    static final String CONFIG_PATH = "assets/data/army";
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
}
