/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff;

import collections.Ring;
import geometry.Point2D;
import geometry3D.Point3D;
import geometry3D.Polygon3D;
import geometry3D.Triangle3D;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import static model.map.cliff.CliffShape.MIDDLE_EDGE_VARIATION;

/**
 *
 * @author Benoît
 */
public class CornerCliffShape extends CliffShape {
    public CornerCliffShape(Cliff cliff, double angle, Point2D pivot){
        super(cliff, angle, pivot);
    }

    @Override
    protected void extrudeProfile() {
        int i = 0;
        double curve = MyRandom.between(0.7, 1);
        
        for(Point3D v : mirror(startingProfile))
            grid[0][i++] = v;
        i = 0;
        for(Point3D v : mirror(profile1))
            grid[1][i++] = v.get2D().getRotation(Angle.RIGHT/2*MyRandom.between(1+MIDDLE_EDGE_VARIATION, 1-MIDDLE_EDGE_VARIATION)).getMult(curve).get3D(v.z);
        i = 0;
        for(Point3D v : mirror(endingProfile))
            grid[2][i++] = v.get2D().getRotation(Angle.RIGHT).get3D(v.z);
    }
    
    private ArrayList<Point3D> mirror(ArrayList<Point3D> profile){
        ArrayList<Point3D> res = new ArrayList<>();
        for(Point3D v : profile)
            res.add(new Point3D(0.5-(v.x-0.5), -v.y, v.z));
        return res;
    }

    @Override
    public Type getType() {
        return Type.Corner;
    }

    @Override
    public ArrayList<Ring<Point3D>> getGrounds() {
        Point3D sw = new Point3D(-0.5, -0.5, 0);
        Point3D se = new Point3D(0.5, -0.5, Cliff.STAGE_HEIGHT);
        Point3D ne = new Point3D(0.5, 0.5, Cliff.STAGE_HEIGHT);
        Point3D nw = new Point3D(-0.5, 0.5, Cliff.STAGE_HEIGHT);

        Ring<Point3D> lowerPoints = new Ring<>();
        Ring<Point3D> upperPoints = new Ring<>();

        lowerPoints.add(sw);
        for(int i=0; i<CliffShape.NB_VERTEX_COL; i++)
            lowerPoints.add(getGrid()[i][0]);

        upperPoints.add(se);
        upperPoints.add(ne);
        upperPoints.add(nw);
        for(int i=CliffShape.NB_VERTEX_COL-1; i>=0; i--)
            upperPoints.add(getGrid()[i][CliffShape.NB_VERTEX_ROWS-1]);
        
        ArrayList<Ring<Point3D>> res = new ArrayList<>();
        res.add(lowerPoints);
        res.add(upperPoints);
        return res;
    }
    
    
}
