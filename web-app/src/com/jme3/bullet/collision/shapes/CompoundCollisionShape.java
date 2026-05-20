package com.jme3.bullet.collision.shapes;

import com.jme3.math.Vector3f;

public class CompoundCollisionShape extends CollisionShape {
    public void addChildShape(CollisionShape shape, Vector3f offset) {}
    public void addChildShape(CollisionShape shape) {}
    public java.util.List<com.jme3.bullet.collision.shapes.infos.ChildCollisionShape> getChildren() {
        return new java.util.ArrayList<>();
    }
}
