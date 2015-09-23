package brainless.openrts.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


class Game {
	
	private File file;
	
	private State state = State.OPEN;
	private List<Player> players = new ArrayList<Player>();
	private Player mySelf = new Player();
	
	
	enum State {
		OPEN, RUNNING, PAUSED, STOPPED
	}

}
