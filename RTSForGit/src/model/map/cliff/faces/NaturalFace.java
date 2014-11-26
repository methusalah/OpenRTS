/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff.faces;

import collections.Ring;
import geometry.Point2D;
import geometry3D.Point3D;
import geometry3D.Polygon3D;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.map.Asset;
import model.map.cliff.Cliff;

/**
 *
 * @author Benoît
 */
public abstract class NaturalFace {
    public final static int NB_VERTEX_ROWS = 13;
    public final static int NB_VERTEX_COL = 3;
    private final static double NOISE_POWER = 0.13;
    protected final static double MIDDLE_EDGE_VARIATION = 0.1;
    
    protected final static double TOP_ROCK_PROB = 0.05;
    protected final static double SIDE_ROCK_PROB = 0.3;
    protected final static double BOTTOM_ROCK_PROB = 0.5;
    
    protected final static double TOP_PLANT_PROB = 0.1;
    protected final static double BOTTOM_PLANT_PROB = 0.1;
    
    Cliff cliff;
    
    public double angle = 0;
    public Point2D pivot;
    Point3D[][] grid = null;

    ArrayList<Point3D> startingProfile = new ArrayList<>();
    ArrayList<Point3D> profile1 = new ArrayList<>();
    ArrayList<Point3D> endingProfile = new ArrayList<>();
    
    public NaturalFace(Cliff cliff, double angle, Point2D pivot){
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
    
    public ArrayList<Asset> getAssets(){
                ArrayList<Asset> res = new ArrayList<>();
        if(MyRandom.next()<TOP_ROCK_PROB){
            Asset a = new Asset("models/env/exterior01/rockA.mesh.xml");
            a.pos = Point3D.ORIGIN.getAddition(MyRandom.between(-0.3, 0.3),
                    0, 
                    MyRandom.between(0.6*Cliff.STAGE_HEIGHT, 0.9*Cliff.STAGE_HEIGHT));
            a.scale = MyRandom.between(0.7, 1.4);
            a.rotX = Angle.FLAT*2*MyRandom.next();
            a.rotY = Angle.FLAT*2*MyRandom.next();
            a.rotZ = Angle.FLAT*2*MyRandom.next();
            res.add(a);
        }
        if(MyRandom.next()<SIDE_ROCK_PROB){
            Asset a = new Asset("models/env/exterior01/rockA.mesh.xml");
            a.pos = Point3D.ORIGIN.getAddition(MyRandom.between(-0.2, 0.2),
                    0,
                    MyRandom.between(0.1*Cliff.STAGE_HEIGHT, 0.6*Cliff.STAGE_HEIGHT));
            a.scale = MyRandom.between(0.7, 1.4);
            a.rotX = Angle.FLAT*2*MyRandom.next();
            a.rotY = Angle.FLAT*2*MyRandom.next();
            a.rotZ = Angle.FLAT*2*MyRandom.next();
            res.add(a);
        }
        if(MyRandom.next()<BOTTOM_ROCK_PROB){
            Asset a = new Asset("models/env/exterior01/rockA.mesh.xml");
            a.pos = Point3D.ORIGIN.getAddition(MyRandom.between(0.3, 0.8),
                    0,
                    MyRandom.between(-0.3*Cliff.STAGE_HEIGHT, 0*Cliff.STAGE_HEIGHT));
            a.scale = MyRandom.between(0.7, 1.4);
            a.rotX = Angle.FLAT*2*MyRandom.next();
            a.rotY = Angle.FLAT*2*MyRandom.next();
            a.rotZ = Angle.FLAT*2*MyRandom.next();
            res.add(a);
        }
        if(MyRandom.next()<TOP_PLANT_PROB){
            Asset a = new Asset("models/env/exterior01/GrassB.mesh.xml");
            a.pos = Point3D.ORIGIN.getAddition(MyRandom.between(-0.3, 0.3),
                    0, 
                    Cliff.STAGE_HEIGHT);
            a.scale = MyRandom.between(0.7, 1.4);
            a.rotZ = Angle.FLAT*2*MyRandom.next();
            res.add(a);
        }
        if(MyRandom.next()<BOTTOM_PLANT_PROB){
            Asset a = new Asset("models/env/exterior01/GrassB.mesh.xml");
            a.pos = Point3D.ORIGIN.getAddition(MyRandom.between(0.7, 1.5),
                    0,
                    0);
            a.scale = MyRandom.between(0.7, 1.4);
            a.rotZ = Angle.FLAT*2*MyRandom.next();
            res.add(a);
        }
        return res;

    }
    
    public abstract ArrayList<Ring<Point3D>> getGrounds();
    
    private NaturalFace getParentShape(){
        if(cliff.parent != null)
            return cliff.parent.naturalFace;
        else
            return null;
    }

    public boolean isNatural(){
        return !cliff.urban;
    }
    
}
