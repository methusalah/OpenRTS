/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.acting;

import java.util.ArrayList;
import java.util.logging.Logger;

import model.battlefield.actors.Actor;
import model.battlefield.actors.ModelActor;
import model.battlefield.actors.ParticleActor;
import view.mapDrawing.MapDrawer;
import view.math.TranslateUtil;

import com.jme3.effect.Particle;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;

/**
 *
 * @author Benoît
 */
public class ParticlePerformer extends Performer{

	private static final Logger logger = Logger.getLogger(MapDrawer.class.getName());

	public ParticlePerformer(ActorDrawer bs){
		super(bs);
	}

	@Override
	public void perform(Actor a) {
		ParticleActor actor = (ParticleActor)a;

		if (actor.getViewElements().particleEmitter == null) {
			createEmitter(actor);
		}

		ModelActor ma = actor.getParentModelActor();
		if (ma.getViewElements().spatial == null) {
			logger.info(actor + " parent misses spatial for " + ma);
		}

		Vector3f emissionPoint;
		Vector3f direction;
		if(actor.emissionBone != null){
			if(!ma.hasBone(actor.emissionBone)){
				logger.info(actor + " misses bone " + actor.emissionBone + " in " + ma);
				return;
			}

			emissionPoint = TranslateUtil.toVector3f(ma.getBoneCoord(actor.emissionBone));
			if(actor.directionBone != null) {
				direction = TranslateUtil.toVector3f(ma.getBoneCoord(actor.directionBone));
			} else {
				direction = new Vector3f(emissionPoint);
			}
		} else {
			emissionPoint = TranslateUtil.toVector3f(ma.getPos());
			direction = TranslateUtil.toVector3f(ma.getPos().get2D().getTranslation(ma.getYaw(), 1).get3D(emissionPoint.z));
		}
		direction = direction.subtract(emissionPoint).normalize();
		Vector3f velocity = direction.mult((float)actor.velocity);




		ParticleEmitter pe = actor.getViewElements().particleEmitter;
		pe.getParticleInfluencer().setInitialVelocity(velocity);
		pe.getParticleInfluencer().setVelocityVariation((float)actor.fanning);
		if(actor.facing == ParticleActor.Facing.Velocity) {
			pe.setFaceNormal(direction);
		}

		if(pe.getParticlesPerSec() == 0) {
			pe.setParticlesPerSec(actor.perSecond);
		}

		Vector3f pos = pe.getWorldTranslation();
		pe.setLocalTranslation(emissionPoint);

		if(actor.duration == 0) {
			pe.emitAllParticles();
		}

		actor.updateDuration();


		// trick to interpolate position of the particles when emitter moves between two frames
		// as jMonkey doesn't manage it
		if(pe.getUserData("lastPos") != null &&
				!pe.getUserData("lastPos").equals(Vector3f.ZERO) &&
				!pe.getUserData("lastPos").equals(emissionPoint)){
			double elapsedTime = System.currentTimeMillis()-(Long)pe.getUserData("lastTime");
			for(Particle p : getParticles(pe)){
				double age = (p.startlife-p.life)*1000;
				if(age < elapsedTime) {
					p.position.interpolateLocal((Vector3f)pe.getUserData("lastPos"), (float)(age/elapsedTime));
				}
			}
		}
		pe.setUserData("lastPos", pos.clone());
		pe.setUserData("lastTime", System.currentTimeMillis());

	}

	private void createEmitter(ParticleActor actor){
		ParticleEmitter emitter = new ParticleEmitter("", ParticleMesh.Type.Triangle, actor.maxCount);
		Material m = actorDrawer.getParticleMat(actor.spritePath);

		if(!actor.add) {
			m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		}
		emitter.setMaterial(m);
		emitter.setParticlesPerSec(actor.perSecond);
		emitter.setImagesX(actor.nbRow);
		emitter.setImagesY(actor.nbCol);

		emitter.setStartColor(TranslateUtil.toColorRGBA(actor.startColor));
		emitter.setEndColor(TranslateUtil.toColorRGBA(actor.endColor));

		emitter.setStartSize((float)actor.startSize);
		emitter.setEndSize((float)actor.endSize);
		if(actor.gravity) {
			emitter.setGravity(0, 0, 4);
		} else {
			emitter.setGravity(0, 0, 0);
		}

		emitter.setLowLife((float)actor.minLife);
		emitter.setHighLife((float)actor.maxLife);
		emitter.setRotateSpeed((float)actor.rotationSpeed);

		if(actor.startVariation != 0) {
			emitter.setShape(new EmitterSphereShape(Vector3f.ZERO, (float)actor.startVariation));
		}

		if(actor.facing == ParticleActor.Facing.Horizontal) {
			emitter.setFaceNormal(Vector3f.UNIT_Z);
		}
		if(actor.velocity != 0) {
			emitter.setFacingVelocity(true);
		}
		emitter.setQueueBucket(Bucket.Transparent);
		actorDrawer.mainNode.attachChild(emitter);
		actor.getViewElements().particleEmitter = emitter;
	}

	private ArrayList<Particle> getParticles(ParticleEmitter pe){
		ArrayList<Particle> res = new ArrayList<>();
		for(int i = 0; i<pe.getParticles().length; i++){
			if(pe.getParticles()[i].life != 0) {
				res.add(pe.getParticles()[i]);
			}
		}
		return res;
	}
}
