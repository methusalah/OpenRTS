package controller;

import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;

import model.ModelManager;
import model.battlefield.actors.Actor;
import model.battlefield.actors.ActorPool;
import model.battlefield.actors.AnimationActor;
import model.battlefield.actors.ModelActor;
import model.battlefield.actors.ParticleActor;
import model.battlefield.actors.SoundActor;
import model.battlefield.army.Engagement;
import model.battlefield.army.Faction;
import model.battlefield.army.components.Unit;
import model.battlefield.map.Map;
import model.battlefield.map.Trinket;

import com.google.inject.Inject;

/**
 * @author Benoît
 */
public class Reporter {

	private static final Logger logger = Logger.getLogger(Reporter.class.getName());
	private static DecimalFormat df = new DecimalFormat("0");

	@Inject
	private ModelManager modelManager;
	
	@Inject
	public Reporter() {
		
	}
	
	public String getName(Unit u) {
		return "Name : " + u.UIName + " (" + u.race + ")";
	}

	public String getHealth(Unit u) {
		return "Health : " + u.health + "/" + u.maxHealth + " (" + df.format(u.getHealthRate() * 100) + "%)";
	}

	public String getState(Unit u) {
		return "State : " + u.state;
	}

	public String getOrder(Unit u) {
		String res = "Orders : ";
		for (String order : u.ai.getStates()) {
			res = res.concat(order + " /");
		}
		return res;
	}

	public String getHolding(Unit u) {
		if (u.getMover().holdPosition) {
			return "Holding : Yes";
		} else {
			return "Holding : No";
		}
	}


	public boolean reportSingleUnit() {
		return CommandManager.selection.size() == 1;
	}

	public boolean reportNothing() {
		return CommandManager.selection.isEmpty();
	}

	public void reportAll() {
		Engagement eng = modelManager.getBattlefield().getEngagement();
		logger.info("*** ENGAGEMENT ***");
		logger.info("Factions (" + eng.getFactions().size() + ") : ");
		for (Faction f : eng.getFactions()) {
			logger.info("    Name : " + f.getName());
			logger.info("    Units (" + f.getUnits().size() + ") : ");
			for (Unit u : f.getUnits()) {
				logger.info("        Name : " + u.builderID + " at " + u.getPos());
			}
		}

		Map m = modelManager.getBattlefield().getMap();
		logger.info("*** MAP ***");
		logger.info("Style ID : " + m.getMapStyleID());
		logger.info("Width/height : " + m.getWidth() + "/" + m.getHeight() + " (" + m.getTiles().size() + " tiles)");
		logger.info("Trinkets (" + m.getTrinkets().size() + ") : ");
		for (Trinket t : m.getTrinkets()) {
			logger.info("    Name : " + t.builderID + " at " + t.getPos());
		}
		logger.info("Number of initial trinkets : " + m.getInitialTrinkets().size());

		ActorPool p = modelManager.getBattlefield().getActorPool();
		List<? extends Actor> actorList;
		logger.info("*** ACTORS ***");
		actorList = p.getActorsOfType(ModelActor.class);

		logger.info("Model Actors (" + actorList.size() + ") : ");
		for (Actor a : actorList) {
			logger.info("    Name : " + a.debbug_id + ",  " + ((ModelActor) a).getComp());
		}

		actorList = p.getActorsOfType(AnimationActor.class);
		logger.info("Animation Actors (" + actorList.size() + ") : ");
		for (Actor a : actorList) {
			logger.info("    Name : " + a.debbug_id + ",  " + ((AnimationActor) a).animName);
		}

		actorList = p.getActorsOfType(ParticleActor.class);
		logger.info("Particle Actors (" + actorList.size() + ") : ");
		for (Actor a : actorList) {
			logger.info("    Name : " + a.debbug_id);
		}

		actorList = p.getActorsOfType(SoundActor.class);
		logger.info("Sound Actors (" + actorList.size() + ") : ");
		for (Actor a : actorList) {
			logger.info("    Name : " + a.debbug_id);
		}

	}

}
