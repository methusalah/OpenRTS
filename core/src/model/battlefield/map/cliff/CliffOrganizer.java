package model.battlefield.map.cliff;

import geometry.math.AngleUtil;

import java.util.logging.Logger;

import model.battlefield.map.Map;
import model.battlefield.map.Tile;

/**
 * Warning : this class is full of monstrous spiders and mysterious shadows in every corner. Proceed at your own risks. Compute magically the shape and angle of
 * a cliff, according to its neighborhood. Don't touch anything or it will explode !
 */
public class CliffOrganizer {

	private static final Logger logger = Logger.getLogger(CliffOrganizer.class.getName());

	public static void organize(Cliff c, Map map) {
		Tile t = c.getTile();
		Tile n = c.getTile().n();
		Tile s = c.getTile().s();
		Tile e = c.getTile().e();
		Tile w = c.getTile().w();

		if (n == null || s == null || e == null || w == null) {
			c.type = Cliff.Type.Border;
			return;
		}

		if (c.getUpperGrounds(map).size() > 5) {
			c.type = Cliff.Type.Bugged;
			return;
		}

		switch (c.getConnexionConfiguration(map)) {
			// orthogonal
			case "ns":
				if (e.level > w.level) {
					c.angle = AngleUtil.FLAT;
					c.link(s, n);
				} else {
					c.angle = 0;
					c.link(n, s);
				}
				c.type = Cliff.Type.Orthogonal;
				break;
			case "ew":
				if (n.level > s.level) {
					c.angle = -AngleUtil.RIGHT;
					c.link(e, w);
				} else {
					c.angle = AngleUtil.RIGHT;
					c.link(w, e);
				}
				c.type = Cliff.Type.Orthogonal;
				break;

				// digonal
			case "sw":
				c.angle = 0;
				if (w.getNeighborsMaxLevel() > t.getNeighborsMaxLevel()) {
					c.link(w, s);
					c.type = Cliff.Type.Salient;
				} else {
					c.link(s, w);
					c.type = Cliff.Type.Corner;
				}
				break;
			case "se":
				c.angle = AngleUtil.RIGHT;
				if (s.getNeighborsMaxLevel() > t.getNeighborsMaxLevel()) {
					c.link(s, e);
					c.type = Cliff.Type.Salient;
				} else {
					c.link(e, s);
					c.type = Cliff.Type.Corner;
				}
				break;
			case "ne":
				c.angle = AngleUtil.FLAT;
				if (e.getNeighborsMaxLevel() > t.getNeighborsMaxLevel()) {
					c.link(e, n);
					c.type = Cliff.Type.Salient;
				} else {
					c.link(n, e);
					c.type = Cliff.Type.Corner;
				}
				break;
			case "nw":
				c.angle = -AngleUtil.RIGHT;
				if (n.getNeighborsMaxLevel() > t.getNeighborsMaxLevel()) {
					c.link(n, w);
					c.type = Cliff.Type.Salient;
				} else {
					c.link(w, n);
					c.type = Cliff.Type.Corner;
				}
				break;

				// ending cliff (for ramp end)
			case "n":
				if (e.level > w.level) {
					c.angle = AngleUtil.FLAT;
				} else {
					c.angle = 0;
					c.link(n, null);
				}
				c.type = Cliff.Type.Orthogonal;
				break;
			case "s":
				if (e.level > w.level) {
					c.angle = AngleUtil.FLAT;
					c.link(s, null);
				} else {
					c.angle = 0;
				}
				c.type = Cliff.Type.Orthogonal;
				break;
			case "e":
				if (n.level > s.level) {
					c.angle = -AngleUtil.RIGHT;
					c.link(e, null);
				} else {
					c.angle = AngleUtil.RIGHT;
				}
				c.type = Cliff.Type.Orthogonal;
				break;
			case "w":
				if (n.level > s.level) {
					c.angle = -AngleUtil.RIGHT;
				} else {
					c.angle = AngleUtil.RIGHT;
					c.link(w, null);
				}
				c.type = Cliff.Type.Orthogonal;
				break;
			default:
				logger.info("Cliff neighboring is strange at " + c.getTile().getCoord() + " : " + c.getConnexionConfiguration(map));
				c.type = Cliff.Type.Bugged;
		}
	}
}
