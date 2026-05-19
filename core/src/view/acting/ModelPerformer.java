/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package view.acting;

import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;

import java.awt.Color;
import java.util.logging.Logger;

import model.battlefield.abstractComps.FieldComp;
import model.battlefield.actors.Actor;
import model.battlefield.actors.ModelActor;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.battlefield.map.Trinket;
import view.mapDrawing.MapDrawer;
import view.material.MaterialManager;
import view.math.TranslateUtil;
import view.mesh.Circle;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Benoît
 */
public class ModelPerformer extends Performer {
	private static final Logger logger = Logger.getLogger(MapDrawer.class.getName());
	public static final String ENTITYID_USERDATA = "entityid";

	public ModelPerformer(ActorDrawer bs) {
		super(bs);
	}

	@Override
	public void perform(Actor a) {
		ModelActor actor = (ModelActor) a;
		if (actor.getViewElements().spatial == null) {
			Spatial s = actorDrawer.buildSpatial(actor);
			actor.getViewElements().spatial = s;
			// We force update here because we need imediatly to have access to bones' absolute position.
			com.jme3.anim.AnimComposer animComposer = s.getControl(com.jme3.anim.AnimComposer.class);
			AnimControl animControl = s.getControl(AnimControl.class);
			if (animComposer != null) {
				animComposer.update(0);
			} else if (animControl != null) {
				animControl.update(0);
			} else if(actor.getComp() instanceof Unit &&
					!((Unit)actor.getComp()).getTurrets().isEmpty()) {
				throw new RuntimeException("The unit "+actor.getComp()+" attached to actor "+actor+" have one or more turret, but no AnimControl/AnimComposer.");
			}
		}

		if (actor.getComp() != null) {
			drawAsComp(actor);
		}
	}

	protected void drawAsComp(ModelActor actor) {
		Spatial s = actor.getViewElements().spatial;
		FieldComp comp = actor.getComp();
		// save the unitid in the userdata
		// TODO, may be set once in the spatial creation
		s.setUserData(ENTITYID_USERDATA, comp.getId());

		// translation
		s.setLocalTranslation(TranslateUtil.toVector3f(actor.getPos()));

		// rotation
		Quaternion r = new Quaternion();
		if (comp.getDirection() != null) {
			Point3D pu = comp.getUpDirection();
			Point3D pv = comp.getDirection();
			if (pu != null) {
				// the comp has an up vector
				// for ground comps or horizontally flying units
				Vector3f u = TranslateUtil.toVector3f(pu).normalize();
				Vector3f v = TranslateUtil.toVector3f(pv).normalize();
				r.lookAt(v, u);
				// we correct the pitch of the unit because the direction is always flatten
				// this is only to follow the terrain relief
				double angle = Math.acos(pu.getDotProduct(pv) / (pu.getNorm() * pv.getNorm()));
				r = r.mult(new Quaternion().fromAngles((float) (-angle+AngleUtil.RIGHT+actor.getPitchFix()), (float) (actor.getRollFix()), (float) (actor.getYawFix())));
			} else {
				// the comp hasn't any up vector
				// for projectiles
				Vector3f u = new Vector3f(0, -1, 0);
				Vector3f v = TranslateUtil.toVector3f(pv).normalize();
				float real = 1 + u.dot(v);
				Vector3f w = u.cross(v);
				r = new Quaternion(w.x, w.y, w.z, real).normalizeLocal();
			}
		}
		s.setLocalRotation(r);

		if (actor.getComp() instanceof Unit) {
			drawAsUnit(actor);
		} else if (actor.getComp() instanceof Projectile) {
			drawAsProjectile(actor);
		} else if (actor.getComp() instanceof Trinket) {
			drawAsTrinket(actor);
		}
	}

	protected void drawAsUnit(ModelActor actor) {
		orientTurret(actor);
		updateBoneCoords(actor);
		drawSelectionCircle(actor);
	}

	protected void drawAsProjectile(ModelActor actor) {
		updateBoneCoords(actor);
	}

	protected void drawAsTrinket(ModelActor actor) {
	}

	private void drawSelectionCircle(ModelActor actor) {
		Unit unit = (Unit) actor.getComp();
		if (actor.getViewElements().selectionCircle == null) {
			Geometry g = new Geometry();
			g.setMesh(new Circle((float) unit.getRadius(), 10));
			g.setMaterial(MaterialManager.greenMaterial);
			g.rotate((float) AngleUtil.RIGHT, 0, 0);
			Node n = new Node();
			n.attachChild(g);
			actor.getViewElements().selectionCircle = n;
		}
		Node n = actor.getViewElements().selectionCircle;
		n.setLocalTranslation(TranslateUtil.toVector3f(actor.getPos().getAddition(0, 0, 0.2)));

		if (unit.selected) {
			if (!actorDrawer.mainNode.hasChild(n)) {
				actorDrawer.mainNode.attachChild(n);
			}
		} else if (actorDrawer.mainNode.hasChild(n)) {
			actorDrawer.mainNode.detachChild(n);
		}
	}

	private void orientTurret(ModelActor actor) {
		for (Turret t : ((Unit) actor.getComp()).getTurrets()) {
			Spatial s = actor.getViewElements().spatial;

			// Try modern SkinningControl / Joint
			com.jme3.anim.SkinningControl skinCtrl = s.getControl(com.jme3.anim.SkinningControl.class);
			if (skinCtrl != null) {
				com.jme3.anim.Joint joint = skinCtrl.getArmature().getJoint(t.boneName);
				if (joint != null) {
					Quaternion r = new Quaternion().fromAngleAxis((float) t.yaw, Vector3f.UNIT_Y);
					joint.setLocalRotation(r);
					continue;
				}
			}

			// Fallback to legacy AnimControl / Bone
			AnimControl animCtrl = s.getControl(AnimControl.class);
			if (animCtrl != null) {
				Bone turretBone = animCtrl.getSkeleton().getBone(t.boneName);
				if (turretBone != null) {
					Quaternion r = new Quaternion().fromAngleAxis((float) t.yaw, Vector3f.UNIT_Y);
					turretBone.setUserControl(true);
					turretBone.setUserTransforms(Vector3f.ZERO, r, Vector3f.UNIT_XYZ);
					continue;
				}
			}

			throw new RuntimeException("Can't find the bone or joint " + t.boneName + " for turret.");
		}
	}

	private void updateBoneCoords(ModelActor actor) {
		Spatial s = actor.getViewElements().spatial;

		// Try modern SkinningControl / Armature
		com.jme3.anim.SkinningControl skinCtrl = s.getControl(com.jme3.anim.SkinningControl.class);
		if (skinCtrl != null) {
			com.jme3.anim.Armature armature = skinCtrl.getArmature();
			for (com.jme3.anim.Joint j : armature.getJointList()) {
				actor.setBone(j.getName(), getJointWorldPos(actor, j.getName()));
			}
			return;
		}

		// Fallback to legacy AnimControl
		AnimControl ctrl = s.getControl(AnimControl.class);
		if(ctrl == null) {
			return;
		}
		Skeleton sk = ctrl.getSkeleton();
		for (int i = 0; i < sk.getBoneCount(); i++) {
			Bone b = sk.getBone(i);
			actor.setBone(b.getName(), getBoneWorldPos(actor, i));
		}
	}

	private Point3D getJointWorldPos(ModelActor actor, String jointName) {
		Spatial s = actor.getViewElements().spatial;
		com.jme3.anim.SkinningControl skinCtrl = s.getControl(com.jme3.anim.SkinningControl.class);
		if (skinCtrl != null) {
			com.jme3.anim.Joint j = skinCtrl.getArmature().getJoint(jointName);
			if (j != null) {
				Vector3f modelSpacePos = j.getModelTransform().getTranslation();
				Quaternion q = s.getLocalRotation();
				modelSpacePos = q.mult(modelSpacePos);
				modelSpacePos.multLocal(s.getLocalScale());
				modelSpacePos = modelSpacePos.add(s.getLocalTranslation());
				return TranslateUtil.toPoint3D(modelSpacePos);
			}
		}
		return getBoneWorldPos(actor, jointName);
	}

	private Point3D getBoneWorldPos(ModelActor actor, String boneName) {
		return getBoneWorldPos(actor, actor.getPos(), actor.getYaw(), boneName);
	}

	private Point3D getBoneWorldPos(ModelActor actor, int boneIndex) {
		Spatial s = actor.getViewElements().spatial;
		
		com.jme3.anim.SkinningControl skinCtrl = s.getControl(com.jme3.anim.SkinningControl.class);
		if (skinCtrl != null) {
			com.jme3.anim.Joint j = skinCtrl.getArmature().getJointList().get(boneIndex);
			if (j != null) {
				return getJointWorldPos(actor, j.getName());
			}
		}

		AnimControl ctrl = s.getControl(AnimControl.class);
		if (ctrl != null) {
			return getBoneWorldPos(actor, ctrl.getSkeleton().getBone(boneIndex).getName());
		}
		return TranslateUtil.toPoint3D(s.getLocalTranslation());
	}

	private Point3D getBoneWorldPos(ModelActor actor, Point3D actorPos, double actorYaw, String boneName) {
		Spatial s = actor.getViewElements().spatial;
		Vector3f modelSpacePos = null;

		com.jme3.anim.SkinningControl skinCtrl = s.getControl(com.jme3.anim.SkinningControl.class);
		if (skinCtrl != null) {
			com.jme3.anim.Joint j = skinCtrl.getArmature().getJoint(boneName);
			if (j != null) {
				modelSpacePos = j.getModelTransform().getTranslation();
			}
		}

		if (modelSpacePos == null) {
			AnimControl ctrl = s.getControl(AnimControl.class);
			if (ctrl != null) {
				Bone b = ctrl.getSkeleton().getBone(boneName);
				if (b != null) {
					modelSpacePos = b.getModelSpacePosition();
				}
			}
		}

		if (modelSpacePos == null) {
			modelSpacePos = Vector3f.ZERO;
		}

		Quaternion q = actor.getViewElements().spatial.getLocalRotation();
		modelSpacePos = q.mult(modelSpacePos);
		modelSpacePos.multLocal(s.getLocalScale());
		modelSpacePos = modelSpacePos.add(s.getLocalTranslation());
		return TranslateUtil.toPoint3D(modelSpacePos);
	}
}
