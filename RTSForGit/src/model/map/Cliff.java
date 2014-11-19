package model.map;

import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import static model.map.Tile.STAGE_HEIGHT;
import tools.LogUtil;

public class Cliff extends Tile {
    public final static int NB_VERTEX_ROWS = 9;
    public final static int NB_VERTEX_COL = 3;
    private final static double NOISE_POWER = 0.15;
    
	public double angle = 0;
        public Point2D pivot;
        public Cliff previous;
	public boolean ortho = false;
	public boolean acuteDiag = false;
	public boolean obtuseDiag = false;
        Point3D[][] vertices = null;
        
        ArrayList<Point3D> startingProfile = new ArrayList<>();
        ArrayList<Point3D> profile1 = new ArrayList<>();
        ArrayList<Point3D> endingProfile = new ArrayList<>();

	public Cliff(Tile t) {
            super(t);
	}
	public Cliff(int x, int y, int level) {
            super(x, y, level);
            pivot = getPos2D();
	}
	
	public void ComputeAngle() {
		if(n == null || s == null || e == null || w == null)
			return;
		
		// orthogonal
		if(n.isCliff() && s.isCliff()){
			ortho = true;
			if(e.z>w.z){
				angle = Angle.FLAT;
                                previous = (Cliff)s;
                        } else {
				angle = 0;
                                previous = (Cliff)n;
                        }
		} else if(e.isCliff() && w.isCliff()) {
			ortho = true;
			if(n.z>s.z){
				angle = -Angle.RIGHT;
                                previous = (Cliff)e;
                        } else {
				angle = Angle.RIGHT;
                                previous = (Cliff)w;
                        }
		// digonal	
		} else if(w.isCliff() && s.isCliff()) {
			angle = 0;
			if(w.getNeighborsMaxLevel()>getNeighborsMaxLevel()){
				acuteDiag = true;
                                previous = (Cliff)w;
                        } else {
				obtuseDiag = true;
                                previous = (Cliff)s;
                        }
		} else if(s.isCliff() && e.isCliff()) {
			angle = Angle.RIGHT;
                        pivot = pivot.getAddition(1, 0);
			if(s.getNeighborsMaxLevel()>getNeighborsMaxLevel()){
				acuteDiag = true;
                                previous = (Cliff)s;
                        } else {
				obtuseDiag = true;
                                previous = (Cliff)e;
                        }
		} else if(e.isCliff() && n.isCliff()) {
			angle = Angle.FLAT;
                        pivot = pivot.getAddition(1, 1);
			if(e.getNeighborsMaxLevel()>getNeighborsMaxLevel()){
				acuteDiag = true;
                                previous = (Cliff)e;
                        } else {
				obtuseDiag = true;
                                previous = (Cliff)n;
                        }
		} else if(n.isCliff() && w.isCliff()) {
			angle = -Angle.RIGHT;
                        pivot = pivot.getAddition(0, 1);
			if(n.getNeighborsMaxLevel()>getNeighborsMaxLevel()){
				acuteDiag = true;
                                previous = (Cliff)n;
                        } else {
				obtuseDiag = true;
                                previous = (Cliff)w;
                        }
		}
                
                
                if(w!=null && w.level > level ||
                        s!=null && s.level > level ||
                        w.s!=null && w.s.level > level)
                    z = (level+1)*STAGE_HEIGHT;
	}

    @Override
    public boolean isBlocked() {
        return true;
    }

    @Override
    public boolean isCliff() {
        return true;
    }
    
    protected ArrayList<Point3D> getEndingProfile(){
        if(endingProfile.isEmpty()){
            endingProfile = noise(createProfile());
        }
        return endingProfile;
        
    }
    
    private ArrayList<Point3D> createProfile(){
        ArrayList<Point3D> res = new ArrayList<>();
//        for(int i=0; i<NB_VERTEX_ROWS; i++){
//            double offset = 0;
//            if(i == 0)
//                offset = 0.2;
//            if(i == NB_VERTEX_ROWS-1)
//                offset = -0.2;
//            res.add(new Point3D(0.5+offset, 0, (double)STAGE_HEIGHT/(NB_VERTEX_ROWS-1)*i));
//        }
        res.add(new Point3D(0.7, 0, 0));
        res.add(new Point3D(0.60, 0, 0.1*2));
        res.add(new Point3D(0.35, 0, 0.26*2));
        res.add(new Point3D(0.30, 0, 0.51*2));
        res.add(new Point3D(0.40, 0, 0.70*2));
        res.add(new Point3D(0.55, 0, 0.86*2));
        res.add(new Point3D(0.61, 0, 0.96*2));
        res.add(new Point3D(0.4, 0, 1.01*2));
        res.add(new Point3D(0.3, 0, 1*2));
        return res;
    }
    
    private ArrayList<Point3D> noise(ArrayList<Point3D> profile){
        ArrayList<Point3D> res = new ArrayList<>();
        for(Point3D v : profile)
            res.add(v.getAddition((MyRandom.next()-0.5)*NOISE_POWER,
                    (MyRandom.next()-0.5)*NOISE_POWER,
                    0));//(MyRandom.next()-0.5)*NOISE_POWER));
        return res;
    }
    
    private ArrayList<Point3D> mirror(ArrayList<Point3D> profile){
        ArrayList<Point3D> res = new ArrayList<>();
        for(Point3D v : profile)
            res.add(new Point3D(0.5-(v.x-0.5), -v.y, v.z));
        return res;
    }
    
    public void buildProfiles(){
        if(previous != null)
            startingProfile = previous.getEndingProfile();
        else
            startingProfile = noise(createProfile());
        profile1 = noise(createProfile());
        if(endingProfile.isEmpty())
            endingProfile = noise(createProfile());
    }
    
    public Point3D[][] getVertices(){
        if(vertices != null)
            return vertices;
        
        buildProfiles();
        vertices = new Point3D[3][NB_VERTEX_ROWS];
        if(ortho){
            int i = 0;
            double curve = MyRandom.between(0.7, 1.3);
            for(Point3D v : startingProfile)
                vertices[0][i++] = v.getAddition(0, 1, 0);
            i = 0;
            for(Point3D v : profile1)
                vertices[1][i++] = v.getAddition(0, 0.5, 0).get2D().getMult(curve).get3D(v.z);
            i = 0;
            for(Point3D v : endingProfile)
                vertices[2][i++] = v;
            
        } else if(acuteDiag){
            int i = 0;
            double curve = MyRandom.between(0.7, 1);
            for(Point3D v : startingProfile)
                vertices[0][i++] = v.get2D().getRotation(Angle.RIGHT).get3D(v.z);
            i = 0;
            for(Point3D v : profile1)
                vertices[1][i++] = v.get2D().getRotation(Angle.RIGHT/2).getMult(curve).get3D(v.z);
            i = 0;
            for(Point3D v : endingProfile)
                vertices[2][i++] = v;
        } else {
            int i = 0;
            double curve = MyRandom.between(0.7, 1);
            for(Point3D v : mirror(startingProfile))
                vertices[0][i++] = v;
            i = 0;
            for(Point3D v : mirror(profile1))
                vertices[1][i++] = v.get2D().getRotation(Angle.RIGHT/2).getMult(curve).get3D(v.z);
            i = 0;
            for(Point3D v : mirror(endingProfile))
                vertices[2][i++] = v.get2D().getRotation(Angle.RIGHT).get3D(v.z);
        }
        
        for(int i=0; i<NB_VERTEX_COL; i++)
            for(int j=0; j<NB_VERTEX_ROWS; j++)
                vertices[i][j] = vertices[i][j].getAddition(-0.5, -0.5, 0);
        return vertices;
    }
    
    
    
        
        
}
