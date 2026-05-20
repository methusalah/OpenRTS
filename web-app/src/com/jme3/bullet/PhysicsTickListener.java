package com.jme3.bullet;

public interface PhysicsTickListener {
    void physicsTick(PhysicsSpace space, float timeStep);
    void prePhysicsTick(PhysicsSpace space, float timeStep);
}
