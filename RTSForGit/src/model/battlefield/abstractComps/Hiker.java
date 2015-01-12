/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.abstractComps;

import geometry.BoundingCircle;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import java.util.List;
import math.Angle;
import model.battlefield.army.components.Mover;
import model.battlefield.army.components.Unit;
import model.builders.MoverBuilder;

/**
 *
 * @author Benoît
 */
public abstract class Hiker extends FieldComp{
    protected final double radius;
    public final double speed;
    public final double mass;
    public final Mover mover;

    public Hiker(double radius, double speed, double mass, Point3D pos, double yaw, MoverBuilder moverBuilder) {
        super(pos, yaw);
        this.radius = radius;
        this.speed = speed;
        this.mass = mass;
        this.mover = moverBuilder.build(this);
    }
    public Hiker(double radius, double speed, double mass, Point3D pos, double yaw, Mover mover) {
        super(pos, yaw);
        this.radius = radius;
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

    public double getMass() {
        return mass;
    }
    
    public boolean hasMoved(Point3D lastPos, double lastYaw){
        return lastYaw != yaw || !lastPos.equals(pos);
    }
    
    public BoundingCircle getBounds() {
        return new BoundingCircle(new Point2D(pos), radius);
    }
    
    public double getSpacing(Hiker o) {
        return radius+o.radius;
    }
    public double getBoundsDistance(Hiker o){
        List<Hiker> l = new ArrayList<>();
        return getDistance(o)-getSpacing(o);
    }

    public boolean collide(Hiker o){
        return getBounds().collide(o.getBounds());
    }
}
