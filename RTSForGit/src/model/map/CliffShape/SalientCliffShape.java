/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.CliffShape;

import geometry.Point2D;
import geometry3D.Point3D;
import math.Angle;
import math.MyRandom;
import model.map.Cliff;

/**
 *
 * @author Benoît
 */
public class SalientCliffShape extends CliffShape {
    
    public SalientCliffShape(Cliff cliff, double angle, Point2D pivot){
        super(cliff, angle, pivot);
    }

    @Override
    protected void extrudeProfile() {
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
    }

    @Override
    public Type getType() {
        return Type.Salient;
    }
    
    
}
