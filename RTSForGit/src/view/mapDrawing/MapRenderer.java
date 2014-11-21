package view.mapDrawing;

import geometry.Point2D;
import geometry.Polygon;
import geometry3D.PolygonExtruder;


import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import collections.PointRing;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Mesh;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import java.util.Queue;

import math.Angle;
import model.map.Cliff;
import model.map.CliffMesh;
import model.map.Map;
import model.map.Tile;
import model.map.TerrainMesh;

import tools.LogUtil;
import view.material.MaterialManager;
import view.math.Translator;

public class MapRenderer {

    Map map;
    MaterialManager mm;
    public Node mainNode = new Node();
    public PhysicsSpace mainPhysicsSpace = new PhysicsSpace();
    private Spatial cliff;
    private Spatial cliffAcute;
    private Spatial cliffObtuse;
	
	public MapRenderer(Map map, MaterialManager mm, AssetManager am) {
            this.map = map;
            this.mm = mm;

            cliff = am.loadModel("models/cliff_01.mesh.xml");
            cliff.scale(0.005f, 0.005f, 0.005f);

            cliffObtuse = am.loadModel("models/cliff_angleA_01.mesh.xml");
            cliffObtuse.scale(0.005f, 0.005f, 0.005f);

            cliffAcute = am.loadModel("models/cliff_angleB_01.mesh.xml");
            cliffAcute.scale(0.005f, 0.005f, 0.005f);
	}
	
	public void renderTiles() {
		LogUtil.logger.info("rendering tiles");
                
		Node shadowCaster = new Node();
		Node shadowReceiver = new Node();
                TerrainMesh tm = new TerrainMesh();
		for(Tile t : map.getTiles()) {
                    tm.add(t);
                    if(t.isCliff()){
                        Cliff c = (Cliff)t;
                        CliffMesh mesh = new CliffMesh(c);
                        Geometry g = new Geometry("cliff");
                        g.setMesh(Translator.toJMEMesh(mesh));
                        g.setMaterial(mm.getLightingTexture("textures/road.jpg"));
                        g.setShadowMode(RenderQueue.ShadowMode.Receive);

                        g.rotate(0, 0, (float) (c.angle));
                        g.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)t.level*2);
                        
                        mainNode.attachChild(g);
                        
//                            if(c.ortho) {
//                                    Spatial s = cliff.clone();
//                                    s.rotate(0, 0, (float) (c.angle+Angle.RIGHT));
//                                    s.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)t.level*2);
//                                    shadowCaster.attachChild(s);
//                                    s.addControl(new RigidBodyControl(0));
//                                    mainPhysicsSpace.add(s);
//                                    continue;
//                            } else if(c.obtuseDiag){
//                                    Spatial s = cliffObtuse.clone();
//                                    s.rotate(0, 0, (float) (c.angle-Angle.RIGHT));
//                                    s.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)t.level*2);
//                                    shadowCaster.attachChild(s);
//                                    s.addControl(new RigidBodyControl(0));
//                                    mainPhysicsSpace.add(s);
//                                    continue;
//                            } else {
//                                    Spatial s = cliffAcute.clone();
//                                    s.rotate(0, 0, (float) (c.angle+Angle.RIGHT));
//                                    s.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)t.level*2);
//                                    shadowCaster.attachChild(s);
//                                    s.addControl(new RigidBodyControl(0));
//                                    mainPhysicsSpace.add(s);
//                                    continue;
//                            }
                    }
		}
                tm.compute();
                Geometry g = new Geometry();
                Mesh mesh = Translator.toJMEMesh(tm);
//                TangentBinormalGenerator.generate(mesh);
		g.setMesh(mesh);
//		g.setMaterial(mm.getLightingTexture("textures/grass.tga"));
		g.setMaterial(mm.getTerrain("textures/alphamap.png",
                        "textures/grass.tga",
                        "textures/road.jpg",
                        "textures/road.jpg",
                        "textures/road_normal.png"));
                g.setShadowMode(RenderQueue.ShadowMode.Receive);
                g.addControl(new RigidBodyControl(0));
		mainNode.attachChild(g);
                mainPhysicsSpace.add(g);

		mainNode.attachChild(GeometryBatchFactory.optimize(shadowCaster));
		mainNode.attachChild(GeometryBatchFactory.optimize(shadowReceiver));
                
                for(Spatial s : shadowCaster.getChildren())
                    s.setShadowMode(RenderQueue.ShadowMode.Cast);

                for(Spatial s : shadowReceiver.getChildren())
                    s.setShadowMode(RenderQueue.ShadowMode.Receive);
	}
}
