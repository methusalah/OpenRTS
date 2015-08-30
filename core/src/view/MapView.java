package view;

import java.util.logging.Logger;

import model.ModelManager;
import openrts.guice.annotation.GuiNodeRef;
import openrts.guice.annotation.RootNodeRef;
import openrts.guice.annotation.ViewPortRef;
import view.acting.ActorDrawer;
import view.mapDrawing.LightDrawer;
import view.mapDrawing.MapDrawer;
import view.material.MaterialManager;
import brainless.openrts.event.BattleFieldUpdateEvent;
import brainless.openrts.event.EventManager;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;

import geometry.geom2d.Point2D;

public class MapView {

	private static final Logger logger = Logger.getLogger(MapView.class.getName());

	// External ressources
	protected Node rootNode;
	protected Node guiNode = new Node();
	protected PhysicsSpace physicsSpace;

	// Drawers
	private MapDrawer mapDrawer;
	private ActorDrawer actorDrawer;
	protected LightDrawer lightDrawer;

	// Internal ressources
	protected ViewPort vp;
	protected AssetManager assetManager;
	
	protected MaterialManager materialManager;
	protected Injector injector;

	@Inject
	public MapView(@RootNodeRef Node rootNode, @GuiNodeRef Node gui, PhysicsSpace physicsSpace, AssetManager am, @ViewPortRef ViewPort vp, MaterialManager materialManager, Injector injector) {
		this.rootNode = rootNode;
		this.physicsSpace = physicsSpace;
		gui.attachChild(guiNode);

		this.assetManager = am;
		this.vp = vp;
		this.materialManager = materialManager;
		this.injector = injector;

		lightDrawer = new LightDrawer(this, am, rootNode, vp);

		createSky();
		EventManager.register(this);
	}

	public void reset() {
		logger.info("reset");
		// Light drawer
		lightDrawer.reset();
		ModelManager.getBattlefield().getSunLight().addListener(lightDrawer);

		// map drawer
		if(mapDrawer != null){
			rootNode.detachChild(mapDrawer.mainNode);
			EventManager.unregister(mapDrawer);
		}
		mapDrawer = injector.getInstance(MapDrawer.class);
		rootNode.attachChild(mapDrawer.mainNode);
		mapDrawer.mainPhysicsSpace = physicsSpace;

		// actor drawer
		if(actorDrawer != null){
			rootNode.detachChild(actorDrawer.mainNode);
		}
		actorDrawer = injector.getInstance(ActorDrawer.class);
		rootNode.attachChild(actorDrawer.mainNode);
		actorDrawer.mainPhysicsSpace = physicsSpace;

		mapDrawer.renderTiles();
	}

	private void createSky() {
		vp.setBackgroundColor(new ColorRGBA(135f / 255f, 206f / 255f, 250f / 255f, 1));
		Geometry xAxe = new Geometry();
		xAxe.setMesh(new Box(5, 0.1f, 0.1f));
		xAxe.setMaterial(materialManager.getColor(ColorRGBA.Brown));
		xAxe.setLocalTranslation(5, 0, 0);
		getRootNode().attachChild(xAxe);

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
		float minX = (float) Math.min(c1.getX(), c2.getX());
		float maxX = (float) Math.max(c1.getX(), c2.getX());

		float minY = (float) Math.min(c1.getY(), c2.getY());
		float maxY = (float) Math.max(c1.getY(), c2.getY());

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

	@Subscribe
	public void handleUpdate(BattleFieldUpdateEvent e) {
		reset();
	}

	public ActorDrawer getActorManager() {
		return actorDrawer;
	}

	public Node getGuiNode() {
		return guiNode;
	}

	public void setGuiNode(Node guiNode) {
		this.guiNode = guiNode;
	}

	public Node getRootNode() {
		return rootNode;
	}

	// public void setRootNode(Node rootNode) {
	// this.rootNode = rootNode;
	// }

	public MapDrawer getMapRend() {
		return mapDrawer;
	}

	public void setMapRend(MapDrawer mapRend) {
		this.mapDrawer = mapRend;
	}

}
