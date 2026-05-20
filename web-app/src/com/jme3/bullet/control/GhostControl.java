package com.jme3.bullet.control;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class GhostControl extends AbstractControl {
    public GhostControl(CollisionShape shape) {}

    @Override
    protected void controlUpdate(float tpf) {}

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
