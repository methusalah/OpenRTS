package event;

public class ProjectileArrivedEvent extends Event {
	
	private final boolean targetReached;

	public ProjectileArrivedEvent(boolean targetReached){
		this.targetReached = targetReached;
	}
	
	public boolean targetReached(){
		return targetReached;
	}
	
}
