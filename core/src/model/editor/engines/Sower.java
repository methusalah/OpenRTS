package model.editor.engines;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import geometry.math.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.ModelManager;
import model.battlefield.map.Trinket;
import model.builders.MapArtisanUtil;
import model.builders.entity.TrinketBuilder;

public class Sower implements Runnable {
	private static final Logger logger = Logger.getLogger(Sower.class.getName());

	private static final int MAX_PLACES_COUNT = 30;
	private static final int MAX_TRINKETS_COUNT = 10;

	private volatile boolean pauseAsked = true;
	private volatile boolean paused = false;
	private List<Sowing> sowings = new ArrayList<>();

	private volatile Thread thread;

	public Sower() {
		Sowing tree = new Sowing();
		tree.addTrinket("Tree", 1, 1);
		tree.addTrinket("Plant", 1, 1);
		tree.setCliffDist(4);
		tree.addTexture("0", 0.5, 1);
		tree.addTexture("11", 0, 0);
		sowings.add(tree);

		Sowing grass = new Sowing();
		grass.addTrinket("Tree", 1, 2);
		grass.addTrinket("Plant", 20, 1.5);
		grass.addTexture("1", 0.5, 1);
		grass.addTexture("11", 0, 0);
		sowings.add(grass);

		Sowing rocks = new Sowing();
		rocks.addTrinket("LittleRock", 1, 1.5);
		rocks.addTexture("11", 0, 0);
		rocks.addTexture("3", 0.6, 1);
		sowings.add(rocks);

		Sowing rocksOnSlope = new Sowing();
		rocksOnSlope.addTrinket("LittleRock", 1, 0.3);
		rocksOnSlope.setMinSlope(20);
		rocksOnSlope.addTexture("11", 0, 0);
		rocksOnSlope.addTexture("3", 0.6, 1);
		sowings.add(rocksOnSlope);

		Sowing rocksAtCliffFoot = new Sowing();
		rocksAtCliffFoot.addTrinket("LittleRock", 1, 0.3);
		rocksAtCliffFoot.addTexture("11", 0, 0);
		rocksAtCliffFoot.addTexture("3", 0.6, 1);
		rocksAtCliffFoot.setCliffDist(3);
		sowings.add(rocksAtCliffFoot);
	}

	private void sowTrinket(Sowing s, Trinket t) {
		t.sowed = true;
		t.drawOnBattlefield();
		s.toGrow.add(t);
	}

	@Override
	public void run() {
		thread = Thread.currentThread();
		try {
			while (!Thread.currentThread().isInterrupted()) {
				if (pauseAsked) {
					synchronized (this) {
						paused = true;
						this.wait();
						paused = false;
						pauseAsked = false;
					}
				}

				for (Sowing s : sowings) {
					Trinket newTrinket;
					if (s.toGrow.isEmpty()) {
						newTrinket = findNewPlace(s);
						// LogUtil.logger.info("find new place : "+newTrinket);
					} else {
						newTrinket = grow(s);
						// LogUtil.logger.info("grow : "+newTrinket);
					}
					if (newTrinket != null) {
						synchronized (ModelManager.getBattlefield().getMap()) {
							MapArtisanUtil.attachTrinket(newTrinket, ModelManager.getBattlefield().getMap());
						}
					}
				}
//				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
		}
	}

	private Trinket findNewPlace(Sowing s) {
		double r1 = RandomUtil.next();
		double r2 = RandomUtil.next();
		Point2D randomPos = new Point2D(r1 * (ModelManager.getBattlefield().getMap().xSize()-1),
				r2 * (ModelManager.getBattlefield().getMap().ySize()-1));
		if(!ModelManager.getBattlefield().getMap().isInBounds(randomPos))
			return null;
		if (s.isAllowed(randomPos)) {
			int trinketIndex = RandomUtil.between(0, s.trinketBuilders.size());
			TrinketBuilder tb = s.trinketBuilders.get(trinketIndex);
			Trinket candidate = tb.build(randomPos.get3D(ModelManager.getBattlefield().getMap().getAltitudeAt(randomPos)));
			candidate.separationRadius *= s.spacings.get(trinketIndex);
			boolean isValid = true;
			for (Trinket n : ModelManager.getBattlefield().getCloseComps(candidate, randomPos, 10)) {
				double separationDistance = n.getSpacing(candidate);
				if (n.getDistance(candidate) < separationDistance) {
					isValid = false;
					break;
				}
			}
			if (isValid) {
				sowTrinket(s, candidate);
				return candidate;
			}
		}
		return null;
	}

	private Trinket grow(Sowing s) {
		Trinket source = s.toGrow.get(RandomUtil.nextInt(s.toGrow.size()));
		List<Trinket> neibors = ModelManager.getBattlefield().getCloseComps(source, 20);
		for (int i = 0; i < MAX_TRINKETS_COUNT; i++) {
			int trinketIndex = RandomUtil.between(0, s.trinketBuilders.size());
			Trinket candidate = s.trinketBuilders.get(trinketIndex).build(Point3D.ORIGIN);
			candidate.separationRadius *= s.spacings.get(trinketIndex);
			for (int j = 0; j < MAX_PLACES_COUNT; j++) {
				double separationDistance = source.getSpacing(candidate);
				Point2D place = source.getCoord().getTranslation(RandomUtil.between(0, AngleUtil.FULL),
						RandomUtil.between(separationDistance, separationDistance * 2));
				if (!ModelManager.getBattlefield().getMap().isInBounds(place) || !s.isAllowed(place)) {
					continue;
				}

				boolean isValidePlace = true;
				for (Trinket n : neibors) {
					if (n.getCoord().getDistance(place) < n.getSpacing(candidate)) {
						isValidePlace = false;
						break;
					}
				}

				if (isValidePlace) {
					candidate.setPos(place.get3D(ModelManager.getBattlefield().getMap().getAltitudeAt(place)));
					sowTrinket(s, candidate);
					return candidate;

				}
			}
		}
		s.toGrow.remove(source);
		return null;
	}

	public void askForPause() {
		pauseAsked = true;
	}

	public void unpause() {
		this.notify();
	}

	public boolean isPaused() {
		return paused;
	}

	public void destroy() {
		thread.interrupt();
	}

}
