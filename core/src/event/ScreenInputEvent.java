package event;

import geometry.geom2d.Point2D;

import com.jme3.network.serializing.Serializable;

/**
 * this is a Map with MapCoordinates.
 *
 * @author Mario
 */
@Serializable
public class ScreenInputEvent extends ToServerEvent {

	private String command;
	private double x;
	private double y;

	private Boolean isPressed;

	public ScreenInputEvent() {

	}

	public ScreenInputEvent(String command, Point2D coord, Boolean isPressed) {
		this.command = command;
		this.x = coord.getX();
		this.y = coord.getY();
		this.isPressed = isPressed;

	}

	public String getCommand() {
		return command;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Point2D getCoord() {
		return new Point2D(x, y);
	}

	public Boolean getIsPressed() {
		return isPressed;
	}

}
