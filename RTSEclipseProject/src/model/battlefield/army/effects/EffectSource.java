/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.effects;

import geometry3D.Point3D;
import model.battlefield.army.components.Unit;

/**
 *
 * @author Benoît
 */
public interface EffectSource {
    
    public boolean isStillActiveSource();
    public Point3D getPos();
    public Point3D getDirection();
    public Unit getUnit();
    public double getYaw();
    
}
