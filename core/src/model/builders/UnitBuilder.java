/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders;

import geometry.geom3d.Point3D;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.army.components.Unit;
import model.battlefield.warfare.Faction;
import model.builders.actors.ModelActorBuilder;
import model.builders.definitions.BuilderLibrary;
import model.builders.definitions.DefElement;
import model.builders.definitions.Definition;

/**
 * @author Benoît
 */
public class UnitBuilder extends Builder {
	private static final String RADIUS = "Radius";
	private static final String SPEED = "Speed";
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
	private double mass;
	private String moverBuilderID;
	private MoverBuilder moverBuilder;
	private List<String> weaponBuildersID = new ArrayList<>();
	private List<WeaponBuilder> weaponBuilders = new ArrayList<>();
	private List<String> turretBuildersID = new ArrayList<>();
	private List<TurretBuilder> turretBuilders = new ArrayList<>();

	public UnitBuilder(Definition def, BuilderLibrary lib) {
		super(def, lib);
		for (DefElement de : def.elements) {
			switch (de.name) {
				case RADIUS:
					radius = de.getDoubleVal();
					break;
				case SPEED:
					speed = de.getDoubleVal();
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
		Unit res = new Unit(radius, speed, mass, pos, yaw, moverBuilder, UIName, getId(), race, maxHealth, faction, actorBuilder);

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
		actorBuilder = (ModelActorBuilder) lib.getActorBuilder(actorBuilderID);
		moverBuilder = lib.getMoverBuilder(moverBuilderID);
		int i = 0;
		for (String s : weaponBuildersID) {
			weaponBuilders.add(lib.getWeaponBuilder(s));
			if (turretBuildersID.get(i) == null) {
				turretBuilders.add(null);
			} else {
				turretBuilders.add(lib.getTurretBuilder(turretBuildersID.get(i)));
			}
			i++;
		}
	}
}
