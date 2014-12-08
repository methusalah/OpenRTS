/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.lighting;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import math.Angle;
import model.map.Tile;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class SunLight {
    public DirectionalLighting sun;
    public DirectionalLighting shadowCaster;
    public AmbientLighting ambient;
    int confIndex = 0;
    
    double delay = 5;
    double lastAction = 0;
    double angleIncrement = Angle.toRadians(1);
    double intensityIncrement = 0.02;
    int colorIncrement = 1;
    
    ArrayList<Lighting> actualLights = new ArrayList<>();

    ArrayList<ActionListener> listeners = new ArrayList<>();

    public SunLight() {
        double yaw = Angle.toRadians(15);
        double pitch = Angle.toRadians(40);
        double intensity = 2;
        sun = new DirectionalLighting(Color.WHITE, yaw, pitch,  intensity);
        
        shadowCaster = new DirectionalLighting(Color.WHITE, yaw, pitch, 0);

        ambient = new AmbientLighting(Color.WHITE);
        
        actualLights.add(sun);
        actualLights.add(shadowCaster);
    }
    
    public void toggleLight(){
        actualLights.clear();
        switch(confIndex){
            case 0 : 
                actualLights.add(sun);
                confIndex = 1;
                LogUtil.logger.info("Light switch to sun.");
                break;
            case 1 : 
                actualLights.add(shadowCaster);
                confIndex = 2;
                LogUtil.logger.info("Light switch to shadow caster.");
                break;
            case 2 : 
                actualLights.add(sun);
                actualLights.add(shadowCaster);
                confIndex = 3;
                LogUtil.logger.info("Light switch to both sun and shadow caster.");
                break;
            case 3 : 
                actualLights.add(ambient);
                confIndex = 0;
                LogUtil.logger.info("Light switch to ambiant.");
                break;
        }
    }
    
    public void incDayTime(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                if(l instanceof DirectionalLighting)
                    ((DirectionalLighting)l).changePitch(angleIncrement);
        }
        notifyListeners();
    }
    public void decDayTime(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                if(l instanceof DirectionalLighting)
                    ((DirectionalLighting)l).changePitch(-angleIncrement);
        }
        notifyListeners();
    }
    
    
    public void turnCompassEast(){
        boolean toUpdate = false;
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                if(l instanceof DirectionalLighting)
                    ((DirectionalLighting)l).changeYaw(angleIncrement);
        }
        notifyListeners();
    }
    public void turnCompasWest(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                if(l instanceof DirectionalLighting)
                    ((DirectionalLighting)l).changeYaw(-angleIncrement);
        }
        notifyListeners();
    }

    
    public void incIntensity(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                l.intensity += intensityIncrement;
        }
        notifyListeners();
    }
    public void decIntensity(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                l.intensity -= intensityIncrement;
        }
        notifyListeners();
    }
    
    public void decRed(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                if(l.color.getRed()-colorIncrement>=0)
                    l.color = new Color(l.color.getRed()-colorIncrement, l.color.getGreen(), l.color.getBlue());
        }
        notifyListeners();
    }
    public void decGreen(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                if(l.color.getGreen()-colorIncrement>=0)
                    l.color = new Color(l.color.getRed(), l.color.getGreen()-colorIncrement, l.color.getBlue());
        }
        notifyListeners();
    }
    public void decBlue(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                if(l.color.getBlue()-colorIncrement>=0)
                    l.color = new Color(l.color.getRed(), l.color.getGreen(), l.color.getBlue()-colorIncrement);
        }
        notifyListeners();
    }
    public void resetColor(){
        if(lastAction+delay<System.currentTimeMillis()){
            lastAction = System.currentTimeMillis();
            for(Lighting l : actualLights)
                l.color = Color.WHITE;
        }
        notifyListeners();
    }
    
    
    public void toggleSpeed(){
        if(delay == 50){
            delay = 5;
            LogUtil.logger.info("High speed set.");
        } else {
            delay = 50;
            LogUtil.logger.info("low speed set.");
        }
    }
    
    private void notifyListeners(){
        ActionEvent event = new ActionEvent(this, 0, "light");
        for(ActionListener l : listeners)
            l.actionPerformed(event);
    }
    
    public void addListener(ActionListener l) {
        listeners.add(l);
    }
    
}
