package model.battlefield.army.effects;

import java.util.List;

import model.builders.entity.EffectBuilder;


/**
 * Simple damage effect
 *
 */
public class DamageEffect extends Effect {
	protected final int amount;

	public DamageEffect(int amount, List<EffectBuilder> effectBuilders, EffectSource source, EffectTarget target) {
		super(effectBuilders, source, target);
		this.amount = amount;
	}

	@Override
	public void launch(){
		target.damage(source, amount);
	}


}
