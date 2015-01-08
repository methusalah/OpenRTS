/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.effects;

import model.builders.EffectBuilder;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;

/**
 *
 * @author Benoît
 */
public abstract class Effect {
    // final
    protected final String type;
    protected final int amount;
    protected final int periodCount;
    protected final ArrayList<Double> durations;
    protected final ArrayList<Double> ranges;
    protected final Projectile projectile;
    protected final ArrayList<EffectBuilder> effectBuilders;

    
    public Unit source;
    public Point3D sourcePoint;
    public Point3D sourceVec;
    public Unit target;
    public Point3D targetPoint;

    public Effect(String type,
            int amount,
            int periodCount,
            ArrayList<Double> durations,
            ArrayList<Double> ranges,
            Projectile projectile,
            ArrayList<EffectBuilder> effectBuilders,
            Unit source,
            Unit target,
            Point3D targetPoint) {
        this.type = type;
        this.amount = amount;
        this.periodCount = periodCount;
        this.durations = durations;
        this.ranges = ranges;
        this.projectile = projectile;
        this.effectBuilders = effectBuilders;

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
