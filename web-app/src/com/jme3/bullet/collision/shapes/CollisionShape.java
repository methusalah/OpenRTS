package com.jme3.bullet.collision.shapes;

public abstract class CollisionShape {
    public void setScale(com.jme3.math.Vector3f scale) {}
    public com.jme3.math.Vector3f getScale() { return com.jme3.math.Vector3f.UNIT_XYZ.clone(); }
}
