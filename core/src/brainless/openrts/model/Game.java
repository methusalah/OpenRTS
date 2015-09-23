package brainless.openrts.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Game {
	
	private File file;
	
	private State state = State.OPEN;
	private List<Player> players = new ArrayList<Player>();
	private Player mySelf = new Player();
	
	
	public File getFile() {
		return file;
	}


	public State getState() {
		return state;
	}


	public List<Player> getPlayers() {
		return players;
	}


	public Player getMySelf() {
		return mySelf;
	}


	public void setMySelf(Player mySelf) {
		this.mySelf = mySelf;
	}


	public void setFile(File file) {
		this.file = file;
	}


	enum State {
		OPEN, RUNNING, PAUSED, STOPPED
	}

}
