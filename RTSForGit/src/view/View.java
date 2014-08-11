package view;

import view.material.MaterialManager;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.SkyFactory;
import geometry.Point2D;
import model.Model;
import view.math.Translator;
import view.renderers.MapRenderer;
import view.renderers.UnitRenderer;

public class View {

    // External ressources
    Model model;
    public Node rootNode;
    public Node guiNode = new Node();
        
    // Renderers
    public MapRenderer mapRend;
    public UnitRenderer unitsRend;


    // Internal ressources
    public MaterialManager mm;
    public ViewPort vp;
    public AssetManager am;
    public Pointer pointer;

    public View(Node scene, Node gui, AssetManager am, ViewPort vp, Model m){
        model = m;
        rootNode = scene;
        gui.attachChild(guiNode);

        mm = new MaterialManager(am);
        this.am = am;
        this.vp = vp;
        pointer = new Pointer();

        mapRend = new MapRenderer(model.map, mm, am);
        rootNode.attachChild(mapRend.mainNode);
        
        unitsRend = new UnitRenderer(model.armyManager, model.map, mm, am, model.commander);
        rootNode.attachChild(unitsRend.mainNode);
        createLight();
        createSky();
    }
	
    public void createSky() {
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
	
    public void createLight() {
    	AmbientLight al = new AmbientLight();
    	al.setColor(ColorRGBA.White.clone().multLocal(1.5f)); // bright white light
    	rootNode.addLight(al);
    	
    	DirectionalLight zenith = new DirectionalLight();
    	zenith.setColor(new ColorRGBA(250f/255f, 214f/255f, 165f/255f, 255f/255f).clone().multLocal(1.025f)); // bright white light
    	zenith.setDirection(new Vector3f(0f, 0f, -1f));
    	rootNode.addLight(zenith);
    	
//    	DirectionalLight sun = new DirectionalLight();
//    	sun.setColor(ColorRGBA.White.clone().multLocal(1.01f)); // bright white light
//    	sun.setDirection(new Vector3f(2, 1, -1f).normalize());
//    	rootNode.addLight(sun);

        DirectionalLight sunComp1 = new DirectionalLight();
    	sunComp1.setColor(new ColorRGBA(250f/255f, 214f/255f, 165f/255f, 255f/255f).clone().multLocal(2f)); // bright white light
    	sunComp1.setDirection(new Vector3f(-2, 1, -1f).normalize());
    	rootNode.addLight(sunComp1);
        
        int SHADOWMAP_SIZE = 2048;
//        DirectionalLightShadowRenderer sr = new DirectionalLightShadowRenderer(am, SHADOWMAP_SIZE, 3);
//        sr.setLight(sun);
//        vp.addProcessor(sr);
        DirectionalLightShadowFilter sf = new DirectionalLightShadowFilter(am, SHADOWMAP_SIZE, 2);
        sf.setLight(sunComp1);
        sf.setEnabled(true);
        sf.setShadowZExtend(SHADOWMAP_SIZE);
        FilterPostProcessor fpp = new FilterPostProcessor(am);
        fpp.addFilter(sf);
        vp.addProcessor(fpp);
//
//    	DirectionalLight second2 = new DirectionalLight();
//    	second2.setColor(ColorRGBA.White.clone().multLocal(1)); // bright white light
//    	second2.setDirection(new Vector3f(0f, -0.5f, -1f).normalize());
//    	rootNode.addLight(second2);
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
