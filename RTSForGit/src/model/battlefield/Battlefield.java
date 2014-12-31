/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield;

import java.util.ArrayList;
import model.army.ArmyManager;
import model.army.data.Unit;
import model.lighting.SunLight;
import model.map.Map;
import model.map.MapFactory;
import model.map.parcel.ParcelManager;

/**
 *
 * @author bedu
 */
public class Battlefield {
    public static enum Instanciation {Load, New};
    public Map map;
    public ArmyManager armyManager;
    public ParcelManager parcelManager;
    public SunLight sunLight;
    
    public ArrayList<Unit> startUnits = new ArrayList<>();

    public Battlefield(Map map, ArmyManager armyManager, ParcelManager parcelManager, SunLight sunLight) {
        this.map = map;
        this.armyManager = armyManager;
        this.parcelManager = parcelManager;
        this.sunLight = sunLight;
    }
    
    public Battlefield(Map map, Instanciation instanciation){
        switch (instanciation){
            case New : 
                this.map = map;
                sunLight = new SunLight();
                parcelManager = new ParcelManager(map);
                armyManager = new ArmyManager();
                break;
            case Load : 
                break;
        }
        
    }
}
