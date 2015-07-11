/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.acting;

import geometry.geom2d.Point2D;
import geometry.math.AngleUtil;
import geometry.math.RandomUtil;
import model.battlefield.actors.Actor;
import model.battlefield.actors.ModelActor;
import model.battlefield.actors.PhysicActor;
import view.math.TranslateUtil;

import com.jme3.animation.AnimControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Benoît
 */
public class RagdollPerformer extends Performer{
	public RagdollPerformer(ActorDrawer bs){
		super(bs);
	}

	@Override
	public void perform(Actor a) {
		PhysicActor actor = (PhysicActor)a;
		if(!actor.launched){
			actor.life = actor.startLife;
			actorDrawer.modelPfm.perform(actor);

			Spatial s = actor.getViewElements().spatial;
			ModelActor ma = actor.getParentModelActor();

			Vector3f massVec = s.getControl(AnimControl.class).getSkeleton().getBone(actor.massCenterBone).getModelSpacePosition().mult(s.getLocalScale());
			Node massCenter = new Node();
			actorDrawer.mainNode.detachChild(s);
			actorDrawer.mainNode.attachChild(massCenter);
			massCenter.attachChild(s);
			s.setLocalTranslation(massVec.negate());

			RigidBodyControl control = new RigidBodyControl((float)actor.mass*100);
			massCenter.addControl(control);
			actorDrawer.mainPhysicsSpace.add(control);


			// translation
			double massVecLength = massVec.length();
			double massVecAngle = new Point2D(massVec.x, massVec.y).getAngle();
			Point2D unitPos2D = ma.getPos().get2D().getTranslation(massVecAngle, massVecLength);
			control.setPhysicsLocation(TranslateUtil.toVector3f(unitPos2D.get3D(ma.getPos().z+0.1)));


			// rotation
			Quaternion r = new Quaternion();
			r.fromAngles(0, 0, (float)(ma.getYaw()+AngleUtil.RIGHT));
			control.setPhysicsRotation(r);

			//            control.applyCentralForce(new Vector3f((float)MyRandom.next(), (float)MyRandom.next(), 1).mult(1000));
			control.applyForce(massVec.multLocal((float)RandomUtil.next(), (float)RandomUtil.next(), (float)RandomUtil.next()).mult(3000),
					new Vector3f((float)RandomUtil.between(-0.1, 0.1), (float)RandomUtil.between(-0.1, 0.1), (float)RandomUtil.between(-0.1, 0.1)));
			actor.launched = true;
			actor.getViewElements().spatial = massCenter;
		}

		Spatial s = actor.getViewElements().spatial;
		double elapsedTime = System.currentTimeMillis()-actor.timer;
		actor.timer = System.currentTimeMillis();

		if(actor.alive()){
			if(!s.getControl(RigidBodyControl.class).isActive()){
				actor.life -= elapsedTime;
				if(!actor.alive()){
					actorDrawer.mainPhysicsSpace.remove(s);
					s.removeControl(RigidBodyControl.class);
				}
			}
		} else {
			s.setLocalTranslation(s.getWorldTranslation().add(0, 0, (float)-elapsedTime/3000));
			if(s.getWorldTranslation().z < -1) {
				actor.stopActing();
			}
		}
	}
}
