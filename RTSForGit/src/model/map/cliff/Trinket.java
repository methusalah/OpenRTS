/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff;

import geometry3D.Point3D;

/**
 *
 * @author Benoît
 */
public class Trinket {
    public String path;
    public Point3D pos;
    public double scale = 1;
    public double rotX = 0;
    public double rotY = 0;
    public double rotZ = 0;
    
    public Trinket(String path){
        this.path = path;
    }
}
