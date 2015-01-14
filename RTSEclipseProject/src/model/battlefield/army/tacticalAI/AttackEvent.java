/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.tacticalAI;

import model.battlefield.army.components.Unit;

/**
 *
 * @author Benoît
 */
public class AttackEvent {
    protected Unit enemy;
    protected double time;
    
    public AttackEvent(Unit enemy) {
        this.enemy = enemy;
        this.time = System.currentTimeMillis();
    }
}
