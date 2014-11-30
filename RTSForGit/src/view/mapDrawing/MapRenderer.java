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
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import math.Angle;
import model.map.cliff.Trinket;
import model.map.cliff.Cliff;
import model.map.cliff.CliffFaceMesh;
import model.map.Map;
import model.map.MapEditor;
import model.map.parcel.ParcelManager;
import model.map.Tile;
import model.map.cliff.faces.NaturalFace;
import model.map.parcel.ParcelMesh;

import tools.LogUtil;
import view.material.MaterialManager;
import view.math.Translator;

public class MapRenderer {

    Map map;
    ParcelManager parcelManager;
    MaterialManager mm;
    AssetManager am;
    MapEditor editor;
    public Node mainNode = new Node();
    public PhysicsSpace mainPhysicsSpace = new PhysicsSpace();
    private HashMap<String, Spatial> models = new HashMap<>();

    private HashMap<ParcelMesh, Spatial> parcelsSpatial = new HashMap<>();
    private HashMap<Tile, Spatial> tilesSpatial = new HashMap<>();
    public  Node gridNode = new Node();
    private Geometry gridGeom;
    private GridMesh gridMesh;
    private Node activeArea = new Node();
	
	public MapRenderer(Map map, ParcelManager parcelManager, MaterialManager mm, AssetManager am, MapEditor editor) {
            this.map = map;
            this.parcelManager = parcelManager;
            this.mm = mm;
            this.am = am;
            this.editor = editor;
            
            Geometry g = new Geometry();
            g.setMesh(new Quad(1, 1));
            Material mat = mm.getColor(ColorRGBA.Green);
            mat.getAdditionalRenderState().setWireframe(true);
            g.setMaterial(mat);
            g.setLocalTranslation(0, 0, 0.11f);
            activeArea.attachChild(g);
            mainNode.attachChild(activeArea);
	}
	
	public void renderTiles() {
		LogUtil.logger.info("rendering ground");
                gridMesh = new GridMesh(map);
                gridGeom = new Geometry();
                gridGeom.setMesh(Translator.toJMEMesh(gridMesh));
                Material mat = mm.getColor(ColorRGBA.Black);
                mat.getAdditionalRenderState().setWireframe(true);
                gridGeom.setMaterial(mat);//mm.getLightingTexture("textures/grass.tga"));
                
                gridNode.attachChild(gridGeom);
                mainNode.attachChild(gridNode);

                for(ParcelMesh mesh : parcelManager.meshes){
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
                    parcelsSpatial.put(mesh, g);
                    mainNode.attachChild(g);
                    mainPhysicsSpace.add(g);
                }

                LogUtil.logger.info("rendering cliffs");
		for(Tile t : map.getTilesWithCliff()) {
                    if(t.cliff.naturalFace == null)
                        continue;
                    if(t.cliff.manmadeFace == null){
                        Geometry g = new Geometry();
                        g.setMesh(Translator.toJMEMesh(t.cliff.naturalFace.mesh));
                        g.setMaterial(mm.getLightingTexture("textures/road.jpg"));
                        g.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

                        g.rotate(0, 0, (float)(t.cliff.angle));
                        g.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)(t.level*Tile.STAGE_HEIGHT));

                        tilesSpatial.put(t, g);
                        mainNode.attachChild(g);

                        for(Trinket a : t.cliff.naturalFace.getAssets()){
                            Spatial s = getModel(a.path);
                            s.scale(0.002f*(float)a.scale);
                            s.rotate((float)a.rotX, (float)a.rotY, (float)a.rotZ);
                            if(a.path.equals("models/env/exterior01/rockA.mesh.xml"))
                                s.setMaterial(mm.getLightingTexture("textures/road.jpg"));
                            s.setLocalTranslation(Translator.toVector3f(a.pos.getAddition(t.x, t.y, t.level*Tile.STAGE_HEIGHT)));
                            s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                            mainNode.attachChild(s);
                        }
                    } else {
                        Spatial s = null;
                        switch (t.cliff.type){
                            case Orthogonal : 
                                s = getModel("models/env/interior01/wallA.mesh.xml");
                                s.rotate(0, 0, (float) (t.cliff.angle+Angle.RIGHT));
                                break;
                            case Salient : 
                                s = getModel("models/env/interior01/wallAngle1A.mesh.xml");
                                s.rotate(0, 0, (float)(t.cliff.angle+Angle.RIGHT));
                                break;
                            case Corner : 
                                s = getModel("models/env/interior01/wallAngle2A.mesh.xml");
                                s.rotate(0, 0, (float)(t.cliff.angle));
                                break;
                        }
                        if(s == null)
                            continue;
                        s.scale(0.005f);
                        s.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)(t.level*Tile.STAGE_HEIGHT)+0.1f);
                        s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                        tilesSpatial.put(t, s);
                        mainNode.attachChild(s);
                    }
                }
	}
        
        public void update(){
            ArrayList<Tile> updated = editor.grabUpdatedTiles();
            if(!updated.isEmpty()){
                gridMesh.update();
                ((Geometry)gridNode.getChildren().get(0)).setMesh(Translator.toJMEMesh(gridMesh));
            }
            for(Tile t : updated){
                if(t.isCliff()){
                    if(t.cliff.type == Cliff.Type.Bugged){
                        Geometry g = (Geometry)tilesSpatial.get(t);
                        if(g != null)
                            mainNode.detachChild(g);
                        g = new Geometry();
                        g.setMesh(new Box(0.5f, 0.5f, 1));
                        g.setMaterial(mm.redMaterial);
                        g.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)(t.level*Tile.STAGE_HEIGHT)+1);
                        tilesSpatial.put(t, g);
                        mainNode.attachChild(g);
                    } else {
                        if(t.cliff.naturalFace == null)
                            continue;
                        Geometry g = (Geometry)tilesSpatial.get(t);
                        if(g != null)
                            mainNode.detachChild(g);
                        g = new Geometry();
                        g.setMesh(Translator.toJMEMesh(t.cliff.naturalFace.mesh));
                        g.setMaterial(mm.getLightingTexture("textures/road.jpg"));
    //                        g.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

                        g.rotate(0, 0, (float)(t.cliff.angle));
                        g.setLocalTranslation(t.x+0.5f, t.y+0.5f, (float)(t.level*Tile.STAGE_HEIGHT));

                        tilesSpatial.put(t, g);
                        mainNode.attachChild(g);
                    }
                } else {
                    Geometry g = (Geometry)tilesSpatial.get(t);
                    if(g != null){
                        mainNode.detachChild(g);
                        tilesSpatial.remove(t);
                    }
                }
            }

            for(ParcelMesh parcel : editor.grabUpdatedParcels()){
                Geometry g = (Geometry)parcelsSpatial.get(parcel);
                g.setMesh(Translator.toJMEMesh(parcel));
            }
        }
        
        private Spatial getModel(String path){
                if(!models.containsKey(path))
                    models.put(path, am.loadModel(path));
                return models.get(path).clone();
        }

    public void drawActiveArea(Point2D coord) {
        Tile t = map.getTile(coord);
        if(t == null || t.n == null || t.e == null)
            return;
        double z = t.level;
        for(Tile neib : map.get8Around(t))
            if(neib.level>z)
                z = neib.level;
        activeArea.setLocalTranslation(t.x, t.y, (float)(z*Tile.STAGE_HEIGHT));
    }
    
    public void toggleGrid(){
        if(mainNode.hasChild(gridNode))
            mainNode.detachChild(gridNode);
        else
            mainNode.attachChild(gridNode);
            
    }
}
