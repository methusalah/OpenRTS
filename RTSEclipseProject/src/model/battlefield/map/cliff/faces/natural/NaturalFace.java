package model.battlefield.map.cliff.faces.natural;

import geometry.geom3d.Point3D;

import java.awt.Color;
import java.util.ArrayList;

import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.faces.Face;

/**
 * A face that creates a mesh procedurally as its shape. 
 * 
 */
public abstract class NaturalFace extends Face {
    public NaturalFaceMesh mesh;

    final double noiseX, noiseY, noiseZ;
    final double ridgeDepthRange, ridgePosRange;
    final public Color color;
    final public String texturePath;
    
    ArrayList<Point3D> parentProfile = new ArrayList<>();
    ArrayList<Point3D> middleProfile = new ArrayList<>();
    ArrayList<Point3D> childProfile = new ArrayList<>();
    Point3D[][] grid;
    
    public NaturalFace(Cliff cliff, double noiseX, double noiseY, double noiseZ, double ridgeDepth, double ridgePos, Color color, String texturePath){
        super(cliff);
        this.noiseX = noiseX;
        this.noiseY = noiseY;
        this.noiseZ = noiseZ;
        this.ridgeDepthRange = ridgeDepth;
        this.ridgePosRange = ridgePos;
        this.color = color;
        this.texturePath = texturePath;
    }

    @Override
    public String getType() {
        return "natural";
    }
    
    protected NaturalFace getParentFace(){
        if(cliff.hasParent() &&
                cliff.getParent() != null &&
                cliff.getParent().face instanceof NaturalFace)
            return (NaturalFace)(cliff.getParent().face);
        else
            return null;
    }
    protected NaturalFace getChildFace(){
        if(cliff.hasChild() &&
                cliff.getChild() != null &&
                cliff.getChild().face instanceof NaturalFace)
            return (NaturalFace)(cliff.getChild().face);
        else
            return null;
    }
}
