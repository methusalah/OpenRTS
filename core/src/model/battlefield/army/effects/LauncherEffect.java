package model.battlefield.army.effects;

import java.util.ArrayList;

import model.battlefield.army.components.Projectile;
import model.builders.entity.EffectBuilder;

import com.google.common.eventbus.Subscribe;

import event.EventManager;
import event.ProjectileArrivedEvent;

/**
 * That effect is created with a new projectile and subscribe to the projectiles events.
 *
 * When the projectile arrives at destination, launcher effect launches its children effects.
 *
 */
public class LauncherEffect extends Effect {
	protected final Projectile projectile;

	public LauncherEffect(Projectile projectile, ArrayList<EffectBuilder> effectBuilders, EffectSource source, EffectTarget target) {
		super(effectBuilders, source, target);
		this.projectile = projectile;
		EventManager.registerForServer(this);
	}

	@Override
	public void launch(){
	}

	@Subscribe
	public void actionPerformed(ProjectileArrivedEvent e) {
		for(EffectBuilder eb : childEffectBuilders) {
			eb.build(source, target, null).launch();
		}
	}

}
