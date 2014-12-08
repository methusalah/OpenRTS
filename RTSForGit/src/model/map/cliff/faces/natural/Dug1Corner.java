/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff.faces.natural;

import collections.Ring;
import geometry.Point2D;
import geometry3D.Point3D;
import geometry3D.Polygon3D;
import geometry3D.Triangle3D;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.map.Tile;
import model.map.cliff.Trinket;
import model.map.cliff.Cliff;
import static model.map.cliff.faces.natural.Dug1.MAX_RIDGE_POS;

/**
 *
 * @author Benoît
 */
public class Dug1Corner extends Dug1 {
    private final static double RIDGE_PROTRUDE = 0;
    private final static double RIDGE_RETREAT = 0.6;

    public Dug1Corner(NaturalFace face){
        super(face);
        buildMesh();
    }

    @Override
    protected void extrudeProfile() {
        int i = 0;
        double ridgeDepth = MyRandom.between(1+RIDGE_PROTRUDE*ridgeDepthRange, 1-RIDGE_RETREAT*ridgeDepthRange);
        double ridgePos = MyRandom.between(1+MAX_RIDGE_POS*ridgePosRange, 1-MAX_RIDGE_POS*ridgePosRange);
        
        for(Point3D v : mirror(parentProfile))
            grid[0][i++] = v;
        i = 0;
        for(Point3D v : mirror(middleProfile))
            grid[1][i++] = v.get2D().getRotation(Angle.RIGHT/2*ridgePos).getMult(ridgeDepth).get3D(v.z);
        i = 0;
        for(Point3D v : mirror(childProfile))
            grid[2][i++] = v.get2D().getRotation(Angle.RIGHT).get3D(v.z);
    }
    
    private ArrayList<Point3D> mirror(ArrayList<Point3D> profile){
        ArrayList<Point3D> res = new ArrayList<>();
        for(Point3D v : profile)
            res.add(new Point3D(1-v.x, -v.y, v.z));
        return res;
    }


    @Override
    public ArrayList<Ring<Point3D>> getGrounds() {
        Point3D sw = new Point3D(-0.5, -0.5, 0);
        Point3D se = new Point3D(0.5, -0.5, 0);
        Point3D ne = new Point3D(0.5, 0.5, 0);
        Point3D nw = new Point3D(-0.5, 0.5, 0);

        Ring<Point3D> lowerPoints = new Ring<>();
        Ring<Point3D> upperPoints = new Ring<>();

        lowerPoints.add(sw);
        for(int i=0; i<NB_VERTEX_COL; i++)
            lowerPoints.add(grid[i][0]);

        upperPoints.add(se);
        upperPoints.add(ne);
        upperPoints.add(nw);
        for(int i=NB_VERTEX_COL-1; i>=0; i--)
            upperPoints.add(grid[i][NB_VERTEX_ROWS-1]);
        
        ArrayList<Ring<Point3D>> res = new ArrayList<>();
        res.add(lowerPoints);
        res.add(upperPoints);
        return res;
    }
}
