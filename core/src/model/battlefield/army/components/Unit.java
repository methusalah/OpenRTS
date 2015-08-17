package model.battlefield.army.components;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.battlefield.abstractComps.FieldComp;
import model.battlefield.abstractComps.GroundHiker;
import model.battlefield.actors.ModelActor;
import model.battlefield.army.Group;
import model.battlefield.army.effects.EffectSource;
import model.battlefield.army.effects.EffectTarget;
import model.battlefield.army.tacticalAI.TacticalAI;
import model.battlefield.warfare.Faction;
import model.builders.entity.MoverBuilder;
import model.builders.entity.TurretBuilder;
import model.builders.entity.WeaponBuilder;
import model.builders.entity.actors.ModelActorBuilder;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A special Hicker that can move freely on the battle field and interact with other The role of this class is to put in common three subsystems : - a mover to
 * achieve motion over the battlefield and trhought other units - a tactical AI to take decisions - an arming to launch effects It uses a model actor to be
 * drawn on the view. It is defined by XML and is only instanciated by associate builder.
 */
public class Unit extends GroundHiker implements EffectSource, EffectTarget {
	private static final Logger logger = Logger.getLogger(Mover.class.getName());

	public enum STATE {
		MOVING, AIMING, IDLING, DESTROYED, STUCK
	};

	// final data
	public final String UIName;
	public final String builderID;
	public final String race;
	public final int maxHealth;
	public final ModelActor actor;
	public final Arming arming;
	public final TacticalAI ai;

	// variables
	public Faction faction;
	public Group group = new Group();
	public int health;
	public STATE state = STATE.IDLING;
	public boolean selected = false;

	public Unit(double radius,
			double speed,
    		double acceleration,
    		double deceleration,
    		double stationnaryRotationSpeed,
    		double turningRate,
			Point3D pos,
			double yaw,
			MoverBuilder moverBuilder,
			String UIName,
			String BuilderID,
			String race,
			int maxHealth,
			Faction faction,
			ModelActorBuilder actorBuilder) {
		super(pos, yaw, radius, speed, acceleration, deceleration, stationnaryRotationSpeed, turningRate, moverBuilder);
		this.UIName = UIName;
		this.builderID = BuilderID;
		this.race = race;
		this.maxHealth = maxHealth;
		ai = new TacticalAI(this);
		arming = new Arming(this);
		setFaction(faction);
		health = maxHealth;
		actor = actorBuilder.build(this);
	}

	private void setFaction(Faction faction) {
		if (this.faction != null) {
			this.faction.getUnits().remove(this);
		}
		this.faction = faction;
		faction.getUnits().add(this);
	}

	public void update(double elapsedTime) {
		boolean aiming, moving;

		if (destroyed()) {
			return;
		}
		findNearbyMovers();
		arming.updateWeapons();

		ai.update();

		aiming = arming.isAiming();
		actor.onAim(aiming);

		mover.updatePosition(elapsedTime);

		moving = mover.hasMoved;
		actor.onMove(moving);

		arming.updateTurrets(elapsedTime, moving);

		actor.onWait(!aiming && !moving);

	}

    public void head(Point2D target) {
    	mover.desiredOrientation = getAngleTo(target);
    }
    
    public boolean heading(Point2D target, double toleranceInDegrees){
    	return AngleUtil.getSmallestDifference(getAngleTo(target), getOrientation()) <= AngleUtil.toRadians(toleranceInDegrees);
    }
    
    private double getAngleTo(Point2D p){
    	return p.getSubtraction(getCoord()).getAngle();
    }
    
	public void idle() {
		state = STATE.IDLING;
	}

	private void findNearbyMovers() {
		mover.toFlockWith.clear();
		for (Unit u : group) {
			if (u != this && u.getMover().hasDestination()) {
				mover.toFlockWith.add(u.getMover());
			}
		}

		mover.toLetPass.clear();
		for (Unit u : faction.getUnits()) {
			if (u != this && getBoundsDistance(u) <= 0 && u.mover.heightmap.equals(mover.heightmap)) {
				mover.toLetPass.add(u.mover);
			}
		}

		mover.toAvoid.clear();
		mover.toAvoid = getBlockers();
		mover.addTrinketsToAvoidingList();
	}

	private List<FieldComp> getBlockers() {
		List<FieldComp> res = new ArrayList<>();
		for (Faction f : faction.getEnemies()) {
			for (Unit u : f.getUnits()) {
				res.add(u);
			}
		}

		for (Unit u : faction.getUnits()) {
			if (u != this && u.mover.holdPosition) {
				res.add(u);
			}
		}
		return res;
	}

	private void destroy() {
		state = STATE.DESTROYED;
		actor.onMove(false);
		actor.onAim(false);
		actor.onWait(false);
		actor.onDestroyedEvent();
		actor.stopActing();
	}

	public void removeFromBattlefield() {
		state = STATE.DESTROYED;
		actor.stopActingAndChildren();
	}
	public void drawOnBattlefield() {
		actor.act();
	}

	public boolean destroyed() {
		return state == STATE.DESTROYED;
	}

	public double getHealthRate() {
		return (double) health / maxHealth;
	}

	public Mover getMover() {
		return mover;
	}

	public ArrayList<Turret> getTurrets() {
		return arming.turrets;
	}

	public void addWeapon(WeaponBuilder weaponBuilder, TurretBuilder turretBuilder) {
		Turret t = null;
		if (turretBuilder != null) {
			t = turretBuilder.build(this);
			arming.turrets.add(t);
		}
		arming.weapons.add(weaponBuilder.build(this, t));
	}

	@Override
	public boolean isStillActiveSource() {
		return !destroyed();
	}

	@Override
	public void damage(EffectSource source, int amount) {
		if (destroyed()) {
			return;
		}
		health -= amount;
		if (health <= 0) {
			destroy();
		}

		ai.registerAsAttacker(source.getUnit());
	}

	@Override
	public Unit getUnit() {
		return this;
	}

	// @Override
	public Unit getNearest(Unit o1, Unit o2) {
		return (Unit) super.getNearest(o1, o2);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("UIName", UIName).toString();
	}

	@Override
	public double getYaw() {
		return getOrientation();
	}
}
