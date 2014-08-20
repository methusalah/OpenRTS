/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.effects;

import geometry3D.Point3D;
import java.util.ArrayList;
import model.army.data.Effect;
import math.MyRandom;
import model.army.data.Unit;

/**
 *
 * @author Benoît
 */
public class PersistentEffect extends Effect {
    private boolean launched = false;
    public boolean terminated = false;
    
    private int count = 0;
    private int periodIndex = 0;
    private int effectIndex = 0;
    
    private long lastPeriod;
    private double currentPeriodDuration;
    
    public PersistentEffect(Unit source, Unit target, Point3D targetPoint){
        super(source, target, targetPoint);
    }
    
    @Override
    public void launch(){
        launched = true;
        setNextPeriodDuration();
        lastPeriod = System.currentTimeMillis();
    }
    
    public void update(){
        if(!launched)
            return;
        if(source.destroyed())
            terminated = true;
        if(!terminated && lastPeriod+currentPeriodDuration < System.currentTimeMillis()){
            effectBuilders.get(effectIndex).build(source, target, targetPoint).launch();
            
            if(++effectIndex >= effectBuilders.size())
                effectIndex = 0;
            if(++periodIndex >= durations.size())
                periodIndex = 0;
            setNextPeriodDuration();
            lastPeriod = System.currentTimeMillis();

            if(++count >= periodCount)
                terminated = true;
        }
    }
    
    private void setNextPeriodDuration(){
        currentPeriodDuration = durations.get(periodIndex)+ MyRandom.between(0, ranges.get(periodIndex));
    }
    
}
