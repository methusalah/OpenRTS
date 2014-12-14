package view;

import view.material.MaterialManager;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import geometry.Point2D;
import model.Model;
import view.actorDrawing.ActorDrawingManager;
import view.mapDrawing.EditorRenderer;
import view.mapDrawing.LightDrawer;
import view.mapDrawing.MapRenderer;

public class View {

    // External ressources
    Model model;
    public Node rootNode;
    public Node guiNode = new Node();
    public PhysicsSpace physicsSpace;
        
    // Renderers
    public MapRenderer mapRend;
    public EditorRenderer editorRend;
    public ActorDrawingManager actorManager;
    public LightDrawer lightDrawer;
//    public UnitRenderer unitsRend;


    // Internal ressources
    public MaterialManager mm;
    public ViewPort vp;
    public AssetManager am;
    public Pointer pointer;

    public View(Node rootNode, Node gui, PhysicsSpace physicsSpace, AssetManager am, ViewPort vp, Model m){
        model = m;
        this.rootNode = rootNode;
        this.physicsSpace = physicsSpace;
        gui.attachChild(guiNode);

        mm = new MaterialManager(am);
        this.am = am;
        this.vp = vp;
        pointer = new Pointer();
        
        lightDrawer = new LightDrawer(m.sunLight, am, rootNode, vp);
        model.sunLight.addListener(lightDrawer);

        mapRend = new MapRenderer(model.map, model.parcelManager, mm, am);
        rootNode.attachChild(mapRend.mainNode);
        mapRend.mainPhysicsSpace = physicsSpace;
        model.toolManager.addListener(mapRend);
        
        editorRend = new EditorRenderer(model.map, model.toolManager, mm);
        rootNode.attachChild(editorRend.mainNode);
        model.toolManager.addListener(editorRend);
        
        
        actorManager = new ActorDrawingManager(am, mm, model.armyManager);
        rootNode.attachChild(actorManager.mainNode);
        actorManager.mainPhysicsSpace = physicsSpace;
        
        createLight();
        createSky();
    }
    
    public void reset(){
        rootNode.detachChild(mapRend.mainNode);
        rootNode.detachChild(editorRend.mainNode);
        rootNode.detachChild(actorManager.mainNode);
        
        mapRend = new MapRenderer(model.map, model.parcelManager, mm, am);
        rootNode.attachChild(mapRend.mainNode);
        mapRend.mainPhysicsSpace = physicsSpace;
        model.toolManager.addListener(mapRend);
        mapRend.renderTiles();
        
        editorRend = new EditorRenderer(model.map, model.toolManager, mm);
        rootNode.attachChild(editorRend.mainNode);
        model.toolManager.addListener(editorRend);
        
        
        actorManager = new ActorDrawingManager(am, mm, model.armyManager);
        rootNode.attachChild(actorManager.mainNode);
        actorManager.mainPhysicsSpace = physicsSpace;
        
    }
	
    private void createSky() {
        vp.setBackgroundColor(new ColorRGBA(135f/255f, 206f/255f, 250f/255f, 1));
        Geometry xAxe = new Geometry();
        xAxe.setMesh(new Box(5, 0.1f, 0.1f));
        xAxe.setMaterial(mm.getColor(ColorRGBA.Brown));
        xAxe.setLocalTranslation(5, 0, 0);
        rootNode.attachChild(xAxe);

        Geometry zAxe = new Geometry();
        zAxe.setMesh(new Box(0.1f, 0.1f, 5));
        zAxe.setMaterial(mm.greenMaterial);
        zAxe.setLocalTranslation(0, 0, 5);
        rootNode.attachChild(zAxe);

        Geometry yAxe = new Geometry();
        yAxe.setMesh(new Box(0.1f, 5, 0.1f));
        yAxe.setMaterial(mm.redMaterial);
        yAxe.setLocalTranslation(0, 5, 0);
        rootNode.attachChild(yAxe);
    }
    
    public DirectionalLight sunComp1;
    public DirectionalLight sunComp2;
    private void createLight() {
    	
    }

    public void drawSelectionArea(Point2D c1, Point2D c2) {
        float minX = (float) Math.min(c1.x, c2.x);
        float maxX = (float) Math.max(c1.x, c2.x);

        float minY = (float) Math.min(c1.y, c2.y);
        float maxY = (float) Math.max(c1.y, c2.y);

        guiNode.detachAllChildren();

        Geometry g1 = new Geometry();
        g1.setMesh(new Line(new Vector3f(minX, minY, 0),
                new Vector3f(maxX, minY, 0)));
        g1.setMaterial(mm.getColor(ColorRGBA.White));
        guiNode.attachChild(g1);

        Geometry g2 = new Geometry();
        g2.setMesh(new Line(new Vector3f(minX, maxY, 0),
                new Vector3f(maxX, maxY, 0)));
        g2.setMaterial(mm.getColor(ColorRGBA.White));
        guiNode.attachChild(g2);
            
        Geometry g3 = new Geometry();
        g3.setMesh(new Line(new Vector3f(minX, minY, 0),
                new Vector3f(minX, maxY, 0)));
        g3.setMaterial(mm.getColor(ColorRGBA.White));
        guiNode.attachChild(g3);

        Geometry g4 = new Geometry();
        g4.setMesh(new Line(new Vector3f(maxX, minY, 0),
                new Vector3f(maxX, maxY, 0)));
        g4.setMaterial(mm.getColor(ColorRGBA.White));
        guiNode.attachChild(g4);
}

}
