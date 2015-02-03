package model.battlefield.army.effects;

import java.util.ArrayList;

import model.builders.EffectBuilder;

public class ImpactEffect extends Effect {

	public ImpactEffect(ArrayList<EffectBuilder> childEffectBuilders, EffectSource source, EffectTarget target) {
		super(childEffectBuilders, source, target);
	}
	
	
	@Override
	public void launch() {
	}

}
