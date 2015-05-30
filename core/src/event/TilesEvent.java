package event;

import java.util.List;

import model.battlefield.map.Tile;

public class TilesEvent extends Event {

	private final List<Tile> extended;

	public TilesEvent(List<Tile> extended) {
		super();
		this.extended = extended;
	}

	public List<Tile> getExtended() {
		return extended;
	}

}
