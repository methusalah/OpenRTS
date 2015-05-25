package model;

import event.BattleFieldUpdateEvent;
import event.EventManager;
import geometry.tools.LogUtil;
import model.battlefield.Battlefield;
import model.battlefield.BattlefieldFactory;
import model.builders.definitions.DefParser;

public class ModelManager {
	public static final String BATTLEFIELD_UPDATED_EVENT = "mapupdatedevent";
	public static final String CONFIG_PATH = "assets/data";
	public static final String DEFAULT_MAP_PATH = "assets/maps/";
	private static final double UPDATE_DELAY = 1000;
	private static final int DEFAULT_WIDTH = 64;
	private static final int DEFAULT_HEIGHT = 32;

	private static final BattlefieldFactory factory;

	private static Battlefield battlefield;
	private final static DefParser parser;
	private static double nextUpdate = 0;

	// private static List<ActionListener> listeners = new ArrayList<>();

	static {
		parser = new DefParser(CONFIG_PATH);

		factory = new BattlefieldFactory();
		setNewBattlefield();
	}

	// no instancing from outside
	private ModelManager() {

	}

	public static void updateConfigs() {
		if (System.currentTimeMillis() > nextUpdate) {
			nextUpdate = System.currentTimeMillis() + UPDATE_DELAY;
			parser.readFile();
		}
	}

	public static void loadBattlefield() {
		Battlefield loadedBattlefield = factory.load();
		setBattlefield(loadedBattlefield);
	}

	public static void loadBattlefield(String file) {
		Battlefield loadedBattlefield = factory.load(file);
		setBattlefield(loadedBattlefield);
	}

	public static void saveBattlefield() {
		factory.save(getBattlefield());
	}

	public static void setNewBattlefield() {
		setBattlefield(factory.getNew(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	public static void setBattlefield(Battlefield battlefield) {
		if (battlefield != null) {
			ModelManager.battlefield = battlefield;
			LogUtil.logger.info("Reseting view...");
			EventManager.post(new BattleFieldUpdateEvent());
			LogUtil.logger.info("Done.");
		}
	}

	public static void reload() {
		saveBattlefield();
		Battlefield loadedBattlefield = factory.load(getBattlefield().getFileName());
		setBattlefield(loadedBattlefield);
	}

	public static Battlefield getBattlefield() {
		return battlefield;
	}

}
