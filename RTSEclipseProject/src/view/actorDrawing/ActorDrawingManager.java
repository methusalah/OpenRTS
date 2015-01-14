/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.actorDrawing;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.awt.Color;
import java.util.HashMap;
import model.battlefield.actors.Actor;
import model.battlefield.actors.ActorPool;
import model.battlefield.actors.AnimationActor;
import model.battlefield.actors.ModelActor;
import model.battlefield.actors.ParticleActor;
import model.battlefield.actors.PhysicActor;
import tools.LogUtil;
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
            if(a.viewElements.spatial != null){
                mainNode.detachChild(a.viewElements.spatial);
            }
            if(a.viewElements.particleEmitter != null)
                a.viewElements.particleEmitter.setParticlesPerSec(0);
            if(a.viewElements.selectionCircle != null)
                mainNode.detachChild(a.viewElements.selectionCircle);
        }
        
        for(Actor a : pool.getActors()){
            switch (a.getType()){
                case "default" : break;
                case "physic" : physicDrawer.draw((PhysicActor)a); break;
                case "model" : modelDrawer.draw((ModelActor)a); break;
                case "animation" : animationDrawer.draw((AnimationActor)a); break;
                case "particle" : particleDrawer.draw((ParticleActor)a); break;
            }
        }
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
    }

}
