package model.builders.entity.actors;

import model.battlefield.actors.Actor;
import model.battlefield.actors.AnimationActor;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class AnimationActorBuilder extends ActorBuilder {
	private static final String ANIMATION_NAME = "AnimName";
	private static final String SPEED = "Speed";
	private static final String CYCLE = "Cycle";
	private static final String CYCLE_ONCE = "Once";
	private static final String CYCLE_LOOP = "Loop";
	private static final String CYCLE_CYCLE = "Cycle";

	private String animationName;
	private double speed;
	private AnimationActor.Cycle cycle;

	public AnimationActorBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case TYPE:
				case TRIGGER:
				case ACTOR_LIST:
					break;
				case ANIMATION_NAME:
					animationName = de.getVal();
					break;
				case SPEED:
					speed = de.getDoubleVal();
					break;
				case CYCLE:
					switch (de.getVal()) {
						case CYCLE_ONCE:
							cycle = AnimationActor.Cycle.Once;
							break;
						case CYCLE_LOOP:
							cycle = AnimationActor.Cycle.Loop;
							break;
						case CYCLE_CYCLE:
							cycle = AnimationActor.Cycle.Cycle;
							break;
					}
					break;
				default:
					printUnknownElement(de.name);
			}
		}
	}

	@Override
	public Actor build(String trigger, Actor parent) {
		Actor res = new AnimationActor(parent, trigger, childrenTriggers, childrenActorBuilders, animationName, cycle, speed);
		res.debbug_id = getId();
		return res;
	}
}
