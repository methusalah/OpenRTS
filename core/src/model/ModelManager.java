package model;

import java.io.File;
import java.util.logging.Logger;

import model.battlefield.Battlefield;
import model.battlefield.BattlefieldFactory;
import model.battlefield.army.ArmyManager;
import model.builders.entity.definitions.BuilderManager;
import model.builders.entity.definitions.DefParser;
import util.MapArtisanManager;
import brainless.openrts.event.BattleFieldUpdateEvent;
import brainless.openrts.event.EventManager;

import com.google.inject.Inject;

public class ModelManager {

	private static final Logger logger = Logger.getLogger(ModelManager.class.getName());

	public static final String CONFIG_PATH = "assets/data";
	public static final String DEFAULT_MAP_PATH = "assets/maps/";
	private static final double UPDATE_DELAY = 1000;
	private static final int DEFAULT_WIDTH = 64;
	private static final int DEFAULT_HEIGHT = 32;

	private static final BattlefieldFactory factory;

	private static Battlefield battlefield;
	private final static DefParser parser;
	private static double nextUpdate = 0;
	public static boolean battlefieldReady = true;

	static {
		parser = new DefParser(CONFIG_PATH);

		factory = new BattlefieldFactory();
		// setNewBattlefield();
	}

	@Inject
	private MapArtisanManager mapArtisanManager;
	
	@Inject
	private ArmyManager armyManager;
	
	@Inject
	private BuilderManager builderManager;
	
	@Inject
	ModelManager() {

	}

	public void updateConfigs() {
		if (System.currentTimeMillis() > nextUpdate) {
			nextUpdate = System.currentTimeMillis() + UPDATE_DELAY;
			parser.readFiles();
		}
	}

	public void loadBattlefield() {
		Battlefield loadedBattlefield = factory.loadWithFileChooser();
		setBattlefield(loadedBattlefield);
	}

	public void loadBattlefield(String file) {
		Battlefield loadedBattlefield = factory.load(file);
		setBattlefield(loadedBattlefield);
	}

	public void saveBattlefield() {
		factory.save(battlefield);
	}

	public void setNewBattlefield() {
		setBattlefield(factory.getNew(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	void setBattlefield(Battlefield battlefield) {
		if (battlefield != null) {
			ModelManager.battlefield = battlefield;
			battlefieldReady = true;
			mapArtisanManager.act(getBattlefield().getMap());
			getBattlefield().getEngagement().reset(armyManager, builderManager);
			EventManager.post(new BattleFieldUpdateEvent());
			logger.info("Done.");

		}
	}

	public void reload() {
		saveBattlefield();
		Battlefield loadedBattlefield = factory.load(battlefield.getFileName());
		setBattlefield(loadedBattlefield);
	}

	public Battlefield getBattlefield() {
		if(battlefieldReady) {
			return battlefield;
		} else {
			throw new RuntimeException("Trying to acces to battlefield while it is unavailable");
		}
	}

	public void setBattlefieldUnavailable(){
		battlefieldReady = false;
	}
	public void setBattlefieldReady(){
		battlefieldReady = true;
	}

	public Battlefield loadOnlyStaticValues(File file) {
		return factory.loadOnlyStaticValues(file);
	}
	
}
