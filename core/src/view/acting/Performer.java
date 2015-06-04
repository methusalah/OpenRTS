package view.acting;

import model.battlefield.actors.Actor;

public abstract class Performer {
	ActorDrawer actorDrawer;
	
	public Performer(ActorDrawer bs){
		this.actorDrawer = bs;
	}
	
	public abstract void perform(Actor a);

}
