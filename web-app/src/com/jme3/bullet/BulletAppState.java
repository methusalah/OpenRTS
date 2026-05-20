package com.jme3.bullet;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;

public class BulletAppState extends AbstractAppState {
    private final PhysicsSpace physicsSpace = new PhysicsSpace();

    public void startPhysics() {}
    public PhysicsSpace getPhysicsSpace() { return physicsSpace; }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {}

    @Override
    public void render(RenderManager rm) {}

    @Override
    public void cleanup() {}
}
