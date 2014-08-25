package view.renderers;

import geometry.Point2D;
import geometry.Polygon;
import geometry3D.PolygonExtruder;


import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import collections.PointRing;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.texture.Texture;

import math.Angle;
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
                Point2D start = new Point2D(45, 15);
                Point2D end = new Point2D(50, 35);
                map.meetObstacle2(start, end);
                for(Tile t : map.traversed){
                    Geometry g = new Geometry();
                    g.setMesh(new Box(0.45f, 0.45f, 0.45f));
                    g.setMaterial(mm.greenMaterial);
                    g.setLocalTranslation(t.x+0.5f, t.y+0.5f, t.level*2f);
                    mainNode.attachChild(g);
                }
                Geometry g1 = new Geometry();
                g1.setMesh(new Line(Translator.toVector3f(start, 2.2), Translator.toVector3f(end, 2.2)));
                g1.setMaterial(mm.redMaterial);
                mainNode.attachChild(g1);
                
                
                
		Node shadowCaster = new Node();
		Node shadowReceiver = new Node();
                TerrainMesh tm = new TerrainMesh();
		for(Tile t : map.getTiles()) {
                    tm.add(t);
                    if(t.isCliff()){
                            if(t.cliff.ortho) {
                                    Spatial s = cliff.clone();
                                    s.rotate(0, 0, (float) (t.cliff.angle+Angle.RIGHT));
                                    s.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)t.level*2);
                                    shadowCaster.attachChild(s);
                                    continue;
                            } else if(t.cliff.obtuseDiag){
                                    Spatial s = cliffObtuse.clone();
                                    s.rotate(0, 0, (float) (t.cliff.angle-Angle.RIGHT));
                                    s.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)t.level*2);
                                    shadowCaster.attachChild(s);
                                    continue;
                            } else {
                                    Spatial s = cliffAcute.clone();
                                    s.rotate(0, 0, (float) (t.cliff.angle+Angle.RIGHT));
                                    s.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)t.level*2);
                                    shadowCaster.attachChild(s);
                                    continue;
                            }
                    }
		}
                tm.compute();
                Geometry g = new Geometry();
		g.setMesh(Translator.toJMEMesh(tm));
//		g.setMaterial(mm.getLightingTexture("textures/grass.tga"));
		g.setMaterial(mm.getTerrain("textures/alphamap.png",
                        "textures/grass.tga",
                        "textures/road.jpg",
                        "textures/dirt.jpg",
                        "textures/road_normal.png"));
                g.setShadowMode(RenderQueue.ShadowMode.Receive);
		mainNode.attachChild(g);

		mainNode.attachChild(GeometryBatchFactory.optimize(shadowCaster));
		mainNode.attachChild(GeometryBatchFactory.optimize(shadowReceiver));
                
                for(Spatial s : shadowCaster.getChildren())
                    s.setShadowMode(RenderQueue.ShadowMode.Cast);

                for(Spatial s : shadowReceiver.getChildren())
                    s.setShadowMode(RenderQueue.ShadowMode.Receive);
	}
}
