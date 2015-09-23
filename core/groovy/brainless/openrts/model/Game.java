package brainless.openrts.model


class Game {
	
	File file
	
	State state = State.OPEN
	List<Player> players = []
	Player mySelf = new Player()
	
	
	enum State {
		OPEN, RUNNING, PAUSED, STOPPED
	}

}
