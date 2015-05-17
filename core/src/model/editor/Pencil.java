/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor;

import geometry.collections.PointRing;
import geometry.geom2d.BoundingCircle;
import geometry.geom2d.Point2D;
import geometry.geom2d.Polygon;
import geometry.math.Angle;
import geometry.math.MyRandom;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Tile;

/**
 * @author Benoît
 */
public class Pencil {
	public static final int MAX_SIZE = 12;

	public enum Shape {
		Square, Diamond, Circle
	}

	public enum Mode {
		Unique, Rough, Airbrush, Noise
	}

	private Point2D pos = Point2D.ORIGIN;
	public Point2D containerTilePos;
	public Point2D nearestTilePos;
	public Shape shape = Shape.Square;
	public Mode mode = Mode.Rough;

	public double size = 1;
	public double sizeIncrement = 1;

	public double strength = 1;
	public double strengthIncrement = 0.1;

	public boolean snapPair = false;

	public boolean maintained = false;

	public Pencil() {

	};

	public void incRadius() {
		if (size < sizeIncrement) {
			size = sizeIncrement;
		} else {
			size = Math.min(MAX_SIZE, size + sizeIncrement);
		}
	}

	public void decRadius() {
		size = Math.max(1, size - sizeIncrement);
	}

	private void setSquare() {
		shape = Shape.Square;
	}

	private void setDiamond() {
		shape = Shape.Diamond;
	}

	private void setCircle() {
		shape = Shape.Circle;
	}

	public void setPos(Point2D pos) {
		this.pos = pos;
		containerTilePos = null;
		nearestTilePos = null;
	}

	public Point2D getCoord() {
		return pos;
	}

	public void setSquareShape() {
		shape = Shape.Square;
	}

	public void setDiamondShape() {
		shape = Shape.Diamond;
	}

	public void setCircleShape() {
		shape = Shape.Circle;
	}

	public void setRoughMode() {
		mode = Mode.Rough;
	}

	public void setAirbrushMode() {
		mode = Mode.Airbrush;
	}

	public void setNoiseMode() {
		mode = Mode.Noise;
	}

	public void setUniqueMode() {
		mode = Mode.Unique;
	}

	public void toggleShape() {
		switch (shape) {
			case Circle:
				setSquare();
				break;
			case Square:
				setDiamond();
				break;
			case Diamond:
				setCircle();
				break;
			default:
				throw new RuntimeException();
		}
	}

	public void toggleMode() {
		if (mode == Mode.Unique) {
			return;
		}
		switch (mode) {
			case Rough:
				mode = Mode.Airbrush;
				break;
			case Airbrush:
				mode = Mode.Noise;
				break;
			case Noise:
				mode = Mode.Rough;
				break;
			default:
				throw new RuntimeException();
		}
	}

	public List<Tile> getTiles() {
		switch (shape) {
			case Circle:
				return getInCircle(getContainerTilePos());
			case Diamond:
			case Square:
				return getInQuad(getContainerTilePos());
			default:
				throw new RuntimeException();
		}
	}

	public List<Tile> getNodes() {
		switch (shape) {
			case Circle:
				return getInCircle(getNearestTilePos());
			case Diamond:
			case Square:
				return getInQuad(getNearestTilePos());
			default:
				throw new RuntimeException();
		}
	}

	public Tile getCenterTile() {
		return ModelManager.battlefield.map.getTile(getContainerTilePos());
	}

	private Point2D getContainerTilePos() {
		if (containerTilePos == null) {
			int x = (int) Math.floor(pos.x);
			int y = (int) Math.floor(pos.y);
			if (size > 1 && snapPair) {
				if (x % 2 != 0) {
					x--;
				}
				if (y % 2 != 0) {
					y--;
				}
			}
			containerTilePos = new Point2D(x, y);
		}
		return containerTilePos;
	}

	private Point2D getNearestTilePos() {
		if (nearestTilePos == null) {
			int x = (int) Math.round(pos.x);
			int y = (int) Math.round(pos.y);
			nearestTilePos = new Point2D(x, y);
		}
		return nearestTilePos;

	}

	private List<Tile> getInCircle(Point2D center) {
		List<Tile> res = new ArrayList<>();
		if (size > 1 && snapPair) {
			center = center.getAddition(0.5, 0.5);
		}

		BoundingCircle circle = new BoundingCircle(center, (size / 2) + 0.01);

		for (int x = -(int) size; x < (int) size; x++) {
			for (int y = -(int) size; y < (int) size; y++) {
				Point2D p = new Point2D(x, y).getAddition(center);
				if (ModelManager.battlefield.map.isInBounds(p) && circle.contains(ModelManager.battlefield.map.getTile(p).getCoord())) {
					res.add(ModelManager.battlefield.map.getTile(p));
				}
			}
		}
		return res;
	}

	private List<Tile> getInQuad(Point2D center) {
		List<Tile> res = new ArrayList<>();
		if (size > 1 && snapPair) {
			center = center.getAddition(0.5, 0.5);
		}

		Polygon quad = getOrientedQuad(center);

		// map.getTile(center).elevation+=0.001;

		for (int x = -(int) size; x < (int) size; x++) {
			for (int y = -(int) size; y < (int) size; y++) {
				Point2D p = new Point2D(x, y).getAddition(center);
				if (ModelManager.battlefield.map.isInBounds(p) && quad.hasInside(ModelManager.battlefield.map.getTile(p).getCoord())) {
					res.add(ModelManager.battlefield.map.getTile(p));
				}
			}
		}
		return res;
	}

	private Polygon getOrientedQuad(Point2D center) {
		PointRing pr = new PointRing();
		double halfSide = (size / 2) - 0.01;
		pr.add(center.getAddition(-halfSide, -halfSide));
		pr.add(center.getAddition(halfSide, -halfSide));
		pr.add(center.getAddition(halfSide, halfSide));
		pr.add(center.getAddition(-halfSide, halfSide));
		switch (shape) {
			case Square:
				return new Polygon(pr);
			case Diamond:
				return new Polygon(pr).getRotation(Angle.RIGHT / 2, center);
			default:
				throw new RuntimeException();
		}
	}

	public double getShapeAngle() {
		if (shape == Shape.Diamond) {
			return Angle.RIGHT / 2;
		}
		return 0;
	}

	public double getElevation() {
		return ModelManager.battlefield.map.getTile(getContainerTilePos()).getZ();
	}

	private double getEccentricity(Point2D p) {
		switch (shape) {
			case Square:
				double xDist = Math.abs(p.x - pos.x);
				double yDist = Math.abs(p.y - pos.y);
				return ((size / 2) - Math.max(xDist, yDist)) / (size / 2);
			case Diamond:
				xDist = Math.abs(p.x - pos.x);
				yDist = Math.abs(p.y - pos.y);
				return ((size / 2) * 1.414 - xDist - yDist) / ((size / 2) * 1.414);
			case Circle:
				return ((size / 2) - p.getDistance(pos)) / (size / 2);
		}
		return 0;
	}

	public double getApplicationRatio(Point2D p) {
		switch (mode) {
			case Rough:
				return 1;
			case Airbrush:
				double x = getEccentricity(p);
				x = x * 10;
				x -= 5;
				double localFalloff = 1 / (1 + Math.exp(-x));
				return localFalloff;
			case Noise:
				return MyRandom.next();
			case Unique:
				return 1;
			default:
				throw new RuntimeException();
		}
	}

	public void release() {
		maintained = false;
	}

	public void maintain() {
		maintained = true;
	}

}
