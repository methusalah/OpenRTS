/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.effects;

import geometry.Point2D;
import geometry3D.Point3D;
import model.army.data.Effect;
import model.army.data.Unit;


/**
 *
 * @author Benoît
 */
public class DamageEffect extends Effect {
    
    public DamageEffect(Unit source, Unit target, Point3D targetPoint) {
        super(source, target, targetPoint);
    }

    @Override
    public void launch(){
        target.damage(amount);
        target.ai.registerAsAttacker(source);
    }
    
    
}
