package com.jme3.bullet.objects;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;

public class PhysicsRigidBody extends PhysicsCollisionObject {
    public PhysicsRigidBody(CollisionShape shape, float mass) {}
    public void setPhysicsLocation(Vector3f location) {}
    public Vector3f getPhysicsLocation(Vector3f store) { return Vector3f.ZERO.clone(); }
    public void setPhysicsRotation(Quaternion rotation) {}
    public float getMass() { return 0f; }
    public boolean isActive() { return false; }
    public void setLinearVelocity(Vector3f velocity) {}
    public Vector3f getLinearVelocity(Vector3f store) { return Vector3f.ZERO.clone(); }
}
