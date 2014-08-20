/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;

/**
 *
 * @author Benoît
 */
public abstract class Effect {
    // final
    protected String type;
    protected int amount;
    protected int periodCount;
    protected ArrayList<Double> durations = new ArrayList<>();
    protected ArrayList<Double> ranges = new ArrayList<>();
    protected Projectile projectile;
    protected ArrayList<EffectBuilder> effectBuilders = new ArrayList<>();

    
    protected Unit source;
    protected Point3D sourcePoint;
    protected Point3D sourceVec;
    protected Unit target;
    protected Point3D targetPoint;
    

    public Effect(Unit source, Unit target, Point3D targetPoint) {
        this.source = source;
        this.target = target;
        this.targetPoint = targetPoint;
    }
    
    public abstract void launch();
    
    public void setSourcePoint(Point3D source, Point3D vec){
        sourcePoint = source;
        sourceVec = vec;
    }
}
