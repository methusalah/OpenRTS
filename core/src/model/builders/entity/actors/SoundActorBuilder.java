package model.builders.entity.actors;

import model.battlefield.actors.Actor;
import model.battlefield.actors.SoundActor;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class SoundActorBuilder extends ActorBuilder {
	private static final String SOUND_PATH = "SoundPath";
	private static final String VOLUME = "Volume";
	private static final String LOOPING = "Looping";
	private static final String POSITIONAL = "Positional";

	private String soundPath;
	private double volume = 1;
	private boolean positional = false;
	private boolean looping = false;

	public SoundActorBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case TYPE:
				case TRIGGER:
				case ACTOR_LIST:
					break;
				case SOUND_PATH:
					soundPath = de.getVal();
					break;
				case VOLUME:
					volume = de.getDoubleVal();
					break;
				case LOOPING:
					looping = de.getBoolVal();
					break;
				case POSITIONAL:
					positional = de.getBoolVal();
					break;
				default:
					printUnknownElement(de.name);
			}
		}
	}

	@Override
	public Actor build(String trigger, Actor parent) {
		Actor res = new SoundActor(parent, trigger, childrenTriggers, childrenActorBuilders, soundPath, looping,
				volume, positional);
		res.debbug_id = getId();
		return res;
	}
}
