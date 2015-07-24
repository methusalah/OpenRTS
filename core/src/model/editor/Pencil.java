/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor;

import geometry.collections.PointRing;
import geometry.geom2d.BoundingCircle;
import geometry.geom2d.Point2D;
import geometry.geom2d.Polygon;
import geometry.geom2d.algorithm.PerlinNoise;
import geometry.math.AngleUtil;
import geometry.math.RandomUtil;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Tile;

/**
 * @author Benoît
 */
public class Pencil {
	public static final int MAX_SIZE = 12;

	public enum SHAPE {
		Square, Diamond, Circle
	}

	public enum MODE {
		Unique, Rough, Airbrush, Noise
	}

	private Point2D pos = Point2D.ORIGIN;
	public Point2D containerTilePos;
	public Point2D nearestTilePos;
	public SHAPE shape = SHAPE.Square;
	public MODE mode = MODE.Rough;

	public double size = 1;
	public double sizeIncrement = 1;

	public double strength = 1;
	public double strengthIncrement = 0.1;

	public boolean snapPair = false;

	public boolean maintained = false;
	
	private PerlinNoise perlin;

	public Pencil() {
		perlin = new PerlinNoise();
	}

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
		shape = SHAPE.Square;
	}

	private void setDiamond() {
		shape = SHAPE.Diamond;
	}

	private void setCircle() {
		shape = SHAPE.Circle;
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
		shape = SHAPE.Square;
	}

	public void setDiamondShape() {
		shape = SHAPE.Diamond;
	}

	public void setCircleShape() {
		shape = SHAPE.Circle;
	}

	public void setRoughMode() {
		mode = MODE.Rough;
	}

	public void setAirbrushMode() {
		mode = MODE.Airbrush;
	}

	public void setNoiseMode() {
		mode = MODE.Noise;
	}

	public void setUniqueMode() {
		mode = MODE.Unique;
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
		if (mode == MODE.Unique) {
			return;
		}
		switch (mode) {
			case Rough:
				mode = MODE.Airbrush;
				break;
			case Airbrush:
				mode = MODE.Noise;
				break;
			case Noise:
				mode = MODE.Rough;
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
		return ModelManager.getBattlefield().getMap().get(getContainerTilePos());
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
				if (ModelManager.getBattlefield().getMap().isInBounds(p) && circle.contains(new Point2D(Math.floor(p.x), Math.floor(p.y)))) {
					res.add(ModelManager.getBattlefield().getMap().get(p));
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
				if (ModelManager.getBattlefield().getMap().isInBounds(p) && quad.hasInside(new Point2D(Math.floor(p.x), Math.floor(p.y)))) {
					res.add(ModelManager.getBattlefield().getMap().get(p));
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
				return new Polygon(pr).getRotation(AngleUtil.RIGHT / 2, center);
			default:
				throw new RuntimeException();
		}
	}

	public double getShapeAngle() {
		if (shape == SHAPE.Diamond) {
			return AngleUtil.RIGHT / 2;
		}
		return 0;
	}

	public double getElevation() {
		return ModelManager.getBattlefield().getMap().get(getContainerTilePos()).getElevation();
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
				return perlin.noise(p);//, 10, 1);
//				return MyRandom.next();
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
