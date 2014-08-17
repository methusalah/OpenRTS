/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.renderers;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import math.Angle;
import model.Commander;
import model.map.Map;
import model.army.data.Unit;
import model.army.ArmyManager;
import model.army.data.Actor;
import model.army.data.Projectile;
import model.army.data.actors.AnimationActor;
import model.army.data.actors.ModelActor;
import model.army.data.actors.MovableActor;
import model.army.data.actors.ParticleActor;
import model.army.data.actors.ProjectileActor;
import model.army.data.actors.UnitActor;
import tools.LogUtil;
import view.material.MaterialManager;
import view.math.Translator;
import view.mesh.Circle;

/**
 *
 * @author Benoît
 */
public class UnitRenderer implements AnimEventListener {
    private static final float DEFAULT_SCALE = 0.0025f;
    
    ArmyManager armyManager;
    Map map;
    MaterialManager mm;
    AssetManager am;
    Commander commander;
    public Node mainNode = new Node();
    
    HashMap<String, Spatial> models = new HashMap<>();
    
    public UnitRenderer(ArmyManager um, Map map, MaterialManager mm, AssetManager am, Commander commander) {
        this.armyManager = um;
        this.map = map;
        this.mm = mm;
        this.am = am;
        this.commander = commander;
    }
    
    private Spatial buildSpatial(String modelPath){
        if(!models.containsKey(modelPath))
            models.put(modelPath, am.loadModel("models/"+modelPath));
        Spatial res = models.get(modelPath).clone();
        if(res == null)
            LogUtil.logger.info(modelPath);
        AnimControl control = res.getControl(AnimControl.class);
        control.addListener(this);
        control.createChannel();
        return res;
    }
    
    public void renderFirstTime(){

    }
    
    public void renderActors(){
        // first, the spatials attached to destroyed actor are destroyed
        for(Actor a : armyManager.grabDeletedActors()){
            if(a.viewElements.spatial != null){
                mainNode.detachChild(a.viewElements.spatial);
                a.viewElements.spatial = null;
            }
            if(a.viewElements.particleEmitter != null)
                a.viewElements.particleEmitter.setParticlesPerSec(0);
            if(a.viewElements.selectionCircle != null){
                mainNode.detachChild(a.viewElements.selectionCircle);
                a.viewElements.selectionCircle = null;
            }
        }
        
        for(Actor a : armyManager.getActors()){
            if(a instanceof UnitActor)
                renderUnitActor((UnitActor)a);
            if(a instanceof ProjectileActor)
                renderProjectileActor((ProjectileActor)a);
            if(a instanceof AnimationActor)
                renderAnimationActor((AnimationActor)a);
            if(a instanceof ParticleActor)
                renderParticleActor((ParticleActor)a);
        }
        
        
        // here we use the scenegraph to grab the coordinates of all bones and store them for the model.
        for(Actor a : armyManager.getActors()){
            if(a.containsModel()){
                Skeleton sk = a.viewElements.spatial.getControl(AnimControl.class).getSkeleton();
                for(int i=0; i<sk.getBoneCount(); i++){
                    Bone b = sk.getBone(i);
                    ((ModelActor)a).boneCoords.put(b.getName(), Translator.toPoint3D(b.getWorldBindPosition()));
                }

            }
        }
        
    }
    
    private void renderMovableActor(MovableActor actor){
        if(actor.viewElements.spatial == null){
            Spatial s = buildSpatial(actor.modelPath);
            s.setLocalScale((float)actor.scale*DEFAULT_SCALE);
            s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            s.setName(actor.getLabel());
            actor.viewElements.spatial = s;
            mainNode.attachChild(s);
        }
        Spatial s = actor.viewElements.spatial;

        // translation
        s.setLocalTranslation(Translator.toVector3f(actor.getPos()));
        
        // rotation
        Quaternion r = new Quaternion();
        r.fromAngles(0, 0, (float)(actor.getOrientation()+Angle.RIGHT));
        s.setLocalRotation(r);
    }
    
    private void renderUnitActor(UnitActor actor){
        renderMovableActor(actor);
        orientTurret(actor);
        drawSelectionCircle(actor);
    }
    
    private void renderProjectileActor(ProjectileActor actor){
        renderMovableActor(actor);
    }

    private void renderAnimationActor(AnimationActor actor){
        Spatial s = actor.getParentModelActor().viewElements.spatial;
        AnimChannel channel = s.getControl(AnimControl.class).getChannel(0);
        channel.setAnim(actor.animName);
        switch (actor.cycle){
            case Once : channel.setLoopMode(LoopMode.DontLoop); break;
            case Loop : channel.setLoopMode(LoopMode.Loop); break;
            case Cycle : channel.setLoopMode(LoopMode.Cycle); break;
        }
        channel.setSpeed((float)actor.speed);
        
        actor.interrupt();
    }
    
    private void renderParticleActor(ParticleActor actor){
        if(actor.launched)
            return;
        UnitActor ua = (UnitActor)actor.getParentModelActor();
        Vector3f emissionPoint = Translator.toVector3f(getBoneWorldPos(ua, actor.emissionNode));
        Vector3f directionPoint = Translator.toVector3f(getBoneWorldPos(ua, actor.directionNode));
                
        directionPoint = directionPoint.subtract(emissionPoint).mult((float)actor.velocity);
        
        if(actor.viewElements.particleEmitter == null){
            ParticleEmitter emitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, actor.maxCount);
            
            Material m = new Material(am, "Common/MatDefs/Misc/Particle.j3md");
            m.setTexture("Texture", am.loadTexture("textures/"+actor.spritePath));
            emitter.setMaterial(m);
            
            emitter.setParticlesPerSec(actor.perSecond);
            emitter.setImagesX(actor.nbRow); 
            emitter.setImagesY(actor.nbCol);

            emitter.setStartColor(Translator.toColorRGBA(actor.startColor));
            emitter.setEndColor(Translator.toColorRGBA(actor.endColor));

            emitter.setStartSize((float)actor.startSize);
            emitter.setEndSize((float)actor.endSize);
            if(actor.gravity)
                emitter.setGravity(0, -1, 0);
            else
                emitter.setGravity(0, 0, 0);

            emitter.setLowLife((float)actor.minLife);
            emitter.setHighLife((float)actor.maxLife);

            emitter.getParticleInfluencer().setVelocityVariation((float)actor.fanning);
            mainNode.attachChild(emitter);
            actor.viewElements.particleEmitter = emitter;
        }
        ParticleEmitter pe = actor.viewElements.particleEmitter;
        pe.getParticleInfluencer().setInitialVelocity(directionPoint);
        if(pe.getParticlesPerSec() == 0)
            pe.setParticlesPerSec(actor.perSecond);
        pe.setLocalTranslation(emissionPoint);
        
        if(actor.duration == 0){
            pe.emitAllParticles();
            actor.interrupt();
        } else {
            if(actor.startTime == 0)
                actor.startTime = System.currentTimeMillis();
            else
                if(actor.startTime+actor.duration < System.currentTimeMillis())
                    actor.interrupt();
        }
            

}
    
    
    
    
    
    private void drawSelectionCircle(UnitActor actor){
        if(actor.viewElements.selectionCircle == null){
            Geometry g = new Geometry();
            g.setMesh(new Circle((float)actor.getUnit().getSeparationRadius(), 10));
            g.setMaterial(mm.greenMaterial);
            g.rotate((float)Angle.RIGHT, 0, 0);
            Node n = new Node();
            n.attachChild(g);
            actor.viewElements.selectionCircle = n;
        }
        Node n = actor.viewElements.selectionCircle;
        n.setLocalTranslation(Translator.toVector3f(actor.getPos().getAddition(0, 0, 0.2)));

        if(actor.isSelectedOn(commander)){
            if(!mainNode.hasChild(n))
                mainNode.attachChild(n);
        } else
            if(mainNode.hasChild(n))
                mainNode.detachChild(n);
    }
 
    private void orientTurret(UnitActor actor) {
        if(!actor.hasTurret())
            return;
        actor.updateTurretOrientation();
        
        Bone turretBone = actor.viewElements.spatial.getControl(AnimControl.class).getSkeleton().getBone(actor.turretBone);
        if(turretBone == null)
            throw new RuntimeException("Can't find the bone "+actor.turretBone+"for turret.");
        
        Quaternion r = turretBone.getWorldBindRotation()
                .mult(new Quaternion().fromAngleAxis((float)Angle.RIGHT, Vector3f.UNIT_Z))
                .mult(new Quaternion().fromAngleAxis((float)actor.turretOrientation, Vector3f.UNIT_Y));
        
        turretBone.setUserControl(true);
        turretBone.setUserTransforms(Vector3f.ZERO, r, Vector3f.UNIT_XYZ);
    }
    
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
    
    private Point3D getBoneWorldPos(UnitActor actor, String boneName){
        Vector3f modelSpacePos = actor.viewElements.spatial.getControl(AnimControl.class).getSkeleton().getBone(boneName).getModelSpacePosition();
        Point2D p2D = Translator.toPoint2D(modelSpacePos);
        p2D = p2D.getRotation(actor.getOrientation()+Angle.RIGHT);
        Point3D p3D = new Point3D(p2D.getMult(DEFAULT_SCALE), modelSpacePos.z*DEFAULT_SCALE, 1);
        p3D = p3D.getAddition(actor.getPos());
        return p3D;
    }

}
