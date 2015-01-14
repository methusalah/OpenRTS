/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield;

import java.util.ArrayList;
import model.battlefield.actors.Actor;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.components.Unit;
import model.battlefield.lighting.SunLight;
import model.battlefield.map.Map;
import model.battlefield.map.parcel.ParcelManager;
import model.battlefield.actors.ActorPool;
import model.battlefield.army.Engagement;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import ressources.definitions.BuilderLibrary;

/**
 *
 * @author bedu
 */
@Root
public class Battlefield {
    @Element
    public final Map map;
    @Element
    public final Engagement engagement;
    
    public String fileName;

    public final ArmyManager armyManager;
    public ParcelManager parcelManager;
    public final SunLight sunLight;
    public final ActorPool actorPool;
    
    public Battlefield(@Element(name="map")Map unfinishedMap, @Element(name="engagement")Engagement engagement){
        this.map = unfinishedMap;
        this.engagement = engagement;
        armyManager = new ArmyManager();
        sunLight = new SunLight();
        actorPool = new ActorPool();
    }
    
    public Battlefield(Map map, BuilderLibrary lib) {
        this.map = map;
        armyManager = new ArmyManager();
        parcelManager = new ParcelManager(map);
        sunLight = new SunLight();
        actorPool = new ActorPool();
        engagement = new Engagement(this, lib);
    }
    
    public void buildParcels(){
        parcelManager = new ParcelManager(map);
    }
}
