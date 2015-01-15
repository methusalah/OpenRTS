/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map;

import java.util.ArrayList;
import java.util.List;

import model.builders.CliffShapeBuilder;

/**
 *
 * @author Benoît
 */
public class MapStyle {
    public List<CliffShapeBuilder> cliffShapeBuilders = new ArrayList<>();
    
    public List<String> textures = new ArrayList<>();
    public List<String> normals = new ArrayList<>();
    public List<Double> scales = new ArrayList<>();
}
