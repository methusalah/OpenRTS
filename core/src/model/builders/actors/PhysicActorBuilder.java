/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.actors;

import model.ModelManager;
import model.battlefield.abstractComps.Hiker;
import model.battlefield.actors.Actor;
import model.battlefield.actors.PhysicActor;
import model.builders.definitions.DefElement;
import model.builders.definitions.Definition;

/**
 * @author Benoît
 */
public class PhysicActorBuilder extends ActorBuilder {
	private static final String MODEL_PATH = "ModelPath";
	private static final String SCALE = "Scale";
	private static final String MASS = "Mass";
	private static final String LIFE = "Life";
	private static final String MASS_CENTER_BONE = "MassCenterBone";

	private String modelPath;
	private double scale = 1;
	private double mass = 1;
	private double life = 1;
	private String massCenterBone;

	public PhysicActorBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case TYPE:
				case TRIGGER:
				case ACTOR_LIST:
					break;
				case MODEL_PATH:
					modelPath = de.getVal();
					break;
				case SCALE:
					scale = de.getDoubleVal();
					break;
				case LIFE:
					life = de.getDoubleVal() * 1000;
					break;
				case MASS:
					mass = de.getDoubleVal();
					break;
				case MASS_CENTER_BONE:
					massCenterBone = de.getVal();
					break;
				default:
					printUnknownElement(de.name);
			}
		}
	}

	@Override
	public Actor build(String trigger, Actor parent) {
		return build(trigger, null, parent);
	}

	public Actor build(String trigger, Hiker movable, Actor parent) {
		Actor res = new PhysicActor(modelPath, scale, life, mass, massCenterBone, parent, trigger, childrenTriggers, childrenActorBuilders, ModelManager
				.getBattlefield().getActorPool());
		res.debbug_id = getId();
		return res;
	}
}
