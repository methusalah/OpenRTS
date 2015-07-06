package event;

import geometry.geom3d.Point3D;

import com.jme3.network.serializing.Serializable;

/**
 * this is a Map with MapCoordinates.
 *
 * @author Mario
 */
@Serializable
public class MapInputEvent extends ToServerEvent {

	private String command;
	private float x;
	private float y;
	private float z;

	private Boolean isPressed;

	public MapInputEvent() {

	}

	public MapInputEvent(String command, Point3D origin, Boolean isPressed) {
		this.command = command;
		this.x = (float) origin.x;
		this.y = (float) origin.y;
		this.z = (float) origin.z;

		this.isPressed = isPressed;

	}

	public String getCommand() {
		return command;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public Boolean getIsPressed() {
		return isPressed;
	}

}
