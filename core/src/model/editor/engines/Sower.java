package model.editor.engines;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import geometry.math.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.ModelManager;
import model.battlefield.map.Map;
import model.battlefield.map.Trinket;
import model.builders.entity.TrinketBuilder;
import model.builders.entity.definitions.BuilderManager;
import util.MapArtisanManager;
import brainless.openrts.event.EventManager;
import brainless.openrts.event.GenericEvent;

public class Sower implements Runnable {
	private static final Logger logger = Logger.getLogger(Sower.class.getName());

	private static final int MAX_PLACES_COUNT = 30;
	private static final int MAX_TRINKETS_COUNT = 10;

	private volatile boolean pauseAsked = true;
	private volatile boolean paused = false;
	private List<Sowing> sowings = new ArrayList<>();

	private volatile Thread thread;

	private BuilderManager builderManager;
	private ModelManager modelManager;
	private MapArtisanManager mapArtisanManager;
	
	public Sower(BuilderManager builderManager, ModelManager modelManager, MapArtisanManager mapArtisanManager) {
		this.builderManager = builderManager;
		this.modelManager = modelManager;
		this.mapArtisanManager = mapArtisanManager;
		Sowing treeOnCliff = new Sowing();
		treeOnCliff.addTrinket("Tree", 1, 1.5, builderManager);
		treeOnCliff.addTrinket("Lun Tree", 1, 1.5, builderManager);
		treeOnCliff.addTrinket("Plant", 1, 1, builderManager);
		treeOnCliff.setCliffDist(4);
		treeOnCliff.setMaxSlope(10);
		treeOnCliff.addTexture("0", 0.5, 1);
		treeOnCliff.addTexture("11", 0, 0);
		sowings.add(treeOnCliff);

//		Sowing treeOnGrass = new Sowing();
//		treeOnGrass.addTrinket("Tree", 1, 3);
//		treeOnGrass.addTrinket("Lun Tree", 1, 3);
//		treeOnGrass.addTexture("1", 0.5, 1);
//		treeOnGrass.addTexture("11", 0, 0);
//		sowings.add(treeOnGrass);

		Sowing grass = new Sowing();
		grass.addTrinket("Tree", 1, 2, builderManager);
		grass.addTrinket("LittleRock", 10, 1, builderManager);
		grass.addTrinket("Herb2", 20, 1, builderManager);
		grass.addTrinket("Herb", 20, 1, builderManager);
		grass.addTexture("1", 0.5, 1);
		grass.addTexture("11", 0, 0);
		sowings.add(grass);

		Sowing rocks = new Sowing();
		rocks.addTrinket("LittleRock", 1, 1.5, builderManager);
		rocks.addTexture("11", 0, 0);
		rocks.addTexture("3", 0.6, 1);
		sowings.add(rocks);

		Sowing rocksOnSlope = new Sowing();
		rocksOnSlope.addTrinket("LittleRock", 1, 0.3, builderManager);
		rocksOnSlope.setMinSlope(20);
		rocksOnSlope.addTexture("11", 0, 0);
		rocksOnSlope.addTexture("3", 0.6, 1);
		sowings.add(rocksOnSlope);

		Sowing rocksAtCliffFoot = new Sowing();
		rocksAtCliffFoot.addTrinket("LittleRock", 1, 0.3, builderManager);
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
					try{
						if (!s.toGrow.isEmpty() && RandomUtil.next() > 0.5) {
//							logger.info("growing");
							grow(s, modelManager, mapArtisanManager);
						} else {
//							logger.info("finding");
							findNewPlace(s, modelManager, mapArtisanManager);
						}
					}catch(RuntimeException e){
						throw(e);
//						logger.info("exception in sower : "+e.);
					}
				}
//				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
		}
	}

	private void findNewPlace(Sowing s,ModelManager modelManager, MapArtisanManager mapArtisanManager) {
		Map m = modelManager.getBattlefield().getMap();
		Point2D randomPos = new Point2D(RandomUtil.next() * (m.xSize()-1),
				RandomUtil.next() * (m.ySize()-1));
		
		if (s.isAllowed(randomPos, modelManager)) {
			int trinketIndex = RandomUtil.between(0, s.trinketBuilders.size());
			TrinketBuilder tb = s.trinketBuilders.get(trinketIndex);
			Trinket candidate = tb.build(randomPos.get3D(m.getAltitudeAt(randomPos)));
			candidate.separationRadius *= s.spacings.get(trinketIndex);

			if(checkCandidateAndValid(s, candidate, modelManager, mapArtisanManager))
				return;
		}
	}

	private void grow(Sowing s,ModelManager modelManager, MapArtisanManager mapArtisanManager) {
		Map m = modelManager.getBattlefield().getMap();
		
		Trinket source = s.toGrow.get(RandomUtil.nextInt(s.toGrow.size()));
		for (int i = 0; i < MAX_TRINKETS_COUNT; i++) {
			int trinketIndex = RandomUtil.between(0, s.trinketBuilders.size());
			Trinket candidate = s.trinketBuilders.get(trinketIndex).build(Point3D.ORIGIN);
			candidate.separationRadius *= s.spacings.get(trinketIndex);
			for (int j = 0; j < MAX_PLACES_COUNT; j++) {
				double separationDistance = source.getSpacing(candidate);
				Point2D place = source.getCoord().getTranslation(RandomUtil.between(0, AngleUtil.FULL),
						RandomUtil.between(0, separationDistance * 4));
				if (!m.isInBounds(place) || !s.isAllowed(place, modelManager)) {
					continue;
				}
				candidate.setPos(place.get3D(m.getAltitudeAt(place)));
				if(checkCandidateAndValid(s, candidate, modelManager, mapArtisanManager)){
					//debug
					return;
				}
			}
		}
		s.toGrow.remove(source);
	}
	
	private boolean checkCandidateAndValid(Sowing s, Trinket candidate, ModelManager modelManager, MapArtisanManager mapArtisanManager){
		Map m = modelManager.getBattlefield().getMap();

		Asset aCand = new Asset(candidate.modelPath, candidate.getActor().getScaleX(), candidate.getOrientation(), candidate.getPos());
		boolean suspect = false;
		for (Trinket n : m.getInCircle(Trinket.class, candidate.getCoord(), 10)) {
			Asset aN = new Asset(n.modelPath, n.getActor().getScaleX(), n.getOrientation(), n.getPos());
			if(CollisionTester.areColliding(aN, aCand, stepByStep))
				return false;
			else if(n.getPos().getDistance(candidate.getPos()) <= n.getSpacing(candidate))
				suspect = true;
		}

//		if(suspect){
//			for(Spatial spatial : aCand.links)
//				EventManager.post(new GenericEvent(spatial));
//			askForPause();
//		}

		sowTrinket(s, candidate);
		synchronized (m) {
			mapArtisanManager.attachTrinket(candidate, m);
		}
//		if(aCand.s != null){
//			EventManager.post(new GenericEvent(aCand.s));
//			for(Spatial link : aCand.links)
//				EventManager.post(new GenericEvent(link));
//		}
		return true;
	}

	public void askForPause() {
		pauseAsked = true;
	}

	public void unpause() {
		EventManager.post(new GenericEvent(null));
		this.notify();
		stepByStep = false;
	}
	
	
	boolean stepByStep = false;
	
	public void stepByStep(){
		stepByStep = true;
		Sowing s = sowings.get(RandomUtil.nextInt(sowings.size()));
		try{
			if (!s.toGrow.isEmpty() && RandomUtil.next() > 0.5) {
				grow(s, modelManager, mapArtisanManager);
			} else {
				findNewPlace(s, modelManager, mapArtisanManager);
			}
		}catch(RuntimeException e){
			throw(e);
		}

	}

	public boolean isPaused() {
		return paused;
	}

	public void destroy() {
		thread.interrupt();
	}

}
