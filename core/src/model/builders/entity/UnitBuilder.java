/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity;

import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.army.components.Unit;
import model.battlefield.warfare.Faction;
import model.builders.entity.actors.ModelActorBuilder;
import model.builders.entity.definitions.BuilderManager;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class UnitBuilder extends Builder {
	private static final String RADIUS = "Radius";
	private static final String SPEED = "Speed";
	private static final String ACCELERATION = "Acceleration";
	private static final String DECELERATION = "Deceleration";
	private static final String STATIONARY_ROTATION_SPEED = "StationaryRotationSpeed";
	private static final String TURNING_RATE = "TurningRate";
	private static final String MASS = "Mass";
	private static final String MOVER_LINK = "MoverLink";

	private static final String UINAME = "UIName";
	private static final String RACE = "Race";
	private static final String MAXHEALTH = "MaxHealth";

	private static final String WEAPONLIST = "WeaponList";
	private static final String TURRET_LINK = "TurretLink";
	private static final String WEAPON_LINK = "WeaponLink";
	private static final String ACTOR_LINK = "ActorLink";

	private String UIName;
	private String race;
	private int maxHealth;
	private String actorBuilderID;
	private ModelActorBuilder actorBuilder;
	private double radius;
	private double speed;
	private double mass = 1;
	private double acceleration = 1000;
	private double deceleration = 1000;
	private double stationnaryRotationSpeed = AngleUtil.toRadians(720);
	private double turningRate = AngleUtil.toRadians(720);
	private String moverBuilderID;
	private MoverBuilder moverBuilder;
	private List<String> weaponBuildersID = new ArrayList<>();
	private List<WeaponBuilder> weaponBuilders = new ArrayList<>();
	private List<String> turretBuildersID = new ArrayList<>();
	private List<TurretBuilder> turretBuilders = new ArrayList<>();

	public UnitBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case RADIUS:
					radius = de.getDoubleVal();
					break;
				case SPEED:
					speed = de.getDoubleVal();
					break;
				case ACCELERATION:
					acceleration = de.getDoubleVal();
					break;
				case DECELERATION:
					deceleration = de.getDoubleVal();
					break;
				case STATIONARY_ROTATION_SPEED:
					stationnaryRotationSpeed = AngleUtil.toRadians(de.getDoubleVal());
					break;
				case TURNING_RATE:
					turningRate = AngleUtil.toRadians(de.getDoubleVal());
					break;
				case MASS:
					mass = de.getDoubleVal();
					break;
				case MOVER_LINK:
					moverBuilderID = de.getVal();
					break;
				case UINAME:
					UIName = de.getVal();
					break;
				case RACE:
					race = de.getVal();
					break;
				case MAXHEALTH:
					maxHealth = de.getIntVal();
					break;
				case ACTOR_LINK:
					actorBuilderID = de.getVal();
					break;
				case WEAPONLIST:
					weaponBuildersID.add(de.getVal(WEAPON_LINK));
					turretBuildersID.add(de.getVal(TURRET_LINK));
					break;
			}
		}
	}

	public Unit build(Faction faction, Point3D pos, double yaw) {
		Unit res = new Unit(radius, speed, acceleration, deceleration, stationnaryRotationSpeed, turningRate, pos, yaw, moverBuilder, UIName, getId(), race, maxHealth, faction, actorBuilder);

		int i = 0;
		for (WeaponBuilder wb : weaponBuilders) {
			TurretBuilder tb = null;
			if (turretBuilders.get(i) != null) {
				tb = turretBuilders.get(i);
			}
			res.addWeapon(wb, tb);
			i++;
		}

		return res;
	}

	public boolean hasRace(String race) {
		return this.race.equals(race);
	}

	public String getUIName() {
		return UIName;
	}

	@Override
	public void readFinalizedLibrary() {
		actorBuilder = (ModelActorBuilder) BuilderManager.getActorBuilder(actorBuilderID);
		moverBuilder = BuilderManager.getMoverBuilder(moverBuilderID);
		int i = 0;
		for (String s : weaponBuildersID) {
			weaponBuilders.add(BuilderManager.getWeaponBuilder(s));
			if (turretBuildersID.get(i) == null) {
				turretBuilders.add(null);
			} else {
				turretBuilders.add(BuilderManager.getTurretBuilder(turretBuildersID.get(i)));
			}
			i++;
		}
	}
}
