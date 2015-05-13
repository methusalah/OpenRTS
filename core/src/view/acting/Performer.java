package view.acting;

import model.battlefield.actors.Actor;

public abstract class Performer {
	Backstage bs;
	
	public Performer(Backstage bs){
		this.bs = bs;
	}
	
	public abstract void perform(Actor a);

}
