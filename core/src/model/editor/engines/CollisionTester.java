package model.editor.engines;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import sun.font.PhysicalStrike;
import view.math.TranslateUtil;

import com.google.common.eventbus.EventBus;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsSpace.BroadphaseType;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.debug.BulletGhostObjectDebugControl;
import com.jme3.bullet.objects.infos.RigidBodyMotionState;
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

		// Transformations for physics don't include the scale, which is set at
		// collision shape creation  
		Transform body1Transform = new Transform();
		body1Transform.setRotation(s1.getLocalRotation());
		body1Transform.setTranslation(s1.getLocalTranslation());
		Transform body2Transform = new Transform();
		body2Transform.setRotation(s2.getLocalRotation());
		body2Transform.setTranslation(s2.getLocalTranslation());

		// We create a physic space and add the body of the first asset
		PhysicsSpace space = new PhysicsSpace();
		space.setAccuracy(0.01f);
		RigidBodyControl body1 = new RigidBodyControl(getCollisionShape(asset1), 0);
		s1.addControl(body1);
		space.add(body1);

		RigidBodyControl body2 = new RigidBodyControl(getCollisionShape(asset2), 0.01f);
		s2.addControl(body2);
		body2.setGravity(Vector3f.ZERO);
		space.add(body2);
		
		space.update(1000, 1);
		
		// We set a sweep test with a body from the second asset. 
		boolean collision = false;
//		CompoundCollisionShape comp = getCollisionShape(asset2);
//		for(ChildCollisionShape child : comp.getChildren())
//			if(!space.sweepTest(child.shape, Transform.IDENTITY, body2Transform).isEmpty()){
//				collision = true;
//				break;
//			}
		if(body2.getAngularVelocity().distance(Vector3f.ZERO) > 0.0001){
//				getPhysicsLocation().distance(TranslateUtil.toVector3f(asset2.pos)) > 0.1){
//			logger.info("physicslocation : "+body2.getPhysicsLocation()+" initial location : "+TranslateUtil.toVector3f(asset2.pos));
			
			collision = true;
		}
		
		space.remove(body1);
		space.remove(body2);
			
		if(collision){
			// This is only for drawing and debugging
			// spatial 1
			Material m = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
			m.getAdditionalRenderState().setWireframe(true);
			m.setColor("Color", ColorRGBA.Green);
			
			Spatial debugS1 = DebugShapeFactory.getDebugShape(body1.getCollisionShape());
			debugS1.setLocalTransform(body1Transform);
			debugS1.setMaterial(m);
			asset2.links.add(debugS1);

			// spatial 2
			Material m2 = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
			m2.getAdditionalRenderState().setWireframe(true);
			m2.setColor("Color", ColorRGBA.Red);
			
			Spatial debugS2 = DebugShapeFactory.getDebugShape(body2.getCollisionShape());
			debugS2.setLocalTransform(body2Transform);
			debugS2.setMaterial(m2);
			asset2.links.add(debugS2);
//			EventManager.post(new GenericEvent(debugS2));
			

			// link
			Material mlink = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
			mlink.getAdditionalRenderState().setWireframe(true);
			mlink.setColor("Color", ColorRGBA.Blue);
			
			Geometry link = new Geometry();
			Line l = new Line(debugS1.getLocalTranslation().add(0, 0, 0.5f), debugS2.getLocalTranslation().add(0, 0, 0.5f));
			link.setMesh(l);
			link.setMaterial(mlink);
			asset2.links.add(link);
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
		CompoundCollisionShape res = new CompoundCollisionShape();
		if(s instanceof Node){
			for(Spatial child : ((Node)s).getChildren())
				if(child instanceof Geometry){
					HullCollisionShape hull = new HullCollisionShape(((Geometry)child).getMesh());
					hull.setScale(Vector3f.UNIT_XYZ.mult((float)asset.scale));
					res.addChildShape(hull, Vector3f.ZERO);
				}
		} else {
			logger.info("Houston, we've got a problem.");
		}
		return res;
	}
}
