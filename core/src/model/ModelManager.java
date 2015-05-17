package model;

import geometry.tools.LogUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import model.battlefield.Battlefield;
import model.battlefield.BattlefieldFactory;
import model.builders.definitions.BuilderLibrary;
import model.builders.definitions.DefParser;
import model.editor.ToolManager;

public class ModelManager {
	public static final String BATTLEFIELD_UPDATED_EVENT = "mapupdatedevent";
	public static final String CONFIG_PATH = "assets/data";
	public static final String DEFAULT_MAP_PATH = "assets/maps/";
	private static final double UPDATE_DELAY = 1000;
	private static final int DEFAULT_WIDTH = 64;
	private static final int DEFAULT_HEIGHT = 32;

	public static final BattlefieldFactory factory;
	public static final BuilderLibrary lib;

	public static Battlefield battlefield;

	public static ToolManager toolManager = new ToolManager();

	final static DefParser parser;
	static double nextUpdate = 0;

	private static ArrayList<ActionListener> listeners = new ArrayList<>();

	static {
		lib = new BuilderLibrary();
		parser = new DefParser(lib, CONFIG_PATH);

		factory = new BattlefieldFactory(lib);
		setNewBattlefield();
	}

	// no instancing from outside
	private ModelManager() {

	}

	public static void updateConfigs() {
		if(System.currentTimeMillis()>nextUpdate){
			nextUpdate = System.currentTimeMillis()+UPDATE_DELAY;
			parser.readFile();
		}
	}

	public static void loadBattlefield() {
		Battlefield loadedBattlefield = factory.load();
		if(loadedBattlefield != null) {
			setBattlefield(loadedBattlefield);
		}
	}

	public static void saveBattlefield() {
		factory.save(battlefield);
	}

	public static void setNewBattlefield() {
		setBattlefield(factory.getNew(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	private static void setBattlefield(Battlefield battlefield) {
		ModelManager.battlefield = battlefield;
		CommandManager.armyManager = battlefield.armyManager;
		CommandManager.map = battlefield.map;
		LogUtil.logger.info("Reseting view...");
		notifyListeners(BATTLEFIELD_UPDATED_EVENT);
		LogUtil.logger.info("Done.");
	}

	public static void reload() {
		saveBattlefield();
		Battlefield loadedBattlefield = factory.load(battlefield.fileName);
		if(loadedBattlefield != null) {
			setBattlefield(loadedBattlefield);
		}
	}

	public static void addListener(ActionListener listener) {
		listeners.add(listener);
	}

	private static void notifyListeners(String eventCommand) {
		for(ActionListener l : listeners) {
			// FIXME: why we are doing this?
			l.actionPerformed(new ActionEvent(new ModelManager(), 0, eventCommand));
		}

	}
}
