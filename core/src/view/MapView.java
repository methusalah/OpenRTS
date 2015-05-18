package view;

import geometry.geom2d.Point2D;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.ModelManager;
import view.acting.Backstage;
import view.mapDrawing.LightDrawer;
import view.mapDrawing.MapRenderer;
import view.material.MaterialManager;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;

public class MapView implements ActionListener {

	// External ressources
	public Node rootNode;
	public Node guiNode = new Node();
	protected PhysicsSpace physicsSpace;

	// Renderers
	public MapRenderer mapRend;
	public Backstage actorManager;
	protected LightDrawer lightDrawer;

	// Internal ressources
	protected MaterialManager materialManager;
	protected ViewPort vp;
	protected AssetManager assetManager;
	public Pointer pointer;

	public MapView(Node rootNode, Node gui, PhysicsSpace physicsSpace, AssetManager am, ViewPort vp) {
		ModelManager.addListener(this);
		this.rootNode = rootNode;
		this.physicsSpace = physicsSpace;
		gui.attachChild(guiNode);

		materialManager = new MaterialManager(am);
		this.assetManager = am;
		this.vp = vp;
		pointer = new Pointer();

		lightDrawer = new LightDrawer(this, am, rootNode, vp);
		ModelManager.battlefield.sunLight.addListener(lightDrawer);

		mapRend = new MapRenderer(this, materialManager, am);
		rootNode.attachChild(mapRend.mainNode);
		mapRend.mainPhysicsSpace = physicsSpace;
		ModelManager.toolManager.addListener(mapRend);

		actorManager = new Backstage(am, materialManager, ModelManager.battlefield.actorPool);
		rootNode.attachChild(actorManager.mainNode);
		actorManager.mainPhysicsSpace = physicsSpace;

		createSky();
	}

	public void reset() {
		rootNode.detachChild(mapRend.mainNode);
		rootNode.detachChild(actorManager.mainNode);

		ModelManager.toolManager.removeListener(mapRend);

		mapRend = new MapRenderer(this, materialManager, assetManager);
		rootNode.attachChild(mapRend.mainNode);
		mapRend.mainPhysicsSpace = physicsSpace;
		ModelManager.toolManager.addListener(mapRend);
		mapRend.renderTiles();

		lightDrawer.reset();
		lightDrawer.updateLights();
		ModelManager.battlefield.sunLight.addListener(lightDrawer);

		actorManager = new Backstage(assetManager, materialManager, ModelManager.battlefield.actorPool);
		rootNode.attachChild(actorManager.mainNode);
		actorManager.mainPhysicsSpace = physicsSpace;
	}

	private void createSky() {
		vp.setBackgroundColor(new ColorRGBA(135f / 255f, 206f / 255f, 250f / 255f, 1));
		Geometry xAxe = new Geometry();
		xAxe.setMesh(new Box(5, 0.1f, 0.1f));
		xAxe.setMaterial(materialManager.getColor(ColorRGBA.Brown));
		xAxe.setLocalTranslation(5, 0, 0);
		rootNode.attachChild(xAxe);

		Geometry zAxe = new Geometry();
		zAxe.setMesh(new Box(0.1f, 0.1f, 5));
		zAxe.setMaterial(materialManager.greenMaterial);
		zAxe.setLocalTranslation(0, 0, 5);
		rootNode.attachChild(zAxe);

		Geometry yAxe = new Geometry();
		yAxe.setMesh(new Box(0.1f, 5, 0.1f));
		yAxe.setMaterial(materialManager.redMaterial);
		yAxe.setLocalTranslation(0, 5, 0);
		rootNode.attachChild(yAxe);
	}

	public void drawSelectionArea(Point2D c1, Point2D c2) {
		float minX = (float) Math.min(c1.x, c2.x);
		float maxX = (float) Math.max(c1.x, c2.x);

		float minY = (float) Math.min(c1.y, c2.y);
		float maxY = (float) Math.max(c1.y, c2.y);

		guiNode.detachAllChildren();

		Geometry g1 = new Geometry();
		g1.setMesh(new Line(new Vector3f(minX, minY, 0), new Vector3f(maxX, minY, 0)));
		g1.setMaterial(materialManager.getColor(ColorRGBA.White));
		guiNode.attachChild(g1);

		Geometry g2 = new Geometry();
		g2.setMesh(new Line(new Vector3f(minX, maxY, 0), new Vector3f(maxX, maxY, 0)));
		g2.setMaterial(materialManager.getColor(ColorRGBA.White));
		guiNode.attachChild(g2);

		Geometry g3 = new Geometry();
		g3.setMesh(new Line(new Vector3f(minX, minY, 0), new Vector3f(minX, maxY, 0)));
		g3.setMaterial(materialManager.getColor(ColorRGBA.White));
		guiNode.attachChild(g3);

		Geometry g4 = new Geometry();
		g4.setMesh(new Line(new Vector3f(maxX, minY, 0), new Vector3f(maxX, maxY, 0)));
		g4.setMaterial(materialManager.getColor(ColorRGBA.White));
		guiNode.attachChild(g4);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		reset();
	}

}
