/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.effects;

import java.util.ArrayList;
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
