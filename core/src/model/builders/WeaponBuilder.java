/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders;

import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.battlefield.army.components.Weapon;
import model.builders.actors.ActorBuilder;
import model.builders.definitions.BuilderManager;
import model.builders.definitions.DefElement;
import model.builders.definitions.Definition;

/**
 * @author Benoît
 */
public class WeaponBuilder extends Builder {
	private static final String UINAME = "UIName";
	private static final String RANGE = "Range";
	private static final String SCAN_RANGE = "ScanRange";
	private static final String PERIOD = "Period";
	private static final String EFFECT_LINK = "EffectLink";
	private static final String ACTOR_LINK = "ActorLink";
	private static final String SOURCE_BONE = "SourceBone";
	private static final String DIRECTION_BONE = "DirectionBone";

	private String UIName;
	private double range;
	private double scanRange;
	private double period;
	private String effectBuilderID;
	private EffectBuilder effectBuilder;
	private String actorBuilderID = "";
	private ActorBuilder actorBuilder = null;
	private String sourceBone;
	private String directionBone;

	public WeaponBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case UINAME:
					UIName = de.getVal();
					break;
				case RANGE:
					range = de.getDoubleVal();
					break;
				case SCAN_RANGE:
					scanRange = de.getDoubleVal();
					break;
				case PERIOD:
					period = de.getDoubleVal();
					break;
				case EFFECT_LINK:
					effectBuilderID = de.getVal();
					break;
				case ACTOR_LINK:
					actorBuilderID = de.getVal();
					break;
				case SOURCE_BONE:
					sourceBone = de.getVal();
					break;
				case DIRECTION_BONE:
					directionBone = de.getVal();
					break;
			}
		}
	}

	public Weapon build(Unit holder, Turret t) {
		Weapon res = new Weapon(UIName, range, scanRange, period, effectBuilder, sourceBone, directionBone, holder, actorBuilder, t);
		return res;
	}

	@Override
	public void readFinalizedLibrary() {
		effectBuilder = BuilderManager.getEffectBuilder(effectBuilderID);
		if (!actorBuilderID.isEmpty()) {
			actorBuilder = BuilderManager.getActorBuilder(actorBuilderID);
		}
	}

}
