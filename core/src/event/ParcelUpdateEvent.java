package event;

import java.util.List;

import model.battlefield.map.parcelling.Parcel;

public class ParcelUpdateEvent extends Event {

	private final List<Parcel> toUpdate;

	public ParcelUpdateEvent(List<Parcel> toUpdate) {
		super();
		this.toUpdate = toUpdate;
	}

	public List<Parcel> getToUpdate() {
		return toUpdate;
	}

}
