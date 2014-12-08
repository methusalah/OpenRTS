/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff.faces.natural;

import collections.Ring;
import geometry.Point2D;
import geometry3D.Point3D;
import geometry3D.Polygon3D;
import java.awt.Color;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.map.Tile;
import model.map.cliff.Trinket;
import model.map.cliff.Cliff;
import model.map.cliff.faces.Face;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class NaturalFace extends Face {
    public final static int NB_VERTEX_ROWS = 13;
    public final static int NB_VERTEX_COL = 3;
    private final static double NOISE_POWER = 0.13;
    protected final static double MIDDLE_EDGE_VARIATION = 0.1;
    
    protected final static double TOP_ROCK_PROB = 0.05;
    protected final static double SIDE_ROCK_PROB = 0.3;
    protected final static double BOTTOM_ROCK_PROB = 0.5;
    
    protected final static double TOP_PLANT_PROB = 0.1;
    protected final static double BOTTOM_PLANT_PROB = 0.1;
    
    public NaturalFaceMesh mesh;

    Cliff cliff;
    double noiseX, noiseY, noiseZ;
    double ridgeDepth, ridgePos;
    public Color color;
    public String texturePath;
    
    ArrayList<Point3D> parentProfile = new ArrayList<>();
    ArrayList<Point3D> middleProfile = new ArrayList<>();
    ArrayList<Point3D> childProfile = new ArrayList<>();
    Point3D[][] grid;
    
    public NaturalFace(Cliff cliff, double noiseX, double noiseY, double noiseZ, double ridgeDepth, double ridgePos, Color color, String texturePath){
        this.cliff = cliff;
        this.noiseX = noiseX;
        this.noiseY = noiseY;
        this.noiseZ = noiseZ;
        this.ridgeDepth = ridgeDepth;
        this.ridgePos = ridgePos;
        this.color = color;
        this.texturePath = texturePath;
    }

    public NaturalFace(NaturalFace o){
        this.cliff = o.cliff;
        this.noiseX = o.noiseX;
        this.noiseY = o.noiseY;
        this.noiseZ = o.noiseZ;
        this.ridgeDepth = o.ridgeDepth;
        this.ridgePos = o.ridgePos;
        this.color = o.color;
        this.texturePath = o.texturePath;
    }

    @Override
    public String getType() {
        return "natural";
    }
    
    protected ArrayList<Point3D> getChildProfile(){
        if(childProfile.isEmpty()){
            childProfile = noise(createProfile());
        }
        return childProfile;
        
    }
    
    private ArrayList<Point3D> createProfile(){
        ArrayList<Point3D> res = new ArrayList<>();
        res.add(new Point3D(0.6, 0, 0));
        res.add(new Point3D(0.55, 0, 0.07*2));
        res.add(new Point3D(0.48, 0, 0.15*2));
        res.add(new Point3D(0.41, 0, 0.25*2));
        res.add(new Point3D(0.38, 0, 0.38*2));
        res.add(new Point3D(0.36, 0, 0.50*2));
        res.add(new Point3D(0.38, 0, 0.60*2));
        res.add(new Point3D(0.48, 0, 0.7*2));
        res.add(new Point3D(0.55, 0, 0.82*2));
        res.add(new Point3D(0.65, 0, 0.9*2));
        res.add(new Point3D(0.62, 0, 1.01*2));
        res.add(new Point3D(0.40, 0, 1.01*2));
        res.add(new Point3D(0.35, 0, 1*2));
        return res;
    }
    
    private ArrayList<Point3D> noise(ArrayList<Point3D> profile){
        ArrayList<Point3D> res = new ArrayList<>();
        for(Point3D v : profile)
            res.add(v.getAddition((MyRandom.next()-0.5)*NOISE_POWER,
                    (MyRandom.next()-0.5)*NOISE_POWER,
                    (MyRandom.next()-0.5)*NOISE_POWER/10));
        return res;
    }
    
    protected void buildProfiles(){
        if(getParentFace() != null)
            parentProfile = getParentFace().getChildProfile();
        else
            parentProfile = noise(createProfile());
        middleProfile = noise(createProfile());
        if(getChildFace() != null && !getChildFace().parentProfile.isEmpty())
            childProfile = getChildFace().parentProfile;
        else if(childProfile.isEmpty())
            childProfile = noise(createProfile());
    }
    
    protected void extrudeProfile(){
        throw new UnsupportedOperationException("Can't be launched form this mother class.");
    }
    
    protected void buildMesh(){
        grid = new Point3D[3][NB_VERTEX_ROWS];
        buildProfiles();
        extrudeProfile();

        for(int i=0; i<NB_VERTEX_COL; i++)
            for(int j=0; j<NB_VERTEX_ROWS; j++)
                grid[i][j] = grid[i][j].getAddition(-0.5, -0.5, 0);
        mesh = new NaturalFaceMesh(grid);
    }
    private NaturalFace getParentFace(){
        if(cliff.parent != null &&
                cliff.parent.cliff != null &&
                cliff.parent.cliff.face instanceof NaturalFace)
            return (NaturalFace)(cliff.parent.cliff.face);
        else
            return null;
    }
    private NaturalFace getChildFace(){
        if(cliff.child != null &&
                cliff.child.cliff != null &&
                cliff.child.cliff.face instanceof NaturalFace)
            return (NaturalFace)(cliff.child.cliff.face);
        else
            return null;
    }

    @Override
    public ArrayList<Ring<Point3D>> getGrounds() {
        throw new UnsupportedOperationException("Can't be launched form this mother class.");
    }
}
