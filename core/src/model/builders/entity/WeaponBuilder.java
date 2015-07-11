/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity;

import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.battlefield.army.components.Weapon;
import model.builders.entity.actors.ActorBuilder;
import model.builders.entity.definitions.BuilderManager;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

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
		for (DefElement element : def.getElements()) {
			switch (element.name) {
				case UINAME:
					UIName = element.getVal();
					break;
				case RANGE:
					range = element.getDoubleVal();
					break;
				case SCAN_RANGE:
					scanRange = element.getDoubleVal();
					break;
				case PERIOD:
					period = element.getDoubleVal();
					break;
				case EFFECT_LINK:
					effectBuilderID = element.getVal();
					break;
				case ACTOR_LINK:
					actorBuilderID = element.getVal();
					break;
				case SOURCE_BONE:
					sourceBone = element.getVal();
					break;
				case DIRECTION_BONE:
					directionBone = element.getVal();
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
