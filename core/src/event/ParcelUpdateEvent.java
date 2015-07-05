package event;

import java.util.List;

import model.battlefield.map.parcelling.ParcelMesh;

public class ParcelUpdateEvent extends Event {

	private final List<ParcelMesh> toUpdate;

	public ParcelUpdateEvent(List<ParcelMesh> toUpdate) {
		super();
		this.toUpdate = toUpdate;
	}

	public List<ParcelMesh> getToUpdate() {
		return toUpdate;
	}

}
