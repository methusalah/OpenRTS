package com.jme3.bullet.collision;

import com.jme3.bullet.collision.shapes.CollisionShape;

public abstract class PhysicsCollisionObject {
    protected CollisionShape collisionShape;
    public CollisionShape getCollisionShape() { return collisionShape; }
    public void setCollisionShape(CollisionShape shape) { this.collisionShape = shape; }
}
