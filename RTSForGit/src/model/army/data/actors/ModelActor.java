/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.actors;

import model.army.data.Actor;

/**
 *
 * @author Benoît
 */
public class ModelActor extends Actor {
    public String modelPath;
    public double scale;
    
    public ModelActor(String trigger, Actor parent){
        super(trigger, parent);
    }
    
    public String getLabel(){
        return "";
    }
}
