/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff;

import collections.Ring;
import geometry.Point2D;
import geometry3D.Point3D;
import geometry3D.Polygon3D;
import java.util.ArrayList;
import math.MyRandom;

/**
 *
 * @author Benoît
 */
public abstract class CliffShape {
    public enum Type{Orthogonal, Salient, Corner}

    public final static int NB_VERTEX_ROWS = 13;
    public final static int NB_VERTEX_COL = 3;
    private final static double NOISE_POWER = 0.13;
    protected final static double MIDDLE_EDGE_VARIATION = 0.1;
    
    Cliff cliff;
    
    public double angle = 0;
    public Point2D pivot;
    Point3D[][] grid = null;

    ArrayList<Point3D> startingProfile = new ArrayList<>();
    ArrayList<Point3D> profile1 = new ArrayList<>();
    ArrayList<Point3D> endingProfile = new ArrayList<>();
    
    public CliffShape(Cliff cliff, double angle, Point2D pivot){
        this.cliff = cliff;
        this.angle = angle;
        this.pivot = pivot;
    }
    
    protected ArrayList<Point3D> getEndingProfile(){
        if(endingProfile.isEmpty()){
            endingProfile = noise(createProfile());
        }
        return endingProfile;
        
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
    
    private void buildProfiles(){
        if(getParentShape() != null)
            startingProfile = getParentShape().getEndingProfile();
        else
            startingProfile = noise(createProfile());
        profile1 = noise(createProfile());
        if(endingProfile.isEmpty())
            endingProfile = noise(createProfile());
    }
    
    protected abstract void extrudeProfile();
    
    public Point3D[][] getGrid(){
        if(grid != null)
            return grid;

        grid = new Point3D[3][NB_VERTEX_ROWS];
        buildProfiles();
        extrudeProfile();

        for(int i=0; i<NB_VERTEX_COL; i++)
            for(int j=0; j<NB_VERTEX_ROWS; j++)
                grid[i][j] = grid[i][j].getAddition(-0.5, -0.5, 0);
        return grid;
    }
    
    public abstract ArrayList<Ring<Point3D>> getGrounds();
    
    private CliffShape getParentShape(){
        if(cliff.parent != null)
            return cliff.parent.shape;
        else
            return null;
    }
    
    public abstract Type getType();

    
}
