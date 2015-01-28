/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map.cliff.faces.manmade;

import collections.Ring;
import geometry3D.Point3D;
import java.util.ArrayList;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.Trinket;
import model.battlefield.map.cliff.faces.Face;

/**
 *
 * @author Benoît
 */
public abstract class ManmadeFace extends Face {
    public final String modelPath;
    
    public ManmadeFace(Cliff cliff, String modelPath){
    	super(cliff);
        this.modelPath = modelPath;
    }
    
    @Override
    public String getType() {
        return "manmade";
    }
}
