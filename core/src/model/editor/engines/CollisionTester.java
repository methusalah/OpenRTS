package model.editor.engines;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import view.math.TranslateUtil;

import com.google.common.eventbus.EventBus;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.debug.BulletGhostObjectDebugControl;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

import event.EventManager;
import event.GenericEvent;

public class CollisionTester {
	private static final Logger logger = Logger.getLogger(Sower.class.getName());

	private static AssetManager am;
	public static Node root;
	private static Map<String, Spatial> models = new HashMap<>();
	private static Map<String, CompoundCollisionShape> shapes = new HashMap<>();
	
	private CollisionTester(){
		
	}
	
	public static void setAssetManager(AssetManager assetManager){
		am = assetManager;
	}
	
	public static boolean areColliding(Asset asset1, Asset asset2, boolean debug){
		Spatial s1 = getSpatialFromAsset(asset1); 
		Spatial s2 = getSpatialFromAsset(asset2);

		PhysicsSpace space = new PhysicsSpace();
		
		RigidBodyControl ghost1 = new RigidBodyControl(getCollisionShape(asset1));
		s1.addControl(ghost1);
		space.add(ghost1);

		RigidBodyControl ghost2 = new RigidBodyControl(getCollisionShape(asset2));
		s2.addControl(ghost2);
//		ghost2.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
//		space.add(ghost2);

		space.update(1);
		
//		int numCollision = ghost1.getOverlappingCount();
//		boolean collision = numCollision > 0;
		Transform t = new Transform();
		t.setRotation(s2.getLocalRotation());
		t.setTranslation(s2.getLocalTranslation());
		boolean collision = false;
		for(ChildCollisionShape hull : getCollisionShape(asset2).getChildren())
			if(!space.sweepTest(hull.shape, Transform.IDENTITY, t).isEmpty()){
				collision = true;
				break;
			}
				
		
		space.remove(ghost1);
//		space.remove(ghost2);

//		if(!collision){
//			Spatial debugS2 = DebugShapeFactory.getDebugShape(ghost2.getCollisionShape());
//			debugS2.setLocalRotation(ghost2.getPhysicsRotation());
////			Spatial debugS2 = s2;
//			Material m = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
//			m.getAdditionalRenderState().setWireframe(true);
//			m.setColor("Color", ColorRGBA.Red);
//			debugS2.setMaterial(m);
//			debugS2.setLocalTranslation(ghost2.getPhysicsLocation());
//			asset2.s = debugS2;
//			//EventManager.post(new GenericEvent(debugS2));
//		}
			
		if(!collision){// && debug){
			Material m = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
			m.getAdditionalRenderState().setWireframe(true);
			m.setColor("Color", ColorRGBA.Red);
			Spatial debugS2 = DebugShapeFactory.getDebugShape(getCollisionShape(asset2));
			debugS2.setLocalTransform(t);
//			debugS2.setLocalRotation(ghost2.getPhysicsRotation());
//			debugS2.setLocalTranslation(ghost2.getPhysicsLocation());
			debugS2.setMaterial(m);

			Material m2 = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
			m2.getAdditionalRenderState().setWireframe(true);
			m2.setColor("Color", ColorRGBA.Blue);
			Geometry linegeom = new Geometry();
			Line l = new Line(debugS2.getLocalTranslation().add(0,  0, 1), ghost1.getPhysicsLocation().add(0,  0, 1));
			linegeom.setMesh(l);
			linegeom.setMaterial(m2);
	
			asset2.s = debugS2;
			if(l.getStart().distance(l.getEnd())<2)
				asset2.links.add(linegeom);
//			EventManager.post(new GenericEvent(debugS2));
//			EventManager.post(new GenericEvent(linegeom));
			
			
		}

		return collision; 
	}
	
	private static Spatial getSpatialFromAsset(Asset asset){
		if (!models.containsKey(asset.modelPath))
			models.put(asset.modelPath, am.loadModel("models/" + asset.modelPath));
		Spatial res = models.get(asset.modelPath).clone();
		
		res.setLocalScale((float)asset.scale);
		res.setLocalTranslation(TranslateUtil.toVector3f(asset.pos));
		res.setLocalRotation(new Quaternion().fromAngles(0, 0, (float)asset.yaw));
		return res;
	}
	
	private static CompoundCollisionShape getCollisionShape(Asset asset){
		Spatial s = models.get(asset.modelPath);
//		if(!shapes.containsKey(asset.modelPath)){
			CompoundCollisionShape res = new CompoundCollisionShape();
			if(s instanceof Node){
				for(Spatial child : ((Node)s).getChildren())
					if(child instanceof Geometry){
						HullCollisionShape hull = new HullCollisionShape(((Geometry)child).getMesh());
						float scale = (float)asset.scale;
						Vector3f vScale = new Vector3f(scale, scale, scale) ;
						hull.setScale(vScale);
						res.addChildShape(hull, Vector3f.ZERO);
					}
			} else {
				logger.info("houston on a un probleme");
			}
			
						
			shapes.put(asset.modelPath, res);
//		}
//		CompoundCollisionShape res = shapes.get(asset.modelPath);
//		float scale = (float)asset.scale;
//		Vector3f vScale = new Vector3f(scale, scale, scale) ;
//		for(ChildCollisionShape hull : res.getChildren())
//			hull.shape.setScale(vScale);

		return res;
	}
}
