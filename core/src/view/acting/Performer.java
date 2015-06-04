package view.acting;

import model.battlefield.actors.Actor;

public abstract class Performer {
	ActorDrawer bs;
	
	public Performer(ActorDrawer bs){
		this.bs = bs;
	}
	
	public abstract void perform(Actor a);

}
