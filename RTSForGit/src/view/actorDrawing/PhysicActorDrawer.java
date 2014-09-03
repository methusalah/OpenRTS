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
import model.army.data.actors.MovableActor;
import model.army.data.actors.RagdollActor;
import view.math.Translator;

/**
 *
 * @author Benoît
 */
public class PhysicActorDrawer {
    ActorDrawingManager manager;
    
    public PhysicActorDrawer(ActorDrawingManager manager){
        this.manager = manager;
    }
    
    protected void draw(RagdollActor actor){
        if(actor.done)
            return;
        manager.modelDrawer.draw(actor);
        
        Spatial s = actor.viewElements.spatial;
        MovableActor ma = (MovableActor)actor.getParentModelActor();

        Vector3f massVec = s.getControl(AnimControl.class).getSkeleton().getBone(actor.massCenterBone).getModelSpacePosition().mult(s.getLocalScale());
        Node massCenter = new Node();
        manager.mainNode.detachChild(s);
        manager.mainNode.attachChild(massCenter);
        massCenter.attachChild(s);
        s.setLocalTranslation(massVec.negate());
        
        RigidBodyControl control = new RigidBodyControl((float)actor.mass*10);
        massCenter.addControl(control);
        manager.mainPhysicsSpace.add(control);
        

        // translation
        double massVecLength = massVec.length();
        double massVecAngle = new Point2D(massVec.x, massVec.y).getAngle();
        Point2D unitPos2D = ma.getPos().get2D().getTranslation(massVecAngle, massVecLength);
        control.setPhysicsLocation(Translator.toVector3f(unitPos2D.get3D(ma.getPos().z)));
        
        
        
        // rotation
        Quaternion r = new Quaternion();
        r.fromAngles(0, 0, (float)(ma.getOrientation()+Angle.RIGHT));
        control.setPhysicsRotation(r);
        
        control.applyCentralForce(Vector3f.UNIT_Z.mult(40));
        actor.done = true;
    }
}
