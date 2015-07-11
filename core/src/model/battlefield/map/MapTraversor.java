package model.battlefield.map;

import geometry.geom2d.Point2D;

public class MapTraversor {

	/*
	 * Fast Voxel Traversal Algorithm for Ray Tracing John Amanatides Andrew Woo
	 */
	public static boolean meetObstacle(Map map, Point2D p1, Point2D p2) {
		// calculate the direction of the ray (linear algebra)
		double dirX = p2.x - p1.x;
		double dirY = p2.y - p1.y;
		double length = Math.sqrt(dirX * dirX + dirY * dirY);
		dirX /= length; // normalize the direction vector
		dirY /= length;
		double tDeltaX = 1 / Math.abs(dirX); // how far we must move in the ray direction before we encounter a new voxel in x-direction
		double tDeltaY = 1 / Math.abs(dirY); // same but y-direction

		// start voxel coordinates
		int x = (int) Math.floor(p1.x); // use your transformer function here
		int y = (int) Math.floor(p1.y);

		// end voxel coordinates
		int endX = (int) Math.floor(p2.x);
		int endY = (int) Math.floor(p2.y);

		// decide which direction to start walking in
		int stepX = (int) Math.signum(dirX);
		int stepY = (int) Math.signum(dirY);

		double tMaxX, tMaxY;
		// calculate distance to first intersection in the voxel we start from
		if (dirX < 0) {
			tMaxX = (x - p1.x) / dirX;
		} else {
			tMaxX = (x + 1 - p1.x) / dirX;
		}

		if (dirY < 0) {
			tMaxY = (y - p1.y) / dirY;
		} else {
			tMaxY = (y + 1 - p1.y) / dirY;
		}

		// check if first is occupied
		if (map.get(x, y).isBlocked()) {
			return true;
		}
		boolean reachedX = false, reachedY = false;
		while (!reachedX || !reachedY) {
			if (tMaxX < tMaxY) {
				tMaxX += tDeltaX;
				x += stepX;
			} else {
				tMaxY += tDeltaY;
				y += stepY;
			}
			if (map.get(x, y).isBlocked()) {
				return true;
			}

			if (stepX > 0) {
				if (x >= endX) {
					reachedX = true;
				}
			} else if (x <= endX) {
				reachedX = true;
			}

			if (stepY > 0) {
				if (y >= endY) {
					reachedY = true;
				}
			} else if (y <= endY) {
				reachedY = true;
			}
		}
		return false;
	}
}
