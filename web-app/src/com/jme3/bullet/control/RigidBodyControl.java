package com.jme3.bullet.control;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class RigidBodyControl extends AbstractControl {
    private final PhysicsRigidBody body;

    public RigidBodyControl(CollisionShape shape, float mass) {
        this.body = new PhysicsRigidBody(shape, mass);
    }

    public RigidBodyControl(CollisionShape shape) {
        this(shape, 0f);
    }

    public RigidBodyControl(float mass) {
        this(null, mass);
    }

    public boolean isActive() { return false; }
    public void setPhysicsLocation(Vector3f loc) {}
    public Vector3f getPhysicsLocation() { return Vector3f.ZERO.clone(); }
    public PhysicsRigidBody getBody() { return body; }

    @Override
    protected void controlUpdate(float tpf) {}

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
