package model.battlefield.map;

import java.util.ArrayList;
import java.util.List;

import model.builders.CliffShapeBuilder;

/**
 *
 */
public class MapStyle {
    public List<CliffShapeBuilder> cliffShapeBuilders = new ArrayList<>();
    
    public List<String> diffuses = new ArrayList<>();
    public List<String> normals = new ArrayList<>();
    public List<Double> scales = new ArrayList<>();

    public List<String> coverDiffuses = new ArrayList<>();
    public List<String> coverNormals = new ArrayList<>();
    public List<Double> coverScales = new ArrayList<>();
}
