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
    protected final int amount;

    public DamageEffect(int amount, ArrayList<EffectBuilder> effectBuilders, EffectSource source, EffectTarget target) {
        super(effectBuilders, source, target);
        this.amount = amount;
    }
    
    @Override
    public void launch(){
        target.damage(source, amount);
    }
    
    
}
