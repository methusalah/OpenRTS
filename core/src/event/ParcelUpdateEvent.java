package event;

import java.util.List;

import model.battlefield.map.parcel.ParcelMesh;

public class ParcelUpdateEvent extends NetworkEvent {

	private final List<ParcelMesh> toUpdate;

	public ParcelUpdateEvent(List<ParcelMesh> toUpdate) {
		super();
		this.toUpdate = toUpdate;
	}

	public List<ParcelMesh> getToUpdate() {
		return toUpdate;
	}

}
