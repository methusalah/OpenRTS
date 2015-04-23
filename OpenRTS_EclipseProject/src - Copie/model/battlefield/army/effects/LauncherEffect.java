package model.battlefield.army.effects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import model.battlefield.army.components.Projectile;
import model.builders.EffectBuilder;

/**
 * That effect is created with a new projectile and subscribe to the projectiles events.
 * 
 * When the projectile arrives at destination, launcher effect launches its children effects.
 *
 */
public class LauncherEffect extends Effect implements ActionListener {
    protected final Projectile projectile;

    public LauncherEffect(Projectile projectile, ArrayList<EffectBuilder> effectBuilders, EffectSource source, EffectTarget target) {
        super(effectBuilders, source, target);
        this.projectile = projectile;
        projectile.addListener(this);
    }
    
    @Override
    public void launch(){
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        for(EffectBuilder eb : childEffectBuilders)
            eb.build(source, target, null).launch();
    }

}
