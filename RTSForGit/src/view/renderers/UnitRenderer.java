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
import java.util.HashMap;
import math.Angle;
import model.Commander;
import model.map.Map;
import model.army.data.Unit;
import model.army.ArmyManager;
import model.army.data.Projectile;
import view.material.MaterialManager;
import view.math.Translator;
import view.mesh.Circle;

/**
 *
 * @author Benoît
 */
public class UnitRenderer implements AnimEventListener {
    ArmyManager ArmyManager;
    Map map;
    MaterialManager mm;
    AssetManager am;
    Commander commander;
    public Node mainNode = new Node();
    public Node selNode = new Node();
    public Node laserNode = new Node();
    public Node projectileNode = new Node();
    
    HashMap<String, Spatial> models = new HashMap<>();
    HashMap<Unit, Unit.State> stateSave = new HashMap<>();
    
	
    public UnitRenderer(ArmyManager um, Map map, MaterialManager mm, AssetManager am, Commander commander) {
        this.ArmyManager = um;
        this.map = map;
        this.mm = mm;
        this.am = am;
        this.commander = commander;
        mainNode.attachChild(laserNode);
        mainNode.attachChild(projectileNode);
    }
    
    public void renderFirstTime(){
        for(Unit u : ArmyManager.getUnits()) {
            if(!models.containsKey(u.modelPath))
                models.put(u.modelPath, am.loadModel("models/"+u.modelPath));
                
            Spatial s = models.get(u.modelPath).clone();
            s.setName(u.label);
            s.setShadowMode(RenderQueue.ShadowMode.Cast);
            mainNode.attachChild(s);

            AnimControl control = s.getControl(AnimControl.class);
            control.addListener(this);
            control.createChannel();


            // faction box
//            Geometry factionBox = new Geometry(u.toString()+"faction");
//            factionBox.setMesh(new Box(0.025f, 0.025f, 0.05f));
//            factionBox.setMaterial(mm.getLightingColor(Translator.toColorRGBA(u.faction.c)));
//            mainNode.attachChild(factionBox);

            // selection circle
            Geometry g = new Geometry(u.toString()+"sel");
            g.setMesh(new Circle(0.5f, 10));
            g.setMaterial(mm.greenMaterial);
            g.rotate((float)Angle.RIGHT, 0, 0);
            
            selNode.attachChild(g);
        }
        // FlowField rendering
//        Node opt = new Node();
//        Node opt2 = new Node();
//        for(int x=0; x<127; x++)
//            for(int y=0; y<127; y++){
//                int heat = um.ff.heatMap[x][y];
//                Geometry box = new Geometry();
//                box.setMesh(new Box(0.1f, 0.1f, 0.1f));
//                box.setMaterial(mm.getLightingColor(Translator.toColorRGBA(new Color((int)(heat*16000000/um.ff.maxHeat)))));
//                box.setLocalTranslation(x+0.5f, y+0.5f, 3);
//                opt2.attachChild(box);
//                
//                Geometry line = new Geometry();
//                line.setMesh(new Line(Translator.toVector3f(Point2D.ORIGIN, 0), Translator.toVector3f(um.ff.getVector(map.getTile(new Point2D(x, y))).getDivision(2), 0)));
////                LogUtil.logger.info("distance : "+Point2D.ORIGIN.getDistance(um.ff.getVector(new Point2D(x, y))));
//                line.setMaterial(mm.redMaterial);
//                line.setLocalTranslation(x+0.5f, y+0.5f, 3);
//                opt.attachChild(line);
//            }
//        mainNode.attachChild(GeometryBatchFactory.optimize(opt));
//        mainNode.attachChild(GeometryBatchFactory.optimize(opt2));
    }
    
    public void renderMovers() {
        laserNode.detachAllChildren();
        Node pn = new Node();
        for(Projectile p : ArmyManager.projectiles){
            Spatial s = projectileNode.getChild(p.label);
            if(s == null){
                if(!models.containsKey(p.modelPath))
                    models.put(p.modelPath, am.loadModel("models/"+p.modelPath));
                s = models.get(p.modelPath).clone();
                s.setName(p.label);
                s.setLocalScale(0.0025f);
                s.setShadowMode(RenderQueue.ShadowMode.Cast);
            }
            Quaternion r = new Quaternion();
            r.fromAngles(0, 0, (float)(p.getOrientation()+Angle.RIGHT));
            s.setLocalTranslation(Translator.toVector3f(p.getPos3D()));
            s.setLocalRotation(r);
            
            pn.attachChild(s);
        }
        projectileNode.detachAllChildren();
        for(Spatial s : pn.getChildren())
            projectileNode.attachChild(s);
        
        
        for(Unit u : ArmyManager.getUnits()) {
            // update the unit graphic
            Spatial s = mainNode.getChild(u.label);
            manageAnim(u, s);
            orientTurret(u, s);
            
            Quaternion r = new Quaternion();
            
            s.setLocalScale(0.0025f);
            if(u.id.equals("humanCruiser"))
                s.scale(3, 3, 1);
            
            r.fromAngles(0, 0, (float)(u.getOrientation()+Angle.RIGHT));
            s.setLocalTranslation(Translator.toVector3f(u.getPos3D()));

            s.setLocalRotation(r);
            
            
            
            // attacking indicator
            if(u.getWeapon().range <= 0.1 && 
                    u.state == Unit.State.ATTACK && u.getWeapon().lastStrikeTime+200 > System.currentTimeMillis()){
                Unit target = u.getWeapon().getTarget();
                if(target == null)
                    continue;
                Geometry laser = new Geometry();
                laser.setMesh(new Box((float)u.getDistance(target)/2, 0.01f, 0.01f));
                laser.setMaterial(mm.getLightingColor(Translator.toColorRGBA(u.faction.c)));
                laser.rotate(0, 0, (float)target.getPos().getSubtraction(u.getPos()).getAngle());
                laser.setLocalTranslation(Translator.toVector3f(target.getPos().getAddition(u.getPos()).getDivision(2), u.getPos3D().z+0.3));
                laserNode.attachChild(laser);
            }

            // update faction box
//            Spatial factionBox = mainNode.getChild(u.toString()+"faction");
//            factionBox.setLocalTranslation((float)u.pos.x, (float)u.pos.y, (float)u.z+1f);
            
            // draw forces
//            drawForce(u.pos, u.sm.separationForce, new ColorRGBA(0f, 0f, 0.5f, 1f));
//            drawForce(u.pos, u.sm.cohesionForce, new ColorRGBA(0f, 0f, 0.9f, 1f));
//            drawForce(u.pos, u.sm.alignementForce, new ColorRGBA(0f, 0.5f, 0.5f, 1f));
//            drawForce(u.pos, u.sm.destinationForce, new ColorRGBA(0f, 1f, 0f, 1f));
//            drawForce(u.pos, u.sm.avoidModification, new ColorRGBA(1f, 0f, 0f, 1f));
            
            // attach selections circles
            Spatial sel = mainNode.getChild(u.toString()+"sel");
            if(sel != null)
                selNode.attachChild(sel);
        }
        for(Unit u : commander.selection){
            // update selection circles
            Spatial uSpatial = selNode.getChild(u.toString()+"sel");
            if(uSpatial==null)
                continue;
            uSpatial.setLocalScale((float)u.getRadius()*2);
            uSpatial.setLocalTranslation(Translator.toVector3f(u.getPos3D().getAddition(0, 0, 0.2)));
            mainNode.attachChild(uSpatial);
            
            // map obstacle debug
//            AlignedBoundingBox bb = u.getBoundingCircle().getABB();
//            Geometry g = new Geometry();
//            g.setMesh(new Box((float)bb.width/2, (float)bb.height/2, 0.5f));
//            g.setMaterial(mm.cyanMaterial);
//            g.setQueueBucket(RenderQueue.Bucket.Transparent);   
//            g.setLocalTranslation(Translator.toVector3f(u.pos, u.z));
//            laserNode.attachChild(g);
//            
//            for(AlignedBoundingBox wall : u.mm.walls){
//                Point2D wallCenter = wall.getPoints().get(0).getAddition(0.5, 0.5);
//                g = new Geometry();
//                g.setMesh(new Box((float)wall.width/2, (float)wall.height/2, 0.5f));
//                g.setMaterial(mm.redMaterial);
//                g.setLocalTranslation(Translator.toVector3f(wallCenter, map.getTile(wallCenter).level));
//                laserNode.attachChild(g);
//            }
        }
        
    }

    private void orientTurret(Unit u, Spatial s) {
        if(!u.hasTurret())
            return;
        AnimControl control = s.getControl(AnimControl.class);
        Skeleton sk = control.getSkeleton();
        Bone turretBone = sk.getBone(u.getTurrets().get(0).boneName);
        if(turretBone == null)
            throw new RuntimeException("head oriented unit has no head bone");
        
        Quaternion r = turretBone.getWorldBindRotation()
                .mult(new Quaternion().fromAngleAxis((float)Angle.RIGHT, Vector3f.UNIT_Z))
                .mult(new Quaternion().fromAngleAxis((float)u.getTurretOrientation(), Vector3f.UNIT_Y));
        
        turretBone.setUserControl(true);
        turretBone.setUserTransforms(Vector3f.ZERO, r, Vector3f.UNIT_XYZ);
//        sk.updateWorldVectors();
    }
    
    private void manageAnim(Unit u, Spatial s) {
        if(s instanceof Geometry)
            return;
        Unit.State lastState = stateSave.get(u);
        if(lastState == null )
            stateSave.put(u, u.state);
        
        if(u.state == lastState)
            return;
        
        // at this point, we know that the state has changed
        stateSave.put(u, u.state);
        
        AnimControl control = s.getControl(AnimControl.class);
        AnimChannel channel = control.getChannel(0);
        
        switch (u.state){
            case IDLE : 
                channel.setAnim("idle");
                channel.setLoopMode(LoopMode.Cycle);
                channel.setSpeed(1);
                break;
            case MOVE :
                if(u.modelPath.equals("human/infantry01.mesh.xml")){
                    channel.setAnim("run");
                    channel.setLoopMode(LoopMode.Cycle);
                    channel.setSpeed(2);
                } else {
                    channel.setAnim("run");
                    channel.setLoopMode(LoopMode.Cycle);
                    channel.setSpeed(2);
                }                    
                break;
        }
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if(animName.equals("start_run")) {
            channel.setAnim("run");
            channel.setLoopMode(LoopMode.Cycle);
            channel.setSpeed(2);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
    
    public void drawForce(Point2D origin, Point2D force, ColorRGBA color){
        if(force.equals(Point2D.ORIGIN))
            return;
        
        double length = force.getLength();
        double angle = force.getAngle();
        
        Geometry g = new Geometry();
        g.setMesh(new Box((float)length/2, 0.02f, 0.02f));
        g.setMaterial(mm.getLightingColor(color));
        g.rotate(0, 0, (float)angle);
        g.setLocalTranslation(Translator.toVector3f(origin.getTranslation(angle, length/2), 0.3f));
        laserNode.attachChild(g);
    }
}
