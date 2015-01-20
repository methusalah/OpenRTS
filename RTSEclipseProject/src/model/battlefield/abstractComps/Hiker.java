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
    public final double speed;
    public final double mass;
    public final Mover mover;

    public Hiker(double radius, double speed, double mass, Point3D pos, double yaw, MoverBuilder moverBuilder) {
        super(pos, yaw, radius);
        this.speed = speed;
        this.mass = mass;
        this.mover = moverBuilder.build(this);
        mover.desiredYaw = yaw;
    }
    public Hiker(double radius, double speed, double mass, Point3D pos, double yaw, Mover mover) {
        super(pos, yaw, radius);
        this.speed = speed;
        this.mass = mass;
        this.mover = new Mover(mover, this);
        mover.desiredYaw = yaw;
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

    public double getMass() {
        return mass;
    }
    
    public boolean hasMoved(Point3D lastPos, double lastYaw){
        return lastYaw != yaw || !lastPos.equals(pos);
    }
    
}
