package model.battlefield.actors;

import java.util.List;

import model.builders.entity.actors.ActorBuilder;

/**
 * Can play a named animation of the first Model Actor parent it finds.
 */
public class AnimationActor extends Actor {
	public enum Cycle {
		Once, Loop, Cycle
	};

	public final String animName;
	public final Cycle cycle;
	public final double speed;

	public boolean launched = false;

	public AnimationActor(Actor parent, String trigger, List<String> childrenTriggers, List<ActorBuilder> childrenBuilders, String animName, Cycle cycle,
			double speed) {
		super(parent, trigger, childrenTriggers, childrenBuilders);
		this.animName = animName;
		this.cycle = cycle;
		this.speed = speed;
	}

	@Override
	public void act() {
		if (!acting) {
			launched = false;
		}
		super.act();
	}

	@Override
	public void stopActing() {
		super.stopActing();
	}

	public ModelActor getParentModelActor() {
		Actor parent = this;
		do {
			parent = parent.getParent();
			if (parent == null) {
				throw new RuntimeException("AnimationActor seems to miss a modelActor parent");
			}

		} while (!parent.containsModel());
		return (ModelActor) parent;
	}

	@Override
	public String getType() {
		return "animation";
	}

}
