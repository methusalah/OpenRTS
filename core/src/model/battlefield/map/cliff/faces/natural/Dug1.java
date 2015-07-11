package model.battlefield.map.cliff.faces.natural;

import geometry.geom3d.Point3D;
import geometry.math.RandomUtil;

import java.awt.Color;
import java.util.ArrayList;

import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;

public abstract class Dug1 extends NaturalFace {
    public final static int NB_VERTEX_ROWS = 13;
    public final static int NB_VERTEX_COL = 3;
    private final static double MAX_NOISE = 0.26;
    protected final static double MAX_RIDGE_POS = 0.3;

    public Dug1(Cliff cliff, double noiseX, double noiseY, double noiseZ, double ridgeDepth, double ridgePos, Color color, String texturePath) {
        super(cliff, noiseX, noiseY, noiseZ, ridgeDepth, ridgePos, color, texturePath);
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
        	if(profile.indexOf(v)==0)
        		res.add(v);
    		else
	            res.add(v.getAddition((RandomUtil.next()-0.5)*MAX_NOISE*noiseX,
	                    (RandomUtil.next()-0.5)*MAX_NOISE*noiseY,
	                    (RandomUtil.next()-0.5)*(MAX_NOISE/10)*noiseZ));
        return res;
    }
    
    protected void buildProfiles(){
        // compute pinch ratios for ramps
        double parentPinch = 0, middlePinch = 0, childPinch = 0;
        Ramp r = cliff.getTile().ramp;
        if(r != null){
            double slope = r.getCliffSlopeRate(cliff.getTile());
            Tile parentTile = cliff.getParent() == null? null : cliff.getParent().getTile();
            if(parentTile == null){
                parentPinch = 0.99;
                middlePinch = (slope+1)/2;
                childPinch = slope;
            } else {
                double parentSlope = r.getCliffSlopeRate(parentTile);
                 if(parentTile.ramp != r)
                     parentSlope = -0.1;
                if(parentSlope >= slope){
                    parentPinch = parentSlope;
                    middlePinch = (slope+parentSlope)/2;
                    childPinch = slope;
                } else {
                	Tile childTile = cliff.getChild() == null? null : cliff.getChild().getTile(); 
                    if(childTile == null){
                        parentPinch = slope;
                        middlePinch = (slope+1)/2;
                        childPinch = 0.99;
                    } else {
                        double childSlope = r.getCliffSlopeRate(childTile);
                        if(childTile.ramp != r)
                            childSlope = 0;
                        parentPinch = slope;
                        middlePinch = (slope+childSlope)/2;
                        childPinch = childSlope;
                    }
                }
            }
        }
        
        // elevating profiles according to ground elevation
        double parentZ = 0;//cliff.parent != null? cliff.parent.elevation : cliff.tile.elevation;
        double childZ = 0;//cliff.tile.elevation;
        double middleZ = 0;//(parentZ+childZ)/2;
        

        // compute profiles
        if(getParentFace() != null && getParentFace() instanceof Dug1)
            parentProfile = ((Dug1)getParentFace()).getChildProfile();
        else
            parentProfile = elevate(pinch(noise(createProfile()), parentPinch), parentZ);
        
        middleProfile = elevate(pinch(noise(createProfile()), middlePinch), middleZ);
        
        if(getChildFace() != null && !getChildFace().parentProfile.isEmpty())
            childProfile = getChildFace().parentProfile;
        else if(childProfile.isEmpty())
            childProfile = elevate(pinch(noise(createProfile()), childPinch), childZ);
        
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
    
    private ArrayList<Point3D> pinch(ArrayList<Point3D> profile, double ratio){
        if(ratio == 0)
            return profile;
        ArrayList<Point3D> res = new ArrayList<>();
        for(Point3D p : profile){
            if(ratio == 1)
                res.add(new Point3D(0.5+RandomUtil.next()*0.001, 0, 0));
            else
                res.add(p.getAddition(0, 0, -Tile.STAGE_HEIGHT*ratio*p.z/Tile.STAGE_HEIGHT));
        }
        return res;
    }

    private ArrayList<Point3D> elevate(ArrayList<Point3D> profile, double elevation){
        if(elevation == 0)
            return profile;
        ArrayList<Point3D> res = new ArrayList<>();
        for(Point3D p : profile)
            res.add(p.getAddition(0, 0, elevation));
        return res;
    }
}
