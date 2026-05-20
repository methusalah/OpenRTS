package com.jme3.bullet.collision.shapes.infos;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;

public class ChildCollisionShape {
    public CollisionShape getShape() { return null; }
    public Vector3f getOffset(Vector3f store) { return Vector3f.ZERO.clone(); }
}
