/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package view.acting;

import geometry.geom3d.Point3D;
import geometry.math.Angle;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.actors.Actor;
import model.battlefield.actors.ModelActor;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.battlefield.map.Trinket;
import tools.LogUtil;
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
	public static final String ENTITYID_USERDATA = "entityid";

	public ModelPerformer(ActorDrawer bs) {
		super(bs);
	}

	@Override
	public void perform(Actor a) {
		ModelActor actor = (ModelActor) a;
		if (actor.getViewElements().spatial == null) {
			Spatial s = actorDrawer.buildSpatial(actor.getModelPath());

			if (actor.getColor() != null) {
				s.setMaterial(actorDrawer.getMaterialManager().getLightingColor(Translator.toColorRGBA(actor.getColor())));
			}

			s.setLocalScale((float) actor.getScaleX(), (float) actor.getScaleY(), (float) actor.getScaleZ());
			s.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
			s.setName(actor.getLabel());
			actor.getViewElements().spatial = s;
			// We force update here because we need imediatly to have access to bones' absolute position.
			AnimControl animControl = s.getControl(AnimControl.class);
			if (animControl != null) {
				animControl.update(0);
			} else if(actor.getComp() instanceof Unit && 
					!((Unit)actor.getComp()).getTurrets().isEmpty())
				throw new RuntimeException("The unit "+(Unit)actor.getComp()+" attached to actor "+actor+" have one or more turret, but no AnimControl.");
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
		s.setLocalTranslation(Translator.toVector3f(actor.getPos()));

		// rotation
		Quaternion r = new Quaternion();
		if (actor.getComp().direction != null) {
			Point3D pu = actor.getComp().upDirection;
			Point3D pv = actor.getComp().direction;
			if (pu != null) {
				// the comp has a up vector
				// for ground comps or horitonally flying units 
				Vector3f u = Translator.toVector3f(pu).normalize();
				Vector3f v = Translator.toVector3f(pv).normalize();
				r.lookAt(v, u);
				// we correct the pitch of the unit because the direction is always flatten
				// this is only to follow the terrain relief
				double angle = Math.acos(pu.getDotProduct(pv) / (pu.getNorm() * pv.getNorm()));
				r = r.mult(new Quaternion().fromAngles((float) (-angle+Angle.RIGHT+actor.getPitchFix()), (float) (actor.getRollFix()), (float) (actor.getYawFix())));
			} else {
				// the comp hasn't any up vector
				// for projectiles
				Vector3f u = new Vector3f(0, -1, 0);
				Vector3f v = Translator.toVector3f(pv).normalize();
				float real = 1 + u.dot(v);
				Vector3f w = u.cross(v);
				r = new Quaternion(w.x, w.y, w.z, real).normalizeLocal();
			}
		} else {
			r.fromAngles((float)comp.roll, (float)comp.pitch, (float) actor.getYaw());
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
			g.setMaterial(actorDrawer.getMaterialManager().greenMaterial);
			g.rotate((float) Angle.RIGHT, 0, 0);
			Node n = new Node();
			n.attachChild(g);
			actor.getViewElements().selectionCircle = n;
		}
		Node n = actor.getViewElements().selectionCircle;
		n.setLocalTranslation(Translator.toVector3f(actor.getPos().getAddition(0, 0, 0.2)));

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
			Bone turretBone = actor.getViewElements().spatial.getControl(AnimControl.class).getSkeleton().getBone(t.boneName);
			if (turretBone == null) {
				throw new RuntimeException("Can't find the bone " + t.boneName + " for turret.");
			}

			Vector3f axis;
			switch (t.boneAxis){
			case "X" : axis = Vector3f.UNIT_X; break;
			case "Y" : axis = Vector3f.UNIT_Y; break;
			case "Z" : axis = Vector3f.UNIT_Z; break;
			default : throw new IllegalArgumentException("Wrong bone axis for "+((Unit)actor.getComp()).builderID+" : "+t.boneAxis);
			}
			Quaternion r = turretBone.getWorldBindRotation()
					.mult(new Quaternion().fromAngles((float) (actor.getRollFix()), (float) (actor.getRollFix()), (float) (actor.getPitchFix())))
					.mult(new Quaternion().fromAngleAxis((float) t.yaw, axis));

			turretBone.setUserControl(true);
			turretBone.setUserTransforms(Vector3f.ZERO, r, Vector3f.UNIT_XYZ);
		}
	}

	private void updateBoneCoords(ModelActor actor) {
		AnimControl ctrl = actor.getViewElements().spatial.getControl(AnimControl.class);
		if(ctrl == null)
			return;
		Skeleton sk = ctrl.getSkeleton();
		for (int i = 0; i < sk.getBoneCount(); i++) {
			Bone b = sk.getBone(i);
			actor.setBone(b.getName(), getBoneWorldPos(actor, i));
		}
	}

	private Point3D getBoneWorldPos(ModelActor actor, String boneName) {
		return getBoneWorldPos(actor, actor.getPos(), actor.getYaw(), boneName);
	}

	private Point3D getBoneWorldPos(ModelActor actor, int boneIndex) {
		return getBoneWorldPos(actor, actor.getViewElements().spatial.getControl(AnimControl.class).getSkeleton().getBone(boneIndex).getName());
	}

	private Point3D getBoneWorldPos(ModelActor actor, Point3D actorPos, double actorYaw, String boneName) {
		Spatial s = actor.getViewElements().spatial;
		Vector3f modelSpacePos = s.getControl(AnimControl.class).getSkeleton().getBone(boneName).getModelSpacePosition();
		Quaternion q = actor.getViewElements().spatial.getLocalRotation();
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
