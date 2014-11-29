package model;

import geometry.Point2D;
import java.io.File;
import java.util.ArrayList;
import model.map.Map;
import model.army.ArmyManager;
import model.army.data.BuilderLibrary;
import model.map.MapEditor;
import model.map.MapFactory;
import model.map.parcel.ParcelManager;
import ressources.definitions.DefParser;
import tools.LogUtil;

public class Model {
    static final String CONFIG_PATH = "assets/data/army";
    static final double UPDATE_DELAY = 1000;
    
    public Map map;
    public ArmyManager armyManager;
    
    public Commander commander;
    public Reporter reporter;
    public MapEditor editor;
    public ParcelManager parcelManager;
    

    public BuilderLibrary lib;
    DefParser parser;
    File confFile;
    long lastModified = 0;
    double nextUpdate = 0;
    
    public Model() {
        this.map = MapFactory.getNewMap(128, 128);
        parcelManager = new ParcelManager(map);
        editor = new MapEditor(map, parcelManager);

//        for(int x=10; x<20; x++)
//            for(int y=10; y<20; y++)
//                editor.levelUp(new Point2D(x, y));
//        editor.levelUp(new Point2D(11, 11));
//        editor.incHeight(new Point2D(5, 5));
//        editor.incHeight(new Point2D(5, 5));
        
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
