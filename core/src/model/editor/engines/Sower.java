package model.editor.engines;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.Angle;
import geometry.math.MyRandom;

import java.util.ArrayList;
import java.util.List;

import tools.LogUtil;
import model.ModelManager;
import model.battlefield.map.Trinket;
import model.builders.TrinketBuilder;
import model.builders.definitions.BuilderManager;

public class Sower implements Runnable {
	private static final int MAX_PLACES_COUNT = 30;
	private static final int MAX_TRINKETS_COUNT = 10;
	
	List<Trinket> toGrow = new ArrayList<>();
	private volatile boolean pauseAsked = true;
	private volatile boolean paused = false;
	
	private volatile Thread thread; 
	
	public Sower(){
		Trinket first = getRandomTrinket();
		Point3D randomPos = new Point3D(
				(double)ModelManager.getBattlefield().getMap().width/2,
				(double)ModelManager.getBattlefield().getMap().height/2,
				0);
		randomPos.z = ModelManager.getBattlefield().getMap().getAltitudeAt(randomPos.get2D());
		first.setPos(randomPos);
		toGrow.add(first);
	}
	
	private void sowTrinket(Trinket t){
		t.drawOnBattlefield();
		toGrow.add(t);
		ModelManager.getBattlefield().store(t);
	}
	
	private Trinket getRandomTrinket(){
		List<TrinketBuilder> builders = BuilderManager.getAllEditableTrinketBuilders();
		return builders.get(MyRandom.nextInt(builders.size())).build(Point3D.ORIGIN);
		
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

				
				if(!toGrow.isEmpty()){
					Trinket newTrinket = findCandidate();
					if(newTrinket != null)
						synchronized (ModelManager.getBattlefield().getMap()) {
							ModelManager.getBattlefield().getMap().trinkets.add(newTrinket);
						}
				}
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
		}
	}
	
	private Trinket findCandidate(){
		Trinket source = toGrow.get(MyRandom.nextInt(toGrow.size()));
		List<Trinket> neibors = ModelManager.getBattlefield().getCloseComps(source, 10);
		for(int i = 0; i < MAX_TRINKETS_COUNT; i++){
			Trinket candidate = getRandomTrinket();
			for(int j = 0; j < MAX_PLACES_COUNT; j++){
				double separationDistance = source.getRadius()+candidate.getRadius();
				Point2D place = source.getCoord().getTranslation(MyRandom.between(0, Angle.FULL), MyRandom.between(separationDistance, separationDistance*2));
				if(!ModelManager.getBattlefield().getMap().isInBounds(place))
					continue;
				
				boolean isValidePlace = true;
				for(Trinket n : neibors)
					if(n.getCoord().getDistance(place) < n.getRadius()+candidate.getRadius()){
						isValidePlace = false;
						break;
					}

				if(isValidePlace){
					candidate.setPos(place.get3D(ModelManager.getBattlefield().getMap().getAltitudeAt(place)));
					sowTrinket(candidate);
					return candidate;
					
				}
			}
		}
		toGrow.remove(source);
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
