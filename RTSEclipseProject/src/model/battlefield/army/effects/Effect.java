/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.effects;

import model.builders.EffectBuilder;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;

/**
 *
 * @author Benoît
 */
public abstract class Effect {
    protected final ArrayList<EffectBuilder> childEffectBuilders;
    
    public final EffectSource source;
    public final EffectTarget target;

    public Effect(ArrayList<EffectBuilder> childEffectBuilders, EffectSource source, EffectTarget target) {
        this.childEffectBuilders = childEffectBuilders;
        this.source = source;
        this.target = target;
    }
    
    public abstract void launch();
}
