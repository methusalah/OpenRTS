/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.effects;

import geometry.Point2D;
import geometry3D.Point3D;
import model.army.data.Effect;
import model.army.data.EffectBuilder;
import model.army.data.Mover;
import model.army.data.Projectile;
import model.army.data.Unit;
import model.map.Map;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class LauncherEffect extends Effect {
    public LauncherEffect(Unit source, Unit target, Point3D targetPoint) {
        super(source, target, targetPoint);
    }
    
    @Override
    public void launch(){
        if(sourcePoint != null){
            projectile.mover.pos = sourcePoint;
            projectile.mover.velocity = sourceVec;
        } else
            projectile.mover.velocity = Point3D.UNIT_Z;
        
            
    }
    
    public void notifyArrival(){
        for(EffectBuilder eb : effectBuilders)
            eb.build(source, target, targetPoint).launch();
    }

}
