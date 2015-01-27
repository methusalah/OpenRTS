/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.actorDrawing;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.battlefield.actors.Actor;
import model.battlefield.actors.ActorPool;
import model.battlefield.actors.AnimationActor;
import model.battlefield.actors.ModelActor;
import model.battlefield.actors.ParticleActor;
import model.battlefield.actors.PhysicActor;
import tools.LogUtil;
import view.View;
import view.material.MaterialManager;

/**
 *
 * @author Benoît
 */
public class ActorDrawingManager implements AnimEventListener {

    AssetManager assetManager;
    public MaterialManager materialManager;
    
    ActorPool pool;
    public Node mainNode;
    public PhysicsSpace mainPhysicsSpace;
    
    ModelActorDrawer modelDrawer;
    ParticleActorDrawer particleDrawer;
    AnimationActorDrawer animationDrawer;
    RagdollActorDrawer physicDrawer;
    
    
    HashMap<String, Spatial> models = new HashMap<>();
    
    List<ParticleEmitter> dyingEmitters = new ArrayList<>();
    List<PhysicsRigidBody> pausedPhysics = new ArrayList<>();

    public ActorDrawingManager(AssetManager assetManager, MaterialManager materialManager, ActorPool pool){
        this.assetManager = assetManager;
        this.materialManager = materialManager;
        this.pool = pool;
        mainNode = new Node();
        
        modelDrawer = new ModelActorDrawer(this);
        particleDrawer = new ParticleActorDrawer(this);
        animationDrawer = new AnimationActorDrawer();
        physicDrawer = new RagdollActorDrawer(this);
   }
    
    public void render(){
        // first, the spatials attached to interrupted actor are detached
        for(Actor a : pool.grabDeletedActors()){
            if(a.viewElements.spatial != null)
                mainNode.detachChild(a.viewElements.spatial);
            if(a.viewElements.particleEmitter != null){
            	a.viewElements.particleEmitter.setParticlesPerSec(0);
                dyingEmitters.add(a.viewElements.particleEmitter);
                a.viewElements.particleEmitter = null;                
            }
            if(a.viewElements.selectionCircle != null)
                mainNode.detachChild(a.viewElements.selectionCircle);
        }
        List<ParticleEmitter> deleted = new ArrayList<>();
    	for(ParticleEmitter pe : dyingEmitters)
    		if(pe.getNumVisibleParticles() == 0){
    			mainNode.detachChild(pe);
    			deleted.add(pe);
    		}
    	dyingEmitters.removeAll(deleted);
        
        for(Actor a : pool.getActors())
            switch (a.getType()){
            	case "model" : modelDrawer.draw((ModelActor)a); break;
            }

        for(Actor a : pool.getActors())
            switch (a.getType()){
                case "physic" : physicDrawer.draw((PhysicActor)a); break;
                case "animation" : animationDrawer.draw((AnimationActor)a); break;
                case "particle" : particleDrawer.draw((ParticleActor)a); break;
            }
    }
    
    public void pause(boolean val){
    	setEmmitersEnable(mainNode, val);
    	if(val){
    		pausedPhysics.clear();
	    	Iterator<PhysicsRigidBody> i = mainPhysicsSpace.getRigidBodyList().iterator();
	    	while(i.hasNext()){
	    		PhysicsRigidBody rb = i.next();
	    		mainPhysicsSpace.remove(rb);
	    		pausedPhysics.add(rb);
	    	}
    	} else {
    		for(PhysicsRigidBody rb : pausedPhysics)
    			mainPhysicsSpace.add(rb);
    	}
    	
	}

    public void setEmmitersEnable(Spatial s, boolean val){
    	if(s instanceof ParticleEmitter)
    		((ParticleEmitter)s).setEnabled(!val);
    	if(s instanceof Node)
	        for(Spatial child : ((Node)s).getChildren())
	        	setEmmitersEnable(child, val);
    }
    
    
    protected Spatial buildSpatial(String modelPath){
        if(!models.containsKey(modelPath))
            models.put(modelPath, assetManager.loadModel("models/"+modelPath));
        Spatial res = models.get(modelPath).clone();
        if(res == null)
            LogUtil.logger.info(modelPath);
        AnimControl control = res.getControl(AnimControl.class);
        if(control != null){
            control.addListener(this);
            control.createChannel();
        }
        mainNode.attachChild(res);
        return res;
    }
    
    protected Material getParticleMat(String texturePath){
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        m.setTexture("Texture", assetManager.loadTexture("textures/"+texturePath));
        return m;
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
//    	LogUtil.logger.info("anim changed to "+animName);
    }

}
