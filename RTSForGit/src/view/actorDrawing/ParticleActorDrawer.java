/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.actorDrawing;

import com.jme3.effect.Particle;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import model.battlefield.actors.HikerActor;
import model.battlefield.actors.ParticleActor;
import tools.LogUtil;
import view.math.Translator;

/**
 *
 * @author Benoît
 */
public class ParticleActorDrawer {
    
    ActorDrawingManager manager;
    
    public ParticleActorDrawer(ActorDrawingManager manager){
        this.manager = manager;
    }
    
    protected void draw(ParticleActor particleActor){
        if(particleActor.launched)
            return;
        
        if(particleActor.viewElements.particleEmitter == null)
            createEmitter(particleActor);
        
        HikerActor ma = (HikerActor)particleActor.getParentModelActor();
        if(ma.viewElements.spatial == null)
            LogUtil.logger.info("missing spatial from parent actor "+ma.debbug_id+" to render particles from "+particleActor.debbug_id);
        
        Vector3f emissionPoint;
        Vector3f direction;
        if(particleActor.emissionBone != null){
            emissionPoint = Translator.toVector3f(ma.getBoneCoord(particleActor.emissionBone));
            if(particleActor.directionBone != null)
                direction = Translator.toVector3f(ma.getBoneCoord(particleActor.directionBone));
            else
                direction = new Vector3f(emissionPoint);
        } else {
            emissionPoint = Translator.toVector3f(ma.getPos());
            direction = Translator.toVector3f(ma.getPos().get2D().getTranslation(ma.getYaw(), 1).get3D(emissionPoint.z));
        }
        direction = direction.subtract(emissionPoint).normalize();
        Vector3f velocity = direction.mult((float)particleActor.velocity);

        
        
        
        ParticleEmitter pe = particleActor.viewElements.particleEmitter;
        pe.getParticleInfluencer().setInitialVelocity(velocity);
        pe.getParticleInfluencer().setVelocityVariation((float)particleActor.fanning);
        if(particleActor.facing == ParticleActor.Facing.Velocity)
            pe.setFaceNormal(direction);
        
        if(pe.getParticlesPerSec() == 0)
            pe.setParticlesPerSec(particleActor.perSecond);

        Vector3f pos = pe.getWorldTranslation();
        pe.setLocalTranslation(emissionPoint);
        
        if(particleActor.duration == 0)
            pe.emitAllParticles();

        particleActor.updateDuration();
        
        
        // trick to interpolate position of the particles when emitter moves between two frames
        // as jMonkey doesn't manage it
        if(pe.getUserData("lastPos") != null && !pe.getUserData("lastPos").equals(emissionPoint)){
            double elapsedTime = System.currentTimeMillis()-(Long)pe.getUserData("lastTime");
            for(Particle p : getParticles(pe)){
                double age = (p.startlife-p.life)*1000;
                if(age < elapsedTime)
                    p.position.interpolate((Vector3f)pe.getUserData("lastPos"), (float)(age/elapsedTime));
            }
        }
        pe.setUserData("lastPos", pos.clone());
        pe.setUserData("lastTime", System.currentTimeMillis());
    }
    
    private void createEmitter(ParticleActor actor){
        ParticleEmitter emitter = new ParticleEmitter("", ParticleMesh.Type.Triangle, actor.maxCount);
        Material m = manager.getParticleMat(actor.spritePath);
        
        if(!actor.add)
            m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        emitter.setMaterial(m);
        emitter.setParticlesPerSec(actor.perSecond);
        emitter.setImagesX(actor.nbRow); 
        emitter.setImagesY(actor.nbCol);

        emitter.setStartColor(Translator.toColorRGBA(actor.startColor));
        emitter.setEndColor(Translator.toColorRGBA(actor.endColor));

        emitter.setStartSize((float)actor.startSize);
        emitter.setEndSize((float)actor.endSize);
        if(actor.gravity)
            emitter.setGravity(0, 0, 4);
        else
            emitter.setGravity(0, 0, 0);

        emitter.setLowLife((float)actor.minLife);
        emitter.setHighLife((float)actor.maxLife);
        emitter.setRotateSpeed((float)actor.rotationSpeed);

        if(actor.startVariation != 0)
            emitter.setShape(new EmitterSphereShape(Vector3f.ZERO, (float)actor.startVariation));

        if(actor.facing == ParticleActor.Facing.Horizontal)
            emitter.setFaceNormal(Vector3f.UNIT_Z);
        if(actor.velocity != 0)
            emitter.setFacingVelocity(true);
        manager.mainNode.attachChild(emitter);
        actor.viewElements.particleEmitter = emitter;
    }
    
    private ArrayList<Particle> getParticles(ParticleEmitter pe){
        ArrayList<Particle> res = new ArrayList<>();
        for(int i = 0; i<pe.getParticles().length; i++){
            if(pe.getParticles()[i].life != 0)
                res.add(pe.getParticles()[i]);
        }
        return res;
    }
}
