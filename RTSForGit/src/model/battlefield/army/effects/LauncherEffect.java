/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.effects;

import geometry.Point2D;
import geometry3D.Point3D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import model.builders.EffectBuilder;
import model.battlefield.army.components.Mover;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import model.battlefield.map.Map;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class LauncherEffect extends Effect implements ActionListener {

    public LauncherEffect(String type, 
            int amount, 
            int periodCount, 
            ArrayList<Double> durations, 
            ArrayList<Double> ranges, 
            Projectile projectile, 
            ArrayList<EffectBuilder> effectBuilders, 
            Unit source, 
            Unit target, 
            Point3D targetPoint) {
        super(type, amount, periodCount, durations, ranges, projectile, effectBuilders, source, target, targetPoint);
        projectile.addListener(this);
    }
    
    @Override
    public void launch(){
        if(sourcePoint != null){
            projectile.mover.pos = sourcePoint;
            projectile.mover.velocity = sourceVec;
        } else
            projectile.mover.velocity = Point3D.UNIT_Z;
        
            
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        for(EffectBuilder eb : effectBuilders)
            eb.build(source, target, targetPoint).launch();
    }

}
