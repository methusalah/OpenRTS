/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.tacticalIA;

import model.army.data.Unit;

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
