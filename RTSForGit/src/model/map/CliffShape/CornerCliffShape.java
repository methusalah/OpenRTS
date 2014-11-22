/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.CliffShape;

import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.map.Cliff;

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
            vertices[0][i++] = v;
        i = 0;
        for(Point3D v : mirror(profile1))
            vertices[1][i++] = v.get2D().getRotation(Angle.RIGHT/2).getMult(curve).get3D(v.z);
        i = 0;
        for(Point3D v : mirror(endingProfile))
            vertices[2][i++] = v.get2D().getRotation(Angle.RIGHT).get3D(v.z);
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
}
