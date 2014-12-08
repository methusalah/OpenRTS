/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff.faces.natural;

import geometry3D.Point3D;
import java.util.ArrayList;
import math.MyRandom;

/**
 *
 * @author Benoît
 */
public abstract class Dug1 extends NaturalFace {
    public final static int NB_VERTEX_ROWS = 13;
    public final static int NB_VERTEX_COL = 3;
    private final static double MAX_NOISE = 0.26;
    protected final static double MAX_RIDGE_POS = 0.3;

    public Dug1(NaturalFace o) {
        super(o);
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
            res.add(v.getAddition((MyRandom.next()-0.5)*MAX_NOISE*noiseX,
                    (MyRandom.next()-0.5)*MAX_NOISE*noiseY,
                    (MyRandom.next()-0.5)*(MAX_NOISE/10)*noiseZ));
        return res;
    }
    
    protected void buildProfiles(){
        if(getParentFace() != null && getParentFace() instanceof Dug1)
            parentProfile = ((Dug1)getParentFace()).getChildProfile();
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
        mesh = new Dug1Mesh(grid);
    }
}
