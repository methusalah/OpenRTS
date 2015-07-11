package model.battlefield.actors;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.builders.entity.actors.ActorBuilder;
import view.acting.ActorViewElements;

/**
 * Actors are visual representations. If you want to draw something on the view, you simply instanciate an Actor and ask it to play. An Actor is sensitive to a
 * trigger. When it receives that trigger, it can play once or continuously. An Actor may have children. When it receive a trigger, it send it to all its
 * children. Actors play and stop according to the trigger they receive, by registering themselves into the actor pool
 */
public class Actor {
	public final static String TYPE = "Actor";

	protected static final String ON_MOVE = "onMove";
	protected static final String ON_WAIT = "onWait";
	protected static final String ON_AIM = "onAim";
	protected static final String ON_SHOOT = "onShoot";
	protected static final String ON_DESTROYED = "onDestroyed";
	protected static final String ON_EXPLODED = "onExploded";
	protected static final String ON_ALL_TIME = "onAllTime";

	protected final Actor parent;
	protected final String trigger;
	protected final List<Actor> children;

	public String debbug_id = "id not configured";

	protected final ActorViewElements viewElements = new ActorViewElements();

	protected boolean acting = false;

	public Actor(Actor parent, String trigger, List<String> childrenTriggers, List<ActorBuilder> childrenBuilders) {
		this.parent = parent;
		this.trigger = trigger;
		children = new ArrayList<>();
		int i = 0;
		for (ActorBuilder b : childrenBuilders) {
			children.add(b.build(childrenTriggers.get(i), this));
			i++;
		}
	}

	public void onMove(boolean cond) {
		if (cond) {
			activateTrigger(ON_MOVE);
		} else {
			desactivateTrigger(ON_MOVE);
		}
	}

	public void onWait(boolean cond) {
		if (cond) {
			activateTrigger(ON_WAIT);
		} else {
			desactivateTrigger(ON_WAIT);
		}
	}

	public void onAim(boolean cond) {
		if (cond) {
			activateTrigger(ON_AIM);
		} else {
			desactivateTrigger(ON_AIM);
		}
	}

	public void onShootEvent() {
		desactivateTrigger(ON_SHOOT);
		activateTrigger(ON_SHOOT);
	}

	public void onDestroyedEvent() {
		desactivateTrigger(ON_DESTROYED);
		activateTrigger(ON_DESTROYED);
	}

	private void activateTrigger(String trigger) {
		if (this.trigger.equals(trigger)) {
			act();
		}
		for (Actor a : children) {
			a.activateTrigger(trigger);
		}
	}

	private void desactivateTrigger(String trigger) {
		if (this.trigger.equals(trigger)) {
			stopActing();
		}
		for (Actor a : children) {
			a.desactivateTrigger(trigger);
		}
	}

	public void act() {
		if (acting || ModelManager.getBattlefield() == null) {
			return;
		}
		acting = true;
		ModelManager.getBattlefield().getActorPool().registerActor(this);
	}

	public void stopActing() {
		acting = false;
		ModelManager.getBattlefield().getActorPool().deleteActor(this);
	}

	public void stopActingAndChildren() {
		stopActing();
		for (Actor child : children) {
			child.stopActing();
		}
	}

	public Actor getParent() {
		return parent;
	}

	public boolean containsModel() {
		return false;
	}

	public String getType() {
		return "default";
	}

	@Override
	public String toString() {
		return "(" + getClass().getSimpleName() + ")" + debbug_id;
	}

	public ActorViewElements getViewElements() {
		return viewElements;
	}

	// public boolean isActing(){
	// if(acting)
	// return true;
	// for(Actor a : children)
	// if(a.isActing())
	// return true;
	// return false;
	// }
}
