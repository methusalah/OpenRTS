package model.battlefield.army.tacticalAI;

import model.battlefield.army.components.Unit;

/**
 *
 */
public class AttackEvent {
    protected Unit enemy;
    protected double time;
    
    public AttackEvent(Unit enemy) {
        this.enemy = enemy;
        this.time = System.currentTimeMillis();
    }
}
