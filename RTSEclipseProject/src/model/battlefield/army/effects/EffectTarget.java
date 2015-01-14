/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.effects;

import geometry3D.Point3D;

/**
 *
 * @author Benoît
 */
public interface EffectTarget {
    
    public void damage(EffectSource source, int amount);
    public double getRadius();
    public Point3D getPos();
}
