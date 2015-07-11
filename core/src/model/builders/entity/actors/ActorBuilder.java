/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity.actors;

import geometry.math.RandomUtil;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.actors.Actor;
import model.builders.entity.Builder;
import model.builders.entity.definitions.BuilderManager;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class ActorBuilder extends Builder {
	public static final String TYPE = "Type";

	public static final String TYPE_MODEL = "Model";
	public static final String TYPE_PARTICLE = "Particle";
	public static final String TYPE_ANIMATION = "Animation";
	public static final String TYPE_PHYSIC = "Physic";
	public static final String TYPE_SOUND = "Sound";

	protected static final String ACTOR_LIST = "ActorList";
	protected static final String TRIGGER = "Trigger";
	protected static final String PROB = "Prob";
	protected static final String ACTOR_LINK = "ActorLink";

	protected String type;
	protected List<String> childrenActorBuildersID = new ArrayList<>();
	protected List<ActorBuilder> childrenActorBuilders = new ArrayList<>();
	protected List<String> childrenTriggers = new ArrayList<>();
	protected List<Double> childrenProbs = new ArrayList<>();

	public ActorBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case TYPE:
					type = de.getVal();
					break;
				case ACTOR_LIST:
					childrenActorBuildersID.add(de.getVal(ACTOR_LINK));
					childrenTriggers.add(de.getVal(TRIGGER));
					if (de.getVal(PROB) != null) {
						childrenProbs.add(de.getDoubleVal(PROB));
					} else {
						childrenProbs.add(1d);
					}
					break;
			}
		}
	}

	public Actor build(String trigger, Actor parent) {
		List<ActorBuilder> localChildrenActorBuilders = new ArrayList<>();
		List<String> localChildrenTriggers = new ArrayList<>();

		int i = 0;
		for (ActorBuilder b : childrenActorBuilders) {
			if (RandomUtil.next() < childrenProbs.get(i)) {
				localChildrenActorBuilders.add(b);
				localChildrenTriggers.add(childrenTriggers.get(i));
			}
			i++;
		}

		Actor res = new Actor(parent, trigger, localChildrenTriggers, localChildrenActorBuilders);
		res.debbug_id = getId();
		return res;
	}

	@Override
	public void readFinalizedLibrary() {
		for (String s : childrenActorBuildersID) {
			childrenActorBuilders.add(BuilderManager.getActorBuilder(s));
		}
	}

}
