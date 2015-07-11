package model.battlefield.army.effects;

import java.util.List;

import model.builders.entity.EffectBuilder;

/**
 * Abstract class that defines the basic structure of effets.
 *
 * The role of effects is to create interaction between battlefield components.
 *
 * source and target are, for example, units, buildings, players... and implements the EffectSource
 * and EffectTarget interfaces.
 *
 * On launch, effects may have different behaviors, and may also launch children effects
 *
 */
public abstract class Effect {
	protected final List<EffectBuilder> childEffectBuilders;

	public final EffectSource source;
	public final EffectTarget target;

	public Effect(List<EffectBuilder> childEffectBuilders, EffectSource source, EffectTarget target) {
		this.childEffectBuilders = childEffectBuilders;
		this.source = source;
		this.target = target;
	}

	public abstract void launch();
}
