package com.jme3.bullet.collision.shapes;

import com.jme3.scene.Mesh;
import java.nio.FloatBuffer;

public class HullCollisionShape extends ConvexShape {
    public HullCollisionShape(FloatBuffer points) {}
    public HullCollisionShape(Mesh mesh) {}
    public HullCollisionShape(float... points) {}
}
