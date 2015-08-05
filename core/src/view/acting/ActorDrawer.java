package view.acting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import model.ModelManager;
import model.battlefield.actors.Actor;
import model.battlefield.actors.ActorPool;
import model.battlefield.actors.ModelActor;
import model.battlefield.army.components.Unit;
import view.material.MaterialManager;
import view.math.TranslateUtil;

import com.google.common.eventbus.Subscribe;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import event.BattleFieldUpdateEvent;
import event.EventManager;
import event.GenericEvent;

/**
 * @author Benoît
 */
public class ActorDrawer implements AnimEventListener {

	private static final Logger logger = Logger.getLogger(ActorDrawer.class.getName());

	private AssetManager assetManager;

	public Node mainNode;
	public Node abandoned;
	public boolean askClearAbandoned = false;
	public PhysicsSpace mainPhysicsSpace;

	ModelPerformer modelPfm;
	ParticlePerformer particlePfm;
	AnimationPerformer animationPfm;
	RagdollPerformer physicPfm;
	SoundPerformer soundPfm;

	Map<String, Spatial> models = new HashMap<>();
	Map<String, AudioNode> sounds = new HashMap<>();

	List<ParticleEmitter> dyingEmitters = new ArrayList<>();
	List<PhysicsRigidBody> pausedPhysics = new ArrayList<>();
	private List<Spatial> abandonedTrinkets = new ArrayList<>(); 

	public ActorDrawer(AssetManager assetManager) {
		this.assetManager = assetManager;
		mainNode = new Node();
		abandoned = new Node();
		mainNode.attachChild(abandoned);

		modelPfm = new ModelPerformer(this);
		particlePfm = new ParticlePerformer(this);
		animationPfm = new AnimationPerformer(this);
		physicPfm = new RagdollPerformer(this);
		soundPfm = new SoundPerformer(this);
		
		EventManager.register(this);
	}
	
	@Subscribe
	public void handleBuggingSpatial(GenericEvent e) {
		if(e.getObject() == null)
			askClearAbandoned = true;
		else
			synchronized (abandonedTrinkets) {
				abandonedTrinkets.add((Spatial)e.getObject());
			}
	}


	public void render() {
		synchronized (abandonedTrinkets){
			if(!abandonedTrinkets.isEmpty()){
				for(Spatial s : abandonedTrinkets)
					abandoned.attachChild(s);
				abandonedTrinkets.clear();
			}
		}
		if(askClearAbandoned){
			askClearAbandoned = false;
			abandoned.detachAllChildren();
		}
//		for(Spatial s : abandoned.getChildren()){
//			s.setLocalTranslation(s.getLocalTranslation().add(0, 0, -0.0001f));
//			if(s.getLocalTranslation().z <= -3)
//				abandoned.detachChild(s);
//		}
			
		// first, the spatials attached to interrupted actor are detached
		ActorPool pool = ModelManager.getBattlefield().getActorPool();
		for (Actor a : pool.grabDeletedActors()) {
			if (a.getViewElements().spatial != null) {
				mainNode.detachChild(a.getViewElements().spatial);
			}
			if (a.getViewElements().particleEmitter != null) {
				a.getViewElements().particleEmitter.setParticlesPerSec(0);
				dyingEmitters.add(a.getViewElements().particleEmitter);
				a.getViewElements().particleEmitter = null;
			}
			if (a.getViewElements().selectionCircle != null) {
				mainNode.detachChild(a.getViewElements().selectionCircle);
			}
		}
		//		LogUtil.logger.info("nb attached spatial to "+this+" : "+mainNode.getChildren().size());
		List<ParticleEmitter> deleted = new ArrayList<>();
		for (ParticleEmitter pe : dyingEmitters) {
			if (pe.getNumVisibleParticles() == 0) {
				mainNode.detachChild(pe);
				deleted.add(pe);
			}
		}
		dyingEmitters.removeAll(deleted);

		for (Actor a : pool.getActors()) {
			switch (a.getType()) {
				case "model":
					modelPfm.perform(a);
					break;
			}
		}

		for (Actor a : pool.getActors()) {
			switch (a.getType()) {
				case "physic":
					physicPfm.perform(a);
					break;
				case "animation":
					animationPfm.perform(a);
					break;
				case "particle":
					particlePfm.perform(a);
					break;
				case "sound":
					soundPfm.perform(a);
					break;
			}
		}
	}

	public void pause(boolean val) {
		setEmmitersEnable(mainNode, val);
		if (val) {
			pausedPhysics.clear();
			Iterator<PhysicsRigidBody> i = mainPhysicsSpace.getRigidBodyList().iterator();
			while (i.hasNext()) {
				PhysicsRigidBody rb = i.next();
				mainPhysicsSpace.remove(rb);
				pausedPhysics.add(rb);
			}
		} else {
			for (PhysicsRigidBody rb : pausedPhysics) {
				mainPhysicsSpace.add(rb);
			}
		}

	}

	public void setEmmitersEnable(Spatial s, boolean val) {
		if (s instanceof ParticleEmitter) {
			((ParticleEmitter) s).setEnabled(!val);
		}
		if (s instanceof Node) {
			for (Spatial child : ((Node) s).getChildren()) {
				setEmmitersEnable(child, val);
			}
		}
	}

	protected Spatial buildSpatial(ModelActor actor) {
		if (!models.containsKey(actor.getModelPath())) {
			Spatial s = assetManager.loadModel("models/" + actor.getModelPath());
			models.put(actor.getModelPath(), s);
			
			// TODO to refactor. all models don't need to be transparent, neither all materials in a model.
			s.setQueueBucket(Bucket.Transparent);
			for(Spatial n : ((Node)s).getChildren())
				if(n instanceof Geometry){
					((Geometry)n).getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
					((Geometry)n).getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
				}
		}
		Spatial res = models.get(actor.getModelPath()).clone();
		
		AnimControl control = res.getControl(AnimControl.class);
		if (control != null) {
			control.addListener(this);
			control.createChannel();
		}

		for(Integer index : actor.getMaterialsByIndex().keySet()) {
			applyToSubmesh(res, null, index, actor.getMaterialsByIndex().get(index));
		}
		for(String name : actor.getMaterialsByName().keySet()) {
			applyToSubmesh(res, name, -1, actor.getMaterialsByName().get(name));
		}

		if (actor.getColor() != null) {
			res.setMaterial(MaterialManager.getLightingColor(TranslateUtil.toColorRGBA(actor.getColor())));
		} else{
			for(Integer index : actor.getSubColorsByIndex().keySet()) {
				applyToSubmesh(res, null, index, actor.getSubColorsByIndex().get(index));
			}
			for(String name : actor.getSubColorsByName().keySet()) {
				applyToSubmesh(res, name, -1, actor.getSubColorsByName().get(name));
			}
		}


		res.setLocalScale((float) actor.getScaleX(), (float) actor.getScaleY(), (float) actor.getScaleZ());
		res.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
		res.setName(actor.getLabel());
		mainNode.attachChild(res);
		return res;
	}
	
	private void applyToSubmesh(Spatial s, String subMeshName, int subMeshIndex, Object colorOrMaterial){
		if(s instanceof Geometry){
			Geometry g = (Geometry)s; 
			if(g.getName().equals(subMeshName) || subMeshIndex == 0){
				if(colorOrMaterial instanceof Color)
					g.getMaterial().setColor("Diffuse", TranslateUtil.toColorRGBA((Color)colorOrMaterial));
				else if (colorOrMaterial instanceof String)
					g.setMaterial(MaterialManager.getMaterial((String)colorOrMaterial));
				else
					throw new IllegalArgumentException();
					
				return;
			}
		} else {
			for(Spatial child : ((Node)s).getChildren()){
				applyToSubmesh(child, subMeshName, --subMeshIndex, colorOrMaterial);
			}
			return;
		}
		if(subMeshIndex > 0) {
			logger.warning("Sub mesh of index "+subMeshIndex+" doesn't seem to exist.");
		}
		if(subMeshName != null) {
			logger.warning("Sub mesh named "+subMeshName+" doesn't seem to exist.");
		}
	}


	protected Material getParticleMat(String texturePath) {
		Material m = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		m.setTexture("Texture", assetManager.loadTexture("textures/" + texturePath));
		return m;
	}

	protected AudioNode getAudioNode(String soundPath) {
		if (!sounds.containsKey(soundPath)) {
			sounds.put(soundPath, new AudioNode(assetManager, "sounds/" + soundPath));
			mainNode.attachChild(sounds.get(soundPath));
		}
		return sounds.get(soundPath);
	}

	@Override
	public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
	}

	@Override
	public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
		// LogUtil.logger.info("anim changed to "+animName);
	}
}
