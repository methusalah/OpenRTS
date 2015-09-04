package brainless.openrts.model


class Game {
	
	File file
	
	State state = State.OPEN
	List<Player> players = []
	
	
	enum State {
		OPEN, RUNNING, PAUSED, STOPPED
	}

}
