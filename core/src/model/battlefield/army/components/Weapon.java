package model.battlefield.army.components;

import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import geometry.math.PrecisionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.battlefield.actors.Actor;
import model.battlefield.army.effects.Effect;
import model.battlefield.army.effects.EffectSource;
import model.builders.entity.EffectBuilder;
import model.builders.entity.actors.ActorBuilder;

/**
 * Weapons have two roles : - find and acquire the best available target - launch the chain of effects whenever it is possible For now, weapons choose
 * themselves the best target for aggression only. This may change if weapon become responsible to cast beneficial effects to allies. It is defined by XML and
 * is only instanciated by associate builder.
 */
public class Weapon implements EffectSource {
	private static final Logger logger = Logger.getLogger(Mover.class.getName());
	// final
	public final String UIName;
	public final double range;
	public final double scanRange;
	public final double period;
	public final EffectBuilder effectBuilder;
	public final String sourceBone;
	public final String directionBone;
	public final boolean allowMovement = false;
	final Unit holder;
	final Actor actor;

	protected final Turret turret;
	private final boolean turretMounted;

	// variables
	Point3D turretPivot;
	Point3D sourcePoint;
	Point3D directionVector;
	private Unit target;
	public double lastStrikeTime = 0;
	boolean attacking = false;

	protected List<Unit> onScan = new ArrayList<Unit>();
	protected List<Unit> atRange = new ArrayList<Unit>();

	public Weapon(String UIName, double range, double scanRange, double period, EffectBuilder effectBuilder, String sourceBone, String directionBone,
			Unit holder, ActorBuilder actorBuilder, Turret turret) {
		this.UIName = UIName;
		this.range = range;
		this.scanRange = scanRange;
		this.period = period;
		this.effectBuilder = effectBuilder;
		this.sourceBone = sourceBone;
		this.directionBone = directionBone;
		this.holder = holder;
		if (actorBuilder != null) {
			this.actor = actorBuilder.build("", holder.actor);
		} else {
			this.actor = null;
		}
		this.turret = turret;
		turretMounted = turret != null;
	}

	public void update(List<Unit> enemiesNearby) {
		if (sourceBone != null && holder.actor.hasBone()) {
			turretPivot = holder.actor.getBoneCoord(turret.boneName);
			sourcePoint = holder.actor.getBoneCoord(sourceBone);
			directionVector = holder.actor.getBoneCoord(directionBone).getSubtraction(sourcePoint).getNormalized();
		} else {
			sourcePoint = turretPivot = holder.getPos();
			directionVector = Point3D.UNIT_Z; // Point2D.ORIGIN.getTranslation(holder.getYaw(), 1).get3D(0);
		}

		attacking = false;
		onScan.clear();
		atRange.clear();
		for (Unit u : enemiesNearby) {
			if (isAtRange(u)) {
				atRange.add(u);
			}
			if (isAtScanRange(u)) {
				onScan.add(u);
			}
		}
		chooseTarget();
	}

	private void chooseTarget() {
		target = null;
		// search best target at range
		for (Unit u : atRange) {
			if (target == null) {
				target = u;
			} else {
				double healthDiff = u.getHealthRate() - target.getHealthRate();
				if (healthDiff < 0 || healthDiff < PrecisionUtil.APPROX && holder.getDistance(u) < holder.getDistance(target)) {
					target = u;
				}
			}
		}
		// if no target found, search best target on scan
		if (target == null) {
			for (Unit u : onScan) {
				target = target == null ? u : holder.getNearest(u, target);
			}
		}
	}

	public void attack(Unit specificTarget) {
		if (!isAtRange(specificTarget)) {
			throw new RuntimeException("specific target not in range");
		}
		target = specificTarget;
		attack();
	}

	public void attack() {
		attacking = true;
		if (target == null) {
			throw new RuntimeException("no target");
		}
		if (!isAtRange(target)) {
			throw new RuntimeException("target not in range");
		}

		// holder.getMover().tryToHoldPositionHardly();
		setDesiredYaw();

		boolean ready = true;
		// if(!holder.getMover().holdPosition)
		// ready = false;

		if (turretMounted){
			if(!turret.heading(target.getCoord(), 3)){
				ready = false;
			}
		} else if(!holder.heading(target.getCoord(), 3))
			ready = false;

		if (lastStrikeTime + 1000 * period > System.currentTimeMillis()) {
			ready = false;
		}

		if (ready) {
			if (actor != null) {
				actor.onShootEvent();
			}
			target.ai.registerAsAttacker(holder);
			Effect e = effectBuilder.build(this, target, null);
			e.launch();

			lastStrikeTime = System.currentTimeMillis();
		}
	}

	private void setDesiredYaw() {
		if (turretMounted) {
			turret.head(target.getCoord());
		} else {
			holder.head(target.getCoord());
		}
	}

	public boolean hasTargetAtRange(Unit specificTarget) {
		return isAtRange(specificTarget);
	}

	private boolean isAtRange(Unit u) {
		return u.getBoundsDistance(holder) <= range;
	}

	private boolean isAtScanRange(Unit u) {
		return u.getBoundsDistance(holder) <= scanRange;
	}

	public boolean isAttacking() {
		return attacking;
	}

	public Unit getTarget() {
		return target;
	}

	public boolean isAtRange() {
		return !atRange.isEmpty();
	}

	public boolean scanning() {
		return !onScan.isEmpty();
	}

	@Override
	public boolean isStillActiveSource() {
		return !holder.destroyed();
	}

	@Override
	public Point3D getPos() {
		return sourcePoint;
	}

	@Override
	public Point3D getDirection() {
		return directionVector;
	}

	@Override
	public Unit getUnit() {
		return holder;
	}

	@Override
	public double getYaw() {
		if (sourcePoint == null) {
			return holder.getYaw();
		}
		return directionVector.getSubtraction(sourcePoint).get2D().getAngle();
	}

}
