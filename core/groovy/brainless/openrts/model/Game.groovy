package brainless.openrts.model


class Game {
	
	File file
	
	State state = State.OPEN
	List<Player> players = []
	Player mySelf
	
	
	enum State {
		OPEN, RUNNING, PAUSED, STOPPED
	}

}
