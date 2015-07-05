package event;

import java.util.List;

import model.battlefield.army.components.Unit;

public class WorldChangedEvent extends ServerEvent {

	private List<Unit> units;

	public WorldChangedEvent(List<Unit> changedUnits) {
		this.units = changedUnits;
	}

	public List<Unit> getUnits() {
		return units;
	}

}
