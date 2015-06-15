package view.mapDrawing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.ModelManager;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.faces.manmade.ManmadeFace;
import model.battlefield.map.cliff.faces.natural.NaturalFace;
import model.battlefield.map.parcel.ParcelManager;
import model.battlefield.map.parcel.ParcelMesh;
import view.MapView;
import view.jme.SilentTangentBinormalGenerator;
import view.jme.TerrainSplatTexture;
import view.material.MaterialManager;
import view.math.Translator;

import com.google.common.eventbus.Subscribe;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

import event.EventManager;
import event.ParcelUpdateEvent;
import event.TilesEvent;
import event.UpdateGroundAtlasEvent;
import geometry.math.Angle;
import geometry.tools.LogUtil;

public class MapDrawer {

	MapView view;
	MaterialManager mm;
	AssetManager am;

	private Map<String, Spatial> models = new HashMap<>();

	private Map<ParcelMesh, Spatial> parcelsSpatial = new HashMap<>();
	private Map<ParcelMesh, Spatial> layerSpatial = new HashMap<>();
	private Map<Tile, List<Spatial>> tilesSpatial = new HashMap<>();

	private TerrainSplatTexture groundTexture;
	private TerrainSplatTexture coverTexture;

	public Node mainNode = new Node();
	public Node castAndReceiveNode = new Node();
	public Node receiveNode = new Node();

	public PhysicsSpace mainPhysicsSpace = new PhysicsSpace();

	public MapDrawer(MapView view, MaterialManager mm, AssetManager am) {
		this.view = view;
		groundTexture = new TerrainSplatTexture(ModelManager.getBattlefield().getMap().atlas, am);
		coverTexture = new TerrainSplatTexture(ModelManager.getBattlefield().getMap().cover, am);
		coverTexture.transp = true;
		this.mm = mm;
		this.am = am;
		castAndReceiveNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
		receiveNode.setShadowMode(RenderQueue.ShadowMode.Receive);
		mainNode.attachChild(castAndReceiveNode);
		mainNode.attachChild(receiveNode);
		EventManager.register(this);
	}

	public void renderTiles() {
		int index = 0;
		for (String s : ModelManager.getBattlefield().getMap().style.diffuses) {
			Texture diffuse = am.loadTexture(s);
			Texture normal = null;
			if (ModelManager.getBattlefield().getMap().style.normals.get(index) != null) {
				normal = am.loadTexture(ModelManager.getBattlefield().getMap().style.normals.get(index));
			}
			double scale = ModelManager.getBattlefield().getMap().style.scales.get(index);
			
			groundTexture.addTexture(diffuse, normal, scale);
			index++;
		}
		groundTexture.buildMaterial();

		index = 0;
		for (String s : ModelManager.getBattlefield().getMap().style.coverDiffuses) {
			Texture diffuse = am.loadTexture(s);
			Texture normal = null;
			if (ModelManager.getBattlefield().getMap().style.coverNormals.get(index) != null) {
				normal = am.loadTexture(ModelManager.getBattlefield().getMap().style.coverNormals.get(index));
			}
			double scale = ModelManager.getBattlefield().getMap().style.coverScales.get(index);
			
			coverTexture.addTexture(diffuse, normal, scale);
			index++;
		}
		coverTexture.buildMaterial();

		for (ParcelMesh mesh : ParcelManager.getMeshes()) {
			Geometry g = new Geometry();
			Mesh jmeMesh = Translator.toJMEMesh(mesh);
			SilentTangentBinormalGenerator.generate(jmeMesh);
			g.setMesh(jmeMesh);
			g.setMaterial(groundTexture.getMaterial());
			g.setQueueBucket(Bucket.Transparent);

			g.addControl(new RigidBodyControl(0));
			parcelsSpatial.put(mesh, g);
			castAndReceiveNode.attachChild(g);
			mainPhysicsSpace.add(g);
			
			Geometry g2 = new Geometry();
			g2.setMesh(jmeMesh);
			g2.setMaterial(coverTexture.getMaterial());
			g2.setQueueBucket(Bucket.Transparent);
			g2.setLocalTranslation(0, 0, 0.001f);
			layerSpatial.put(mesh, g2);
			castAndReceiveNode.attachChild(g2);
		}
		updateTiles(ModelManager.getBattlefield().getMap().getTiles());
	}

	private Spatial getModel(String path) {
		if (!models.containsKey(path)) {
			models.put(path, am.loadModel(path));
		}
		return models.get(path).clone();
	}

	@Subscribe
	public void handleParcelUpdateEvent(ParcelUpdateEvent e) {
		updateParcelsFor(e.getToUpdate());
	}

	@Subscribe
	public void handleGroundUpdateEvent(UpdateGroundAtlasEvent e) {
		updateGroundTexture();
	}

	@Subscribe
	public void handleTileEvent(TilesEvent e) {
		updateTiles(e.getExtended());
	}

	private void updateGroundTexture() {
		groundTexture.getMaterial();
		coverTexture.getMaterial();
	}

	private void updateTiles(List<Tile> tiles) {
		for (Tile t : tiles) {
			freeTileNode(t);
			for (Cliff c : t.getCliffs()) {
				if (c.type == Cliff.Type.Bugged) {
					attachBuggedCliff(c);
				} else if (c.face == null) {
					continue;
				} else if (c.face.getType().equals("natural")) {
					attachNaturalCliff(c);
				} else if (c.face.getType().equals("manmade")) {
					attachManmadeCliff(c);
				}
			}
		}
	}

	private void freeTileNode(Tile t) {
		if (tilesSpatial.get(t) == null) {
			tilesSpatial.put(t, new ArrayList<Spatial>());
		}
		List<Spatial> nodes = tilesSpatial.get(t);
		for (Spatial s : nodes) {
			castAndReceiveNode.detachChild(s);
		}
		tilesSpatial.get(t).clear();
	}

	private void attachBuggedCliff(Cliff c) {
		Geometry g = new Geometry();
		g.setMesh(new Box(0.5f, 0.5f, 1));
		g.setMaterial(mm.redMaterial);
		g.setLocalTranslation(c.getTile().x + 0.5f, c.getTile().y + 0.5f, (float) (c.level * Tile.STAGE_HEIGHT) + 1);

		Node n = new Node();
		n.attachChild(g);
		tilesSpatial.get(c.getTile()).add(n);
		castAndReceiveNode.attachChild(n);
	}

	private void attachNaturalCliff(Cliff c) {
		Node n = new Node();
		tilesSpatial.get(c.getTile()).add(n);
		castAndReceiveNode.attachChild(n);

		NaturalFace face = (NaturalFace) (c.face);
		Geometry g = new Geometry();
		g.setMesh(Translator.toJMEMesh(face.mesh));
		if (face.color != null) {
			g.setMaterial(mm.getLightingColor(Translator.toColorRGBA(face.color)));
		} else {
			g.setMaterial(mm.getLightingTexture(face.texturePath));
		}
		// g.setMaterial(mm.getLightingTexture("textures/road.jpg"));
		g.rotate(0, 0, (float) (c.angle));
		g.setLocalTranslation(c.getTile().x + 0.5f, c.getTile().y + 0.5f, (float) (c.level * Tile.STAGE_HEIGHT));
		n.attachChild(g);
	}

	private void attachManmadeCliff(Cliff c) {
		Node n = new Node();
		tilesSpatial.get(c.getTile()).add(n);
		castAndReceiveNode.attachChild(n);

		ManmadeFace face = (ManmadeFace) (c.face);
		Spatial s = getModel(face.modelPath);
		if (s == null) {
			LogUtil.logger.warning("Can't find model " + face.modelPath);
			return;
		}
		switch (c.type) {
			case Orthogonal:
				s.rotate(0, 0, (float) (c.angle + Angle.RIGHT));
				break;
			case Salient:
				s.rotate(0, 0, (float) (c.angle + Angle.RIGHT));
				break;
			case Corner:
				s.rotate(0, 0, (float) (c.angle));
				break;
			default:
				break;
		}
		s.scale(0.005f);
		s.setLocalTranslation(c.getTile().x + 0.5f, c.getTile().y + 0.5f, (float) (c.level * Tile.STAGE_HEIGHT) + 0.1f);
		n.attachChild(s);
	}

	private void updateParcelsFor(List<ParcelMesh> toUpdate) {
		for (ParcelMesh parcel : toUpdate) {
			Mesh jmeMesh = Translator.toJMEMesh(parcel);
			SilentTangentBinormalGenerator.generate(jmeMesh);
			Geometry g = ((Geometry) parcelsSpatial.get(parcel));
			g.setMesh(jmeMesh);
			mainPhysicsSpace.remove(g);
			mainPhysicsSpace.add(g);
			
			Geometry g2 = ((Geometry) layerSpatial.get(parcel));
			g2.setMesh(jmeMesh);
		}
	}
}
