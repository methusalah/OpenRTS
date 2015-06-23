package model.battlefield.actors;

import java.util.List;

import model.builders.entity.actors.ActorBuilder;

public class SoundActor extends Actor {

	public final String soundPath;
	public final boolean looping;
	public final double volume;
	public final boolean positional;

	public SoundActor(Actor parent,
			String trigger,
			List<String> childrenTriggers,
			List<ActorBuilder> childrenBuilders,
			String soundPath,
			boolean looping,
			double volume,
			boolean positional) {
		super(parent, trigger, childrenTriggers, childrenBuilders);
		this.soundPath = soundPath;
		this.looping = looping;
		this.volume = volume;
		this.positional = positional;
	}

	public ModelActor getParentModelActor(){
		Actor parent = this;
		do {
			parent = parent.getParent();
			if(parent == null) {
				throw new RuntimeException("seems to miss a modelActor parent");
			}

		} while(!parent.containsModel());
		return (ModelActor)parent;
	}

	@Override
	public String getType() {
		return "sound";
	}
}
