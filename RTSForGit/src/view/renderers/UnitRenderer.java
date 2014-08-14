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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import geometry.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
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
    
    HashMap<Actor, Spatial> modelActors = new HashMap<>();
    HashMap<Actor, Node> selectionCircles = new HashMap<>();
    
	
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
        ArrayList<Actor> toRemove = new ArrayList<>();
        for(Actor a : modelActors.keySet())
            if(a.destroyed){
                mainNode.detachChild(modelActors.get(a));
                toRemove.add(a);
                if(mainNode.hasChild(selectionCircles.get(a)))
                    mainNode.detachChild(selectionCircles.get(a));
                selectionCircles.remove(a);
                }
        for(Actor a : toRemove)
            modelActors.remove(a);
        
        for(Actor a : armyManager.activeActors){
            if(a instanceof UnitActor)
                renderUnitActor((UnitActor)a);
            if(a instanceof ProjectileActor)
                renderProjectileActor((ProjectileActor)a);
            if(a instanceof AnimationActor)
                renderAnimationActor((AnimationActor)a);
        }
        
        
        // here we use the scenegraph to grab the coordinates of all bones and store them for the model.
        for(Actor a : armyManager.activeActors){
            if(a.containsModel()){
                Spatial s = modelActors.get(a);
                Skeleton sk = s.getControl(AnimControl.class).getSkeleton();
                for(int i=0; i<sk.getBoneCount(); i++){
                    Bone b = sk.getBone(i);
                    ((ModelActor)a).boneCoords.put(b.getName(), Translator.toPoint3D(b.getWorldBindPosition()));
                }

            }
        }
        
    }
    
    private void renderMovableActor(MovableActor actor){
        if(!modelActors.containsKey(actor)){
            Spatial s = buildSpatial(actor.modelPath);
            s.setLocalScale((float)actor.scale*DEFAULT_SCALE);
            s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            s.setName(actor.getLabel());
            modelActors.put(actor, s);
            mainNode.attachChild(s);
        }
        Spatial s = modelActors.get(actor);

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

    private  void renderAnimationActor(AnimationActor actor){
        if(actor.launched)
            return;
        actor.launched = true;
        
        Spatial s = modelActors.get(actor.getParentModelActor());
        AnimControl control = s.getControl(AnimControl.class);
        AnimChannel channel = control.getChannel(0);
        channel.setAnim(actor.animName);
        switch (actor.cycle){
            case Once : channel.setLoopMode(LoopMode.DontLoop); break;
            case Loop : channel.setLoopMode(LoopMode.Loop); break;
            case Cycle : channel.setLoopMode(LoopMode.Cycle); break;
        }
        channel.setSpeed((float)actor.speed);
    }
    
    
    
    
    
    private void drawSelectionCircle(UnitActor actor){
        if(!selectionCircles.containsKey(actor)){
            Geometry g = new Geometry();
            g.setMesh(new Circle((float)actor.getUnit().getSeparationRadius(), 10));
            g.setMaterial(mm.greenMaterial);
            g.rotate((float)Angle.RIGHT, 0, 0);
            Node n = new Node();
            n.attachChild(g);
            selectionCircles.put(actor, n);
        }
        Node n = selectionCircles.get(actor);
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
        Spatial s = modelActors.get(actor);
        AnimControl control = s.getControl(AnimControl.class);
        Skeleton sk = control.getSkeleton();
        Bone turretBone = sk.getBone(actor.turretBone);
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
}
