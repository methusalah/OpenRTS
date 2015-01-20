/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.abstractComps;

import geometry.BoundingCircle;
import geometry.Point2D;
import geometry3D.Point3D;

import java.awt.Color;

/**
 *
 * @author Benoît
 */
public class FieldComp {
    public String modelPath = "";
    public Point3D pos;
    public Point3D direction;
    public Point3D upDirection = Point3D.UNIT_Z;
    protected final double radius;
    public double scaleX = 1;
    public double scaleY = 1;
    public double scaleZ = 1;
    public Color color;
    public double roll = 0;
    public double pitch = 0;
    public double yaw = 0;
    public final String label = "label"+this.toString();


    public FieldComp(Point3D pos, double yaw, double radius) {
        this.pos = pos;
        this.yaw = yaw;
        this.radius = radius;
    }
    
    
    public Point2D getCoord(){
        return pos.get2D();
    }
    public Point3D getPos(){
        return pos;
    }
    
    public double getYaw(){
        return yaw;
    }

    public double getRadius() {
        return radius;
    }

    public double getDistance(FieldComp o) {
        return pos.getDistance(o.pos);
    }
    
    public Point3D getVectorTo(FieldComp o) {
        return o.pos.getSubtraction(pos);
    }

    public FieldComp getNearest(FieldComp o1, FieldComp o2){
        if(getDistance(o1) < getDistance(o2))
            return o1;
        else
            return o2;
    }
    
    public BoundingCircle getBounds() {
        return new BoundingCircle(new Point2D(pos), radius);
    }
    
    public double getSpacing(FieldComp o) {
        return radius+o.radius;
    }
    public double getBoundsDistance(FieldComp o){
        return getDistance(o)-getSpacing(o);
    }

    public boolean collide(FieldComp o){
        return getBounds().collide(o.getBounds());
    }
    
    
}
