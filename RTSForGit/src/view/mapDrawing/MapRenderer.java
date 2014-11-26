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
import java.util.HashMap;
import java.util.Queue;

import math.Angle;
import model.map.Asset;
import model.map.cliff.Cliff;
import model.map.cliff.CliffFaceMesh;
import model.map.Map;
import model.map.parcel.ParcelManager;
import model.map.Tile;
import model.map.cliff.faces.NaturalFace;
import model.map.parcel.ParcelMesh;

import tools.LogUtil;
import view.material.MaterialManager;
import view.math.Translator;

public class MapRenderer {

    Map map;
    MaterialManager mm;
    AssetManager am;
    public Node mainNode = new Node();
    public PhysicsSpace mainPhysicsSpace = new PhysicsSpace();
    private HashMap<String, Spatial> models = new HashMap<>();
	
	public MapRenderer(Map map, MaterialManager mm, AssetManager am) {
            this.map = map;
            this.mm = mm;
            this.am = am;
	}
	
	public void renderTiles() {
		LogUtil.logger.info("rendering tiles");
                
		Node shadowCaster = new Node();
		Node shadowReceiver = new Node();
                ParcelManager pm = new ParcelManager(map);
                
                for(ParcelMesh mesh : pm.meshes){
                    Geometry g = new Geometry();
                    Mesh jmeMesh = Translator.toJMEMesh(mesh);
    //                TangentBinormalGenerator.generate(mesh);
                    g.setMesh(jmeMesh);
                    g.setMaterial(mm.getTerrain("textures/alphamap.png",
                            "textures/grass.tga",
                            "textures/road.jpg",
                            "textures/road.jpg",
                            "textures/road_normal.png"));
                    g.setShadowMode(RenderQueue.ShadowMode.Receive);
                    g.addControl(new RigidBodyControl(0));
                    mainNode.attachChild(g);
                    mainPhysicsSpace.add(g);
                }

                
		for(Tile t : map.getTiles()) {
                    if(t.isCliff()){
                        Cliff c = (Cliff)t;
                        if(c.naturalFace == null)
                            continue;
                        if(c.naturalFace.isNatural()){
                            CliffFaceMesh mesh = new CliffFaceMesh(c.naturalFace);
                            Geometry g = new Geometry("cliff");
                            g.setMesh(Translator.toJMEMesh(mesh));
                            g.setMaterial(mm.getLightingTexture("textures/road.jpg"));
                            g.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

                            g.rotate(0, 0, (float)(c.naturalFace.angle));
                            g.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)(t.level*Cliff.STAGE_HEIGHT));

                            mainNode.attachChild(g);

                            for(Asset a : c.naturalFace.getAssets()){
                                Spatial s = getModel(a.path);
                                s.scale(0.002f*(float)a.scale);
                                s.rotate((float)a.rotX, (float)a.rotY, (float)a.rotZ);
                                if(a.path.equals("models/env/exterior01/rockA.mesh.xml"))
                                    s.setMaterial(mm.getLightingTexture("textures/road.jpg"));
                                s.setLocalTranslation(Translator.toVector3f(a.pos.getAddition(c.x, c.y, c.level*Cliff.STAGE_HEIGHT)));
                                s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                                mainNode.attachChild(s);
                            }
                        } else {
                            Spatial s = null;
                            switch (c.type){
                                case Orthogonal : 
                                    s = getModel("models/env/interior01/wallA.mesh.xml");
                                    s.rotate(0, 0, (float) (c.naturalFace.angle+Angle.RIGHT));
                                    break;
                                case Salient : 
                                    s = getModel("models/env/interior01/wallAngle1A.mesh.xml");
                                    s.rotate(0, 0, (float)(c.naturalFace.angle+Angle.RIGHT));
                                    break;
                                case Corner : 
                                    s = getModel("models/env/interior01/wallAngle2A.mesh.xml");
                                    s.rotate(0, 0, (float)(c.naturalFace.angle));
                                    break;
                            }
                            if(s == null)
                                continue;
                            s.scale(0.005f);
                            s.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)(c.level*Cliff.STAGE_HEIGHT)+0.1f);
                            s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                            mainNode.attachChild(s);
                        }
                    }
		}

		mainNode.attachChild(GeometryBatchFactory.optimize(shadowCaster));
		mainNode.attachChild(GeometryBatchFactory.optimize(shadowReceiver));
                
                for(Spatial s : shadowCaster.getChildren())
                    s.setShadowMode(RenderQueue.ShadowMode.Cast);

                for(Spatial s : shadowReceiver.getChildren())
                    s.setShadowMode(RenderQueue.ShadowMode.Receive);
	}
        
        private Spatial getModel(String path){
                if(!models.containsKey(path))
                    models.put(path, am.loadModel(path));
                return models.get(path).clone();
        }
}
