/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff.faces;

import collections.Ring;
import geometry3D.Point3D;
import java.util.ArrayList;
import model.map.cliff.Cliff;

/**
 *
 * @author Benoît
 */
public abstract class ManmadeFace {
    String modelPath;
    
    Cliff cliff;
    
    public abstract ArrayList<Ring<Point3D>> getGrounds();
}
