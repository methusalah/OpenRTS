/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.actorDrawing;

import com.jme3.animation.AnimControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import geometry.Point2D;
import math.Angle;
import math.MyRandom;
import model.battlefield.actors.ModelActor;
import model.battlefield.actors.PhysicActor;
import tools.LogUtil;
import view.math.Translator;

/**
 *
 * @author Benoît
 */
public class RagdollActorDrawer {
    ActorDrawingManager manager;
    
    public RagdollActorDrawer(ActorDrawingManager manager){
        this.manager = manager;
    }
    
    protected void draw(PhysicActor actor){
        if(!actor.launched){
            actor.life = actor.startLife;
            manager.modelDrawer.draw(actor);

            Spatial s = actor.viewElements.spatial;
            ModelActor ma = actor.getParentModelActor();

            Vector3f massVec = s.getControl(AnimControl.class).getSkeleton().getBone(actor.massCenterBone).getModelSpacePosition().mult(s.getLocalScale());
            Node massCenter = new Node();
            manager.mainNode.detachChild(s);
            manager.mainNode.attachChild(massCenter);
            massCenter.attachChild(s);
            s.setLocalTranslation(massVec.negate());

            RigidBodyControl control = new RigidBodyControl((float)actor.mass*100);
            massCenter.addControl(control);
            manager.mainPhysicsSpace.add(control);


            // translation
            double massVecLength = massVec.length();
            double massVecAngle = new Point2D(massVec.x, massVec.y).getAngle();
            Point2D unitPos2D = ma.getPos().get2D().getTranslation(massVecAngle, massVecLength);
            control.setPhysicsLocation(Translator.toVector3f(unitPos2D.get3D(ma.getPos().z+0.1)));


            // rotation
            Quaternion r = new Quaternion();
            r.fromAngles(0, 0, (float)(ma.getYaw()+Angle.RIGHT));
            control.setPhysicsRotation(r);

//            control.applyCentralForce(new Vector3f((float)MyRandom.next(), (float)MyRandom.next(), 1).mult(1000));
            control.applyForce(massVec.multLocal((float)MyRandom.next(), (float)MyRandom.next(), (float)MyRandom.next()).mult(3000),
            		new Vector3f((float)MyRandom.between(-0.1, 0.1), (float)MyRandom.between(-0.1, 0.1), (float)MyRandom.between(-0.1, 0.1)));
            actor.launched = true;
            actor.viewElements.spatial = massCenter;
        }

        Spatial s = actor.viewElements.spatial;
        double elapsedTime = System.currentTimeMillis()-actor.timer;
        actor.timer = System.currentTimeMillis();
 
        if(actor.alive()){
            if(!s.getControl(RigidBodyControl.class).isActive()){
                actor.life -= elapsedTime;
                if(!actor.alive()){
                    manager.mainPhysicsSpace.remove(s);
                    s.removeControl(RigidBodyControl.class);
                }
            }
        } else {
            s.setLocalTranslation(s.getWorldTranslation().add(0, 0, (float)-elapsedTime/3000));
            if(s.getWorldTranslation().z < -1)
                actor.stopActing();
        }
    }
}
