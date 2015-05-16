/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package view.acting;

import geometry.geom3d.Point3D;
import geometry.math.Angle;
import model.battlefield.actors.Actor;
import model.battlefield.actors.ModelActor;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.battlefield.map.Trinket;
import view.math.Translator;
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
	private static final float DEFAULT_SCALE = 0.0025f;
	public static final String ENTITYID_USERDATA = "entityid";

	public ModelPerformer(Backstage bs) {
		super(bs);
	}

	@Override
	public void perform(Actor a) {
		ModelActor actor = (ModelActor) a;
		if (actor.viewElements.spatial == null) {
			Spatial s = bs.buildSpatial(actor.modelPath);

			if (actor.color != null) {
				s.setMaterial(bs.materialManager.getLightingColor(Translator.toColorRGBA(actor.color)));
			}

			s.setLocalScale((float) actor.scaleX * DEFAULT_SCALE, (float) actor.scaleY * DEFAULT_SCALE, (float) actor.scaleZ * DEFAULT_SCALE);
			s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
			s.setName(actor.getLabel());
			actor.viewElements.spatial = s;
			// We force update here because we need imediatly to have access to bones' absolute position.
			AnimControl animControl = s.getControl(AnimControl.class);
			if (animControl != null) {
				animControl.update(0);
			}
		}

		if (actor.getComp() != null) {
			drawAsComp(actor);
		}

	}

	protected void drawAsComp(ModelActor actor) {
		Spatial s = actor.viewElements.spatial;
		// save the unitid in the userdata
		s.setUserData(ENTITYID_USERDATA, actor.getComp().getId());

		// translation
		s.setLocalTranslation(Translator.toVector3f(actor.getPos()));

		// rotation
		Quaternion r = new Quaternion();
		if (actor.getComp().direction != null) {
			Point3D pu = actor.getComp().upDirection;
			Point3D pv = actor.getComp().direction;
			Vector3f u = new Vector3f(0, -1, 0);
			Vector3f v = Translator.toVector3f(pv).normalize();
			if (pu != null) {
				u = Translator.toVector3f(pu).normalize();
				r.lookAt(v, u);
				double angle = Math.acos(pu.getDotProduct(pv) / (pu.getNorm() * pv.getNorm()));
				r = r.mult(new Quaternion().fromAngles(-(float) angle, 0, 0));
			} else {
				float real = 1 + u.dot(v);
				Vector3f w = u.cross(v);
				r = new Quaternion(w.x, w.y, w.z, real).normalizeLocal();
			}

		} else {
			r.fromAngles(0, 0, (float) (actor.getYaw() + Angle.RIGHT));
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
		if (actor.viewElements.selectionCircle == null) {
			Geometry g = new Geometry();
			g.setMesh(new Circle((float) unit.getRadius(), 10));
			g.setMaterial(bs.materialManager.greenMaterial);
			g.rotate((float) Angle.RIGHT, 0, 0);
			Node n = new Node();
			n.attachChild(g);
			actor.viewElements.selectionCircle = n;
		}
		Node n = actor.viewElements.selectionCircle;
		n.setLocalTranslation(Translator.toVector3f(actor.getPos().getAddition(0, 0, 0.2)));

		if (unit.selected) {
			if (!bs.mainNode.hasChild(n)) {
				bs.mainNode.attachChild(n);
			}
		} else if (bs.mainNode.hasChild(n)) {
			bs.mainNode.detachChild(n);
		}
	}

	private void orientTurret(ModelActor actor) {
		for (Turret t : ((Unit) actor.getComp()).getTurrets()) {
			Bone turretBone = actor.viewElements.spatial.getControl(AnimControl.class).getSkeleton().getBone(t.boneName);
			if (turretBone == null) {
				throw new RuntimeException("Can't find the bone " + t.boneName + " for turret.");
			}

			Quaternion r = turretBone.getWorldBindRotation().mult(new Quaternion().fromAngleAxis((float) Angle.RIGHT, Vector3f.UNIT_Z))
					.mult(new Quaternion().fromAngleAxis((float) t.yaw, Vector3f.UNIT_Y));

			turretBone.setUserControl(true);
			turretBone.setUserTransforms(Vector3f.ZERO, r, Vector3f.UNIT_XYZ);
		}
	}

	private void updateBoneCoords(ModelActor actor) {
		Skeleton sk = actor.viewElements.spatial.getControl(AnimControl.class).getSkeleton();
		for (int i = 0; i < sk.getBoneCount(); i++) {
			Bone b = sk.getBone(i);
			actor.setBone(b.getName(), getBoneWorldPos(actor, i));
		}
	}

	private Point3D getBoneWorldPos(ModelActor actor, String boneName) {
		return getBoneWorldPos(actor, actor.getPos(), actor.getYaw(), boneName);
	}

	private Point3D getBoneWorldPos(ModelActor actor, int boneIndex) {
		return getBoneWorldPos(actor, actor.viewElements.spatial.getControl(AnimControl.class).getSkeleton().getBone(boneIndex).getName());
	}

	private Point3D getBoneWorldPos(ModelActor actor, Point3D actorPos, double actorYaw, String boneName) {
		Spatial s = actor.viewElements.spatial;
		Vector3f modelSpacePos = s.getControl(AnimControl.class).getSkeleton().getBone(boneName).getModelSpacePosition();
		Quaternion q = actor.viewElements.spatial.getLocalRotation();
		modelSpacePos = q.mult(modelSpacePos);
		modelSpacePos.multLocal(s.getLocalScale());
		modelSpacePos = modelSpacePos.add(s.getLocalTranslation());
		// float scale
		// Point2D p2D = Translator.toPoint2D(modelSpacePos);
		// p2D = p2D.getRotation(actorYaw+Angle.RIGHT);
		// Point3D p3D = new Point3D(p2D.getMult(DEFAULT_SCALE), modelSpacePos.z*DEFAULT_SCALE, 1);
		// p3D = p3D.getAddition(actorPos);
		// return p3D;
		return Translator.toPoint3D(modelSpacePos);
	}
}
