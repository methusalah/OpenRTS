package com.jme3.bullet;

import com.jme3.math.Vector3f;
import com.jme3.bullet.objects.PhysicsRigidBody;
import java.util.ArrayList;
import java.util.Collection;

public class PhysicsSpace {
    public enum BroadphaseType { DBVT, AXIS_SWEEP_3, AXIS_SWEEP_3_32 }

    public PhysicsSpace() {}
    public PhysicsSpace(BroadphaseType type) {}

    public void setGravity(Vector3f gravity) {}
    public Vector3f getGravity(Vector3f store) { return Vector3f.ZERO.clone(); }
    public void add(Object obj) {}
    public void remove(Object obj) {}
    public void addTickListener(PhysicsTickListener listener) {}
    public void removeTickListener(PhysicsTickListener listener) {}
    public Collection<PhysicsRigidBody> getRigidBodyList() { return new ArrayList<>(); }
    public void update(float timeInterval) {}
    public void distributeEvents() {}
}
