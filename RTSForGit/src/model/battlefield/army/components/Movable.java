/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.components;

import geometry.Point2D;
import geometry3D.Point3D;
import math.Angle;
import model.builders.MoverBuilder;

/**
 *
 * @author Benoît
 */
public abstract class Movable {
    final double radius;
    final double separationRadius;
    final public double speed;
    final public double mass;
    public final Mover mover;

    public Movable(double radius, double separationRadius, double speed, double mass, Point3D pos, double yaw, MoverBuilder moverBuilder) {
        this.radius = radius;
        this.separationRadius = separationRadius;
        this.speed = speed;
        this.mass = mass;
        this.mover = moverBuilder.build(this, pos, yaw);
    }
    public Movable(double radius, double separationRadius, double speed, double mass, Point3D pos, Mover mover) {
        this.radius = radius;
        this.separationRadius = separationRadius;
        this.speed = speed;
        this.mass = mass;
        this.mover = new Mover(mover, this);
    }
    
    
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
    
    public double getOrientation(){
        return mover.yaw;
    }

    public abstract Point3D getPos();
}
