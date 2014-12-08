/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff;

import geometry3D.Point3D;
import java.awt.Color;

/**
 *
 * @author Benoît
 */
public class Trinket {
    public String modelPath;
    public Point3D pos;
    public double scaleX = 1;
    public double scaleY = 1;
    public double scaleZ = 1;
    public double rotX = 0;
    public double rotY = 0;
    public double rotZ = 0;
    public Color color;
    
    public Trinket(String modelPath){
        this.modelPath = modelPath;
    }
}
