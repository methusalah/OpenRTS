/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map.cliff.faces;

import collections.Ring;
import geometry3D.Point3D;
import java.util.ArrayList;
import model.battlefield.map.Trinket;

/**
 *
 * @author Benoît
 */
public abstract class Face {
        public abstract ArrayList<Ring<Point3D>> getGrounds();
        public abstract String getType();
}
