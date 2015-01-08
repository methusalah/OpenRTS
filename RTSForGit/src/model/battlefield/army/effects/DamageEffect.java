/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.effects;

import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import model.builders.EffectBuilder;


/**
 *
 * @author Benoît
 */
public class DamageEffect extends Effect {

    public DamageEffect(String type, int amount, int periodCount, ArrayList<Double> durations, ArrayList<Double> ranges, Projectile projectile, ArrayList<EffectBuilder> effectBuilders, Unit source, Unit target, Point3D targetPoint) {
        super(type, amount, periodCount, durations, ranges, projectile, effectBuilders, source, target, targetPoint);
    }
    
    @Override
    public void launch(){
        target.damage(amount);
        target.ai.registerAsAttacker(source);
    }
    
    
}
