package model.battlefield.map.cliff.faces.natural;

import geometry.collections.Ring;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import geometry.math.RandomUtil;

import java.awt.Color;
import java.util.ArrayList;

import model.battlefield.map.cliff.Cliff;

public class Dug1Corner extends Dug1 {
    private final static double RIDGE_PROTRUDE = 0;
    private final static double RIDGE_RETREAT = 0.6;

    public Dug1Corner(Cliff cliff, double noiseX, double noiseY, double noiseZ, double ridgeDepth, double ridgePos, Color color, String texturePath) {
        super(cliff, noiseX, noiseY, noiseZ, ridgeDepth, ridgePos, color, texturePath);
        buildMesh();
    }

    @Override
    protected void extrudeProfile() {
        int i = 0;
        double ridgeDepth = RandomUtil.between(1+RIDGE_PROTRUDE*ridgeDepthRange, 1-RIDGE_RETREAT*ridgeDepthRange);
        double ridgePos = RandomUtil.between(1+MAX_RIDGE_POS*ridgePosRange, 1-MAX_RIDGE_POS*ridgePosRange);
        
        for(Point3D v : mirror(parentProfile))
            grid[0][i++] = v;
        i = 0;
        for(Point3D v : mirror(middleProfile))
            grid[1][i++] = v.get2D().getRotation(AngleUtil.RIGHT/2*ridgePos).getMult(ridgeDepth).get3D(v.z);
        i = 0;
        for(Point3D v : mirror(childProfile))
            grid[2][i++] = v.get2D().getRotation(AngleUtil.RIGHT).get3D(v.z);
        
        
    }
    
    private ArrayList<Point3D> mirror(ArrayList<Point3D> profile){
        ArrayList<Point3D> res = new ArrayList<>();
        for(Point3D v : profile)
            res.add(new Point3D(1-v.x, -v.y, v.z));
        return res;
    }


    @Override
    public Ring<Point3D> getLowerGround() {
        Ring<Point3D> res = new Ring<>();
        Point3D sw = new Point3D(-0.5, -0.5, 0);

        res.add(sw);
        for(int i=0; i<NB_VERTEX_COL; i++)
            res.add(grid[i][0]);

        return getRotation(res);
    }
    
    @Override
    public Ring<Point3D> getUpperGround() {
        Point3D se = new Point3D(0.5, -0.5, 0);
        Point3D ne = new Point3D(0.5, 0.5, 0);
        Point3D nw = new Point3D(-0.5, 0.5, 0);

        Ring<Point3D> res = new Ring<>();

        res.add(se);
        res.add(ne);
        res.add(nw);
        for(int i=NB_VERTEX_COL-1; i>=0; i--)
            res.add(grid[i][NB_VERTEX_ROWS-1]);
        return getRotation(res);
    }
}
