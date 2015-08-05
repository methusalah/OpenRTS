package view.mapDrawing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import model.ModelManager;
import model.battlefield.map.MapStyle;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.faces.manmade.ManmadeFace;
import model.battlefield.map.cliff.faces.natural.NaturalFace;
import model.battlefield.map.parcelling.Parcel;
import view.MapView;
import view.jme.SilentTangentBinormalGenerator;
import view.jme.TerrainSplatTexture;
import view.material.MaterialManager;
import view.math.TranslateUtil;

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
import geometry.math.AngleUtil;

public class MapDrawer {

	private static final Logger logger = Logger.getLogger(MapDrawer.class.getName());
	MapView view;
	AssetManager am;

	private Map<String, Spatial> models = new HashMap<>();

	private Map<Parcel, Spatial> parcelsSpatial = new HashMap<>();
	private Map<Parcel, Spatial> coverSpatial = new HashMap<>();
	private Map<Tile, List<Spatial>> tilesSpatial = new HashMap<>();

	private TerrainSplatTexture groundTexture;
	private TerrainSplatTexture coverTexture;

	public Node mainNode = new Node();
	public Node castAndReceiveNode = new Node();
	public Node receiveNode = new Node();

	public PhysicsSpace mainPhysicsSpace = new PhysicsSpace();

	public MapDrawer(MapView view, AssetManager am) {
		this.view = view;
		groundTexture = new TerrainSplatTexture(ModelManager.getBattlefield().getMap().getAtlas(), am);
		coverTexture = new TerrainSplatTexture(ModelManager.getBattlefield().getMap().getCover(), am);
		coverTexture.transp = true;
		this.am = am;
		castAndReceiveNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
		receiveNode.setShadowMode(RenderQueue.ShadowMode.Receive);
		mainNode.attachChild(castAndReceiveNode);
		mainNode.attachChild(receiveNode);
		EventManager.register(this);
	}

	public void renderTiles() {
		MapStyle style = ModelManager.getBattlefield().getMap().getStyle();
		int index = 0;
		for (String s : style.diffuses) {
			Texture diffuse = am.loadTexture(s);
			Texture normal = null;
			if (style.normals.get(index) != null) {
				normal = am.loadTexture(style.normals.get(index));
			}
			double scale = style.scales.get(index);

			groundTexture.addTexture(diffuse, normal, scale);
			index++;
		}
		groundTexture.buildMaterial();

		index = 0;
		for (String s : style.coverDiffuses) {
			Texture diffuse = am.loadTexture(s);
			Texture normal = null;
			if (style.coverNormals.get(index) != null) {
				normal = am.loadTexture(style.coverNormals.get(index));
			}
			double scale = style.coverScales.get(index);

			coverTexture.addTexture(diffuse, normal, scale);
			index++;
		}
		coverTexture.buildMaterial();

		for (Parcel parcel : ModelManager.getBattlefield().getMap().getParcelling().getAll()) {
			Geometry g = new Geometry();
			Mesh jmeMesh = TranslateUtil.toJMEMesh(parcel.getMesh());
			SilentTangentBinormalGenerator.generate(jmeMesh);
			g.setMesh(jmeMesh);
			g.setMaterial(groundTexture.getMaterial());
//			g.setQueueBucket(Bucket.Transparent);

			g.addControl(new RigidBodyControl(0));
			parcelsSpatial.put(parcel, g);
			castAndReceiveNode.attachChild(g);
			mainPhysicsSpace.add(g);

			Geometry g2 = new Geometry();
			g2.setMesh(jmeMesh);
			g2.setMaterial(coverTexture.getMaterial());
			g2.setQueueBucket(Bucket.Transparent);
			g2.setLocalTranslation(0, 0, 0.01f);
			coverSpatial.put(parcel, g2);
			castAndReceiveNode.attachChild(g2);
		}
		updateTiles(ModelManager.getBattlefield().getMap().getAll());
	}

	private Spatial getModel(String path) {
		if (!models.containsKey(path)) {
			models.put(path, am.loadModel(path));
		}
		return models.get(path).clone();
	}

	@Subscribe
	public void handleParcelUpdateEvent(ParcelUpdateEvent e) {
		updateParcels(e.getToUpdate());
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
		g.setMaterial(MaterialManager.redMaterial);
		g.setLocalTranslation((float)c.getTile().getCoord().x + 0.5f, (float)c.getTile().getCoord().y + 0.5f, (float) (c.level * Tile.STAGE_HEIGHT) + 1);

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
		g.setMesh(TranslateUtil.toJMEMesh(face.mesh));
		if (face.color != null) {
			g.setMaterial(MaterialManager.getLightingColor(TranslateUtil.toColorRGBA(face.color)));
		} else {
			g.setMaterial(MaterialManager.getLightingTexture(face.texturePath));
		}
		// g.setMaterial(mm.getLightingTexture("textures/road.jpg"));
		g.rotate(0, 0, (float) (c.angle));
		g.setLocalTranslation((float)c.getTile().getCoord().x + 0.5f, (float)c.getTile().getCoord().y + 0.5f, (float) (c.level * Tile.STAGE_HEIGHT));
		n.attachChild(g);
	}

	private void attachManmadeCliff(Cliff c) {
		Node n = new Node();
		tilesSpatial.get(c.getTile()).add(n);
		castAndReceiveNode.attachChild(n);

		ManmadeFace face = (ManmadeFace) (c.face);
		Spatial s = getModel(face.modelPath);
		if (s == null) {
			logger.warning("Can't find model " + face.modelPath);
			return;
		}
		switch (c.type) {
			case Orthogonal:
				s.rotate(0, 0, (float) (c.angle + AngleUtil.RIGHT));
				break;
			case Salient:
				s.rotate(0, 0, (float) (c.angle + AngleUtil.RIGHT));
				break;
			case Corner:
				s.rotate(0, 0, (float) (c.angle));
				break;
			default:
				break;
		}
		s.scale(0.005f);
		s.setLocalTranslation((float)c.getTile().getCoord().x + 0.5f, (float)c.getTile().getCoord().y + 0.5f, (float) (c.level * Tile.STAGE_HEIGHT) + 0.1f);
		n.attachChild(s);
	}

	private void updateParcels(List<Parcel> toUpdate) {
		for (Parcel parcel : toUpdate) {
			Mesh jmeMesh = TranslateUtil.toJMEMesh(parcel.getMesh());
			SilentTangentBinormalGenerator.generate(jmeMesh);
			Geometry g = ((Geometry) parcelsSpatial.get(parcel));
			g.setMesh(jmeMesh);
			mainPhysicsSpace.remove(g);
			mainPhysicsSpace.add(g);

			Geometry g2 = ((Geometry) coverSpatial.get(parcel));
			g2.setMesh(jmeMesh);
		}
	}
}
