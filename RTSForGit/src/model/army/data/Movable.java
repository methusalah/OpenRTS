/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry3D.Point3D;
import math.Angle;

/**
 *
 * @author Benoît
 */
public abstract class Movable {
    double radius;
    double separationRadius;
    double speed;
    double mass;
    public Mover mover;
    
    public double getSpeed() {
        return speed;
    }

    public double getRotSpeed() {
        return Angle.toRadians(720);
    }

    public double getStationaryRotSpeed() {
        return Angle.toRadians(360);
    }

    public double getRadius() {
        return radius;
    }

    public double getSeparationRadius() {
        return separationRadius;
    }

    public double getMass() {
        return mass;
    }
    
    public void setStuck() {
    }
    
    public double getOrientation(){
        return mover.orientation;
    }

    public abstract Point3D getPos();
}
