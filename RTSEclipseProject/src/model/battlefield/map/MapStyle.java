/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map;

import java.util.ArrayList;
import model.builders.CliffShapeBuilder;

/**
 *
 * @author Benoît
 */
public class MapStyle {
    public ArrayList<CliffShapeBuilder> cliffShapes = new ArrayList<>();
    
    public ArrayList<String> textures = new ArrayList<>();
    public ArrayList<String> normals = new ArrayList<>();
    public ArrayList<Double> scales = new ArrayList<>();
}
