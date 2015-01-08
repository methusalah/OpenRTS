/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.actorDrawing;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.HashMap;
import math.Angle;
import model.battlefield.army.components.Turret;
import model.battlefield.actors.ModelActor;
import model.battlefield.actors.MovableActor;
import model.battlefield.actors.ProjectileActor;
import model.battlefield.actors.UnitActor;
import tools.LogUtil;
import view.math.Translator;
import view.mesh.Circle;

/**
 *
 * @author Benoît
 */
public class ModelActorDrawer {
    private static final float DEFAULT_SCALE = 0.0025f;
    
    private ActorDrawingManager manager;
    
    public ModelActorDrawer(ActorDrawingManager manager){
        this.manager = manager;
    }

    protected void draw(ModelActor modelActor){
        if(modelActor.viewElements.spatial == null){
            Spatial s = manager.buildSpatial(modelActor.modelPath);
            s.setLocalScale((float)modelActor.scale*DEFAULT_SCALE);
            s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            s.setName(modelActor.getLabel());
            modelActor.viewElements.spatial = s;
            // We force update here because we need imediatly to have access to bones' absolute position.
            AnimControl animControl = s.getControl(AnimControl.class);
            if(animControl !=  null)
                animControl.update(0);
        }
    }
    
    protected void draw(MovableActor movableActor){
        draw((ModelActor)movableActor);
        Spatial s = movableActor.viewElements.spatial;

        // translation
        s.setLocalTranslation(Translator.toVector3f(movableActor.getPos()));
        
        // rotation
        Quaternion r = new Quaternion();
        if(movableActor instanceof ProjectileActor){
            Vector3f u = new Vector3f(0, -1, 0);
            Vector3f v = Translator.toVector3f(((ProjectileActor)movableActor).getProjectile().mover.velocity).normalize();
            float real = 1+u.dot(v);
            Vector3f w = u.cross(v);
            r = new Quaternion(w.x, w.y, w.z, real).normalizeLocal();
        } else {
            r.fromAngles(0, 0, (float)(movableActor.getOrientation()+Angle.RIGHT));
        }
        s.setLocalRotation(r);
    }
    
    protected void draw(UnitActor unitActor){
        draw((MovableActor)unitActor);
        orientTurret(unitActor);
        updateBoneCoords(unitActor);
        drawSelectionCircle(unitActor);
    }
    
    protected void draw(ProjectileActor projectileActor){
        draw((MovableActor)projectileActor);
        updateBoneCoords(projectileActor);
    }
    
    private void drawSelectionCircle(UnitActor unitActor){
        if(unitActor.viewElements.selectionCircle == null){
            Geometry g = new Geometry();
            g.setMesh(new Circle((float)unitActor.getUnit().getSeparationRadius(), 10));
            g.setMaterial(manager.materialManager.greenMaterial);
            g.rotate((float)Angle.RIGHT, 0, 0);
            Node n = new Node();
            n.attachChild(g);
            unitActor.viewElements.selectionCircle = n;
        }
        Node n = unitActor.viewElements.selectionCircle;
        n.setLocalTranslation(Translator.toVector3f(unitActor.getPos().getAddition(0, 0, 0.2)));

        if(unitActor.isSelected()){
            if(!manager.mainNode.hasChild(n))
                manager.mainNode.attachChild(n);
        } else
            if(manager.mainNode.hasChild(n))
                manager.mainNode.detachChild(n);
    }
 
    private void orientTurret(UnitActor actor) {
        for(Turret t : actor.getTurrets()){
            Bone turretBone = actor.viewElements.spatial.getControl(AnimControl.class).getSkeleton().getBone(t.boneName);
            if(turretBone == null)
                throw new RuntimeException("Can't find the bone "+t.boneName+" for turret.");

            Quaternion r = turretBone.getWorldBindRotation()
                    .mult(new Quaternion().fromAngleAxis((float)Angle.RIGHT, Vector3f.UNIT_Z))
                    .mult(new Quaternion().fromAngleAxis((float)t.yaw, Vector3f.UNIT_Y));

            turretBone.setUserControl(true);
            turretBone.setUserTransforms(Vector3f.ZERO, r, Vector3f.UNIT_XYZ);
        }
    }
    
    private void updateBoneCoords(MovableActor movableActor){
        Skeleton sk = movableActor.viewElements.spatial.getControl(AnimControl.class).getSkeleton();
        for(int i=0; i<sk.getBoneCount(); i++){
            Bone b = sk.getBone(i);
            movableActor.setBone(b.getName(), getBoneWorldPos(movableActor, i));
        }
    }
    
    private Point3D getBoneWorldPos(MovableActor actor, String boneName){
        return getBoneWorldPos(actor, actor.getPos(), actor.getOrientation(), boneName);
    }

    private Point3D getBoneWorldPos(MovableActor actor, int boneIndex){
        return getBoneWorldPos(actor, actor.viewElements.spatial.getControl(AnimControl.class).getSkeleton().getBone(boneIndex).getName());
    }
    
    private Point3D getBoneWorldPos(ModelActor actor, Point3D actorPos, double actorYaw, String boneName){
        Vector3f modelSpacePos = actor.viewElements.spatial.getControl(AnimControl.class).getSkeleton().getBone(boneName).getModelSpacePosition();
        Point2D p2D = Translator.toPoint2D(modelSpacePos);
        p2D = p2D.getRotation(actorYaw+Angle.RIGHT);
        Point3D p3D = new Point3D(p2D.getMult(DEFAULT_SCALE), modelSpacePos.z*DEFAULT_SCALE, 1);
        p3D = p3D.getAddition(actorPos);
        return p3D;
        
    }

}
