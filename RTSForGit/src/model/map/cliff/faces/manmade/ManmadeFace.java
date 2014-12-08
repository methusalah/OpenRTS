/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff.faces.manmade;

import collections.Ring;
import geometry3D.Point3D;
import java.util.ArrayList;
import model.map.cliff.Cliff;
import model.map.cliff.Trinket;
import model.map.cliff.faces.Face;

/**
 *
 * @author Benoît
 */
public abstract class ManmadeFace extends Face {
    public String modelPath;
    
    public ManmadeFace(String modelPath){
        this.modelPath = modelPath;
    }
    
    @Override
    public String getType() {
        return "manmade";
    }
}
