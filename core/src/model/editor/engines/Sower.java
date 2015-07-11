package model.editor.engines;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import geometry.math.MyRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.ModelManager;
import model.battlefield.map.Trinket;
import model.builders.MapArtisan;
import model.builders.entity.TrinketBuilder;

public class Sower implements Runnable {
	private static final Logger logger = Logger.getLogger(Sower.class.getName());

	private static final int MAX_PLACES_COUNT = 30;
	private static final int MAX_TRINKETS_COUNT = 10;

	private volatile boolean pauseAsked = true;
	private volatile boolean paused = false;
	private List<Sowing> sowings = new ArrayList<>();

	private volatile Thread thread;

	public Sower(){
		Sowing tree = new Sowing();
		tree.addTrinket("Tree");
		tree.addTrinket("Plant");
		tree.setCliffDist(2);
		tree.setMaxSlope(30);
		sowings.add(tree);

		Sowing rocks = new Sowing();
		rocks.addTrinket("LittleRock");
		rocks.setMinSlope(20);
		rocks.allowedGrounds.add("1");
		rocks.allowedGrounds.add("2");
		sowings.add(rocks);
	}

	private void sowTrinket(Sowing s, Trinket t){
		t.drawOnBattlefield();
		s.toGrow.add(t);
	}

	@Override
	public void run() {
		thread = Thread.currentThread();
		try {
			while(!Thread.currentThread().isInterrupted()) {
				if(pauseAsked){
					synchronized (this) {
						paused = true;
						this.wait();
						paused = false;
						pauseAsked = false;
					}
				}

				for(Sowing s : sowings){
					Trinket newTrinket;
					if(s.toGrow.isEmpty()){
						newTrinket = findNewPlace(s);
						//						LogUtil.logger.info("find new place : "+newTrinket);
					}else{
						newTrinket = grow(s);
						//						LogUtil.logger.info("grow : "+newTrinket);
					}
					if(newTrinket != null) {
						synchronized (ModelManager.getBattlefield().getMap()) {
							MapArtisan.attachTrinket(newTrinket, ModelManager.getBattlefield().getMap());
						}
					}
				}
				//				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
		}
	}

	private Trinket findNewPlace(Sowing s){
		Point2D randomPos = new Point2D(
				MyRandom.next()*ModelManager.getBattlefield().getMap().xSize(),
				MyRandom.next()*ModelManager.getBattlefield().getMap().ySize());
		if(s.isAllowed(randomPos)){
			TrinketBuilder tb = s.trinketBuilders.get(MyRandom.between(0, s.trinketBuilders.size()));
			Trinket candidate = tb.build(randomPos.get3D(ModelManager.getBattlefield().getMap().getAltitudeAt(randomPos)));
			boolean isValid = true;
			for(Trinket n : ModelManager.getBattlefield().getCloseComps(candidate, randomPos, 10)){
				double separationDistance = n.getRadius()+candidate.getRadius();
				if(n.getDistance(candidate)<separationDistance){
					isValid = false;
					break;
				}
			}
			if(isValid){
				sowTrinket(s, candidate);
				return candidate;
			}
		}
		return null;
	}

	private Trinket grow(Sowing s){
		Trinket source = s.toGrow.get(MyRandom.nextInt(s.toGrow.size()));
		List<Trinket> neibors = ModelManager.getBattlefield().getCloseComps(source, 10);
		for(int i = 0; i < MAX_TRINKETS_COUNT; i++){
			Trinket candidate = s.trinketBuilders.get(MyRandom.between(0, s.trinketBuilders.size())).build(Point3D.ORIGIN);
			for(int j = 0; j < MAX_PLACES_COUNT; j++){
				double separationDistance = source.getRadius()+candidate.getRadius();
				Point2D place = source.getCoord().getTranslation(MyRandom.between(0, AngleUtil.FULL), MyRandom.between(separationDistance, separationDistance*2));
				if(!ModelManager.getBattlefield().getMap().isInBounds(place) ||
						!s.isAllowed(place)) {
					continue;
				}

				boolean isValidePlace = true;
				for(Trinket n : neibors) {
					if(n.getCoord().getDistance(place) < n.getRadius()+candidate.getRadius()){
						isValidePlace = false;
						break;
					}
				}

				if(isValidePlace){
					candidate.setPos(place.get3D(ModelManager.getBattlefield().getMap().getAltitudeAt(place)));
					sowTrinket(s, candidate);
					return candidate;

				}
			}
		}
		s.toGrow.remove(source);
		return null;
	}

	public void askForPause(){
		pauseAsked = true;
	}

	public void unpause(){
		this.notify();
	}

	public boolean isPaused(){
		return paused;
	}

	public void destroy(){
		thread.interrupt();
	}






















































}
