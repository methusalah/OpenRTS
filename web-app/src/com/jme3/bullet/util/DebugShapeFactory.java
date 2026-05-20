package com.jme3.bullet.util;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

public class DebugShapeFactory {
    public static Mesh getDebugMesh(CollisionShape shape) { return new Mesh(); }
    public static Node getDebugShape(CollisionShape shape) { return new Node(); }
}
