/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity;

import geometry.geom3d.Point3D;

import java.util.ArrayList;

import model.battlefield.army.ArmyManager;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.effects.DamageEffect;
import model.battlefield.army.effects.Effect;
import model.battlefield.army.effects.EffectSource;
import model.battlefield.army.effects.EffectTarget;
import model.battlefield.army.effects.LauncherEffect;
import model.battlefield.army.effects.PersistentEffect;
import model.builders.entity.definitions.BuilderManager;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class EffectBuilder extends Builder {
	private static final String TYPE = "Type";
	private static final String EFFECT_LINK_LIST = "EffectLinkList";
	private static final String TYPE_DAMAGE = "Damage";
	private static final String TYPE_PERSISTENT = "Persistent";
	private static final String TYPE_LAUNCHER = "Launcher";
	private static final String AMOUNT = "Amount";
	private static final String PERIOD_COUNT = "PeriodCount";
	private static final String PERIOD_DURATION_LIST = "DurationList";
	private static final String PERIOD_RANGE_LIST = "RangeList";
	private static final String PROJECTILE_LINK = "ProjectileLink";

	private String type = null;
	private ArrayList<String> effectBuildersID = new ArrayList<>();
	private ArrayList<EffectBuilder> effectBuilders = new ArrayList<>();
	private int amount;
	private int periodCount;
	private ArrayList<Double> durations = new ArrayList<>();
	private ArrayList<Double> ranges = new ArrayList<>();
	private String projectileLink = null;

	public EffectBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case TYPE:
					type = de.getVal();
					break;
				case EFFECT_LINK_LIST:
					effectBuildersID.add(de.getVal());
					break;
				case AMOUNT:
					amount = de.getIntVal();
					break;
				case PERIOD_COUNT:
					periodCount = de.getIntVal();
					break;
				case PERIOD_DURATION_LIST:
					durations.add(de.getDoubleVal() * 1000);
					break;
				case PERIOD_RANGE_LIST:
					ranges.add(de.getDoubleVal() * 1000);
					break;
				case PROJECTILE_LINK:
					projectileLink = de.getVal();
					break;
			}
		}
	}

	public Effect build(EffectSource source, EffectTarget target, Point3D targetPoint) {
		Projectile projectile = null;
		if (projectileLink != null) {
			projectile = BuilderManager.getProjectileBuilder(projectileLink).build(source, target, targetPoint);
			projectile.drawOnBattlefield();
		}

		Effect res;
		switch (type) {
			case TYPE_DAMAGE:
				res = new DamageEffect(amount, effectBuilders, source, target);
				break;
			case TYPE_PERSISTENT:
				res = new PersistentEffect(periodCount, durations, ranges, effectBuilders, source, target);
				ArmyManager.addPersistentEffect((PersistentEffect) res);
				break;
			case TYPE_LAUNCHER:
				res = new LauncherEffect(projectile, effectBuilders, source, target);
				break;
			default:
				printUnknownValue(TYPE, type);
				throw new RuntimeException();
		}
		return res;
	}

	@Override
	public void readFinalizedLibrary() {
		for (String s : effectBuildersID) {
			effectBuilders.add(BuilderManager.getEffectBuilder(s));
		}
	}

}
