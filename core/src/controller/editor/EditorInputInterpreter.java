package controller.editor;

import geometry.tools.LogUtil;
import model.ModelManager;
import view.EditorView;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

import controller.InputInterpreter;

public class EditorInputInterpreter extends InputInterpreter {
	protected final static String SWITCH_CTRL_1 = "ctrl1";
	protected final static String SWITCH_CTRL_2 = "ctrl2";
	protected final static String SWITCH_CTRL_3 = "ctrl3";

	protected final static String PRIMARY_ACTION = "lc";
	protected final static String SECONDARY_ACTION = "rc";
	protected final static String TOGGLE_GRID = "GridDisplay";

	protected final static String SET_CLIFF_TOOL = "setclifftool";
	protected final static String SET_HEIGHT_TOOL = "setheighttool";
	protected final static String SET_ATLAS_TOOL = "setatlastool";
	protected final static String SET_RAMP_TOOL = "setramptool";
	protected final static String SET_UNIT_TOOL = "setunittool";

	protected final static String TOGGLE_PENCIL_SHAPE = "pencilshape";
	protected final static String TOGGLE_PENCIL_MODE = "pencilmode";
	protected final static String INC_SELECTOR_RADIUS = "selectorradius+";
	protected final static String DEC_SELECTOR_RADIUS = "selectorradius-";

	protected final static String TOGGLE_OPERATION = "toggleoperation";
	protected final static String TOGGLE_SET = "toggleset";

	protected final static String INC_AIRBRUSH_FALLOF = "incairbrushfallof";
	protected final static String DEC_AIRBRUSH_FALLOF = "decairbrushfallof";

	protected final static String TOGGLE_LIGHT_COMP = "togglelightcomp";
	protected final static String INC_DAYTIME = "incdaytime";
	protected final static String DEC_DAYTIME = "decdaytime";
	protected final static String COMPASS_EAST = "compasseast";
	protected final static String COMPASS_WEST = "compasswest";
	protected final static String INC_INTENSITY = "incintensity";
	protected final static String DEC_INTENSITY = "decintensity";
	protected final static String TOGGLE_SPEED = "togglespeed";
	protected final static String DEC_RED = "decred";
	protected final static String DEC_GREEN = "decgreen";
	protected final static String DEC_BLUE = "decblue";
	protected final static String RESET_COLOR = "resetcolor";

	protected final static String SAVE = "save";
	protected final static String LOAD = "load";
	protected final static String NEW = "new";

	boolean analogUnpressed = false;

	EditorInputInterpreter(EditorController controller) {
		super(controller);
		controller.spatialSelector.centered = false;
		setMappings();
	}

	private void setMappings() {
		mappings = new String[] { SWITCH_CTRL_1, SWITCH_CTRL_2, SWITCH_CTRL_3,

				PRIMARY_ACTION, SECONDARY_ACTION, TOGGLE_PENCIL_SHAPE, TOGGLE_PENCIL_MODE, INC_SELECTOR_RADIUS, DEC_SELECTOR_RADIUS, SET_CLIFF_TOOL, SET_HEIGHT_TOOL,
				SET_ATLAS_TOOL, SET_RAMP_TOOL, SET_UNIT_TOOL,

				TOGGLE_GRID, TOGGLE_SET, TOGGLE_OPERATION, INC_AIRBRUSH_FALLOF, DEC_AIRBRUSH_FALLOF,

				TOGGLE_LIGHT_COMP, INC_DAYTIME, DEC_DAYTIME, COMPASS_EAST, COMPASS_WEST, INC_INTENSITY, DEC_INTENSITY, TOGGLE_SPEED, DEC_RED, DEC_GREEN,
				DEC_BLUE, RESET_COLOR, SAVE, LOAD, NEW, };
	}

	@Override
	protected void registerInputs(InputManager inputManager) {
		inputManager.addMapping(SWITCH_CTRL_1, new KeyTrigger(KeyInput.KEY_F1));
		inputManager.addMapping(SWITCH_CTRL_2, new KeyTrigger(KeyInput.KEY_F2));
		inputManager.addMapping(SWITCH_CTRL_3, new KeyTrigger(KeyInput.KEY_F3));

		inputManager.addMapping(PRIMARY_ACTION, new MouseButtonTrigger(0));
		inputManager.addMapping(SECONDARY_ACTION, new MouseButtonTrigger(1));

		inputManager.addMapping(TOGGLE_PENCIL_SHAPE, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(TOGGLE_PENCIL_MODE, new KeyTrigger(KeyInput.KEY_Z));
		inputManager.addMapping(INC_SELECTOR_RADIUS, new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addMapping(DEC_SELECTOR_RADIUS, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(SET_CLIFF_TOOL, new KeyTrigger(KeyInput.KEY_1));
		inputManager.addMapping(SET_HEIGHT_TOOL, new KeyTrigger(KeyInput.KEY_2));
		inputManager.addMapping(SET_ATLAS_TOOL, new KeyTrigger(KeyInput.KEY_3));
		inputManager.addMapping(SET_RAMP_TOOL, new KeyTrigger(KeyInput.KEY_4));
		inputManager.addMapping(SET_UNIT_TOOL, new KeyTrigger(KeyInput.KEY_5));

		inputManager.addMapping(TOGGLE_GRID, new KeyTrigger(KeyInput.KEY_G));
		inputManager.addMapping(TOGGLE_OPERATION, new KeyTrigger(KeyInput.KEY_E));
		inputManager.addMapping(TOGGLE_SET, new KeyTrigger(KeyInput.KEY_D));

		inputManager.addMapping(TOGGLE_LIGHT_COMP, new KeyTrigger(KeyInput.KEY_NUMPAD7));
		inputManager.addMapping(INC_DAYTIME, new KeyTrigger(KeyInput.KEY_NUMPAD8));
		inputManager.addMapping(DEC_DAYTIME, new KeyTrigger(KeyInput.KEY_NUMPAD5));
		inputManager.addMapping(COMPASS_EAST, new KeyTrigger(KeyInput.KEY_NUMPAD6));
		inputManager.addMapping(COMPASS_WEST, new KeyTrigger(KeyInput.KEY_NUMPAD4));
		inputManager.addMapping(INC_INTENSITY, new KeyTrigger(KeyInput.KEY_ADD));
		inputManager.addMapping(DEC_INTENSITY, new KeyTrigger(KeyInput.KEY_SUBTRACT));
		inputManager.addMapping(TOGGLE_SPEED, new KeyTrigger(KeyInput.KEY_NUMPAD9));
		inputManager.addMapping(DEC_RED, new KeyTrigger(KeyInput.KEY_NUMPAD1));
		inputManager.addMapping(DEC_GREEN, new KeyTrigger(KeyInput.KEY_NUMPAD2));
		inputManager.addMapping(DEC_BLUE, new KeyTrigger(KeyInput.KEY_NUMPAD3));
		inputManager.addMapping(RESET_COLOR, new KeyTrigger(KeyInput.KEY_NUMPAD0));
		inputManager.addMapping(SAVE, new KeyTrigger(KeyInput.KEY_F5));
		inputManager.addMapping(LOAD, new KeyTrigger(KeyInput.KEY_F9));
		inputManager.addMapping(NEW, new KeyTrigger(KeyInput.KEY_F12));

		inputManager.addListener(this, mappings);

		LogUtil.logger.info("------ Map editing (ZQSD zone)");
		LogUtil.logger.info(" Tools : '1' for cliff tool, '2' for height tool, '3' for atlas tool, '4' for ramp tool, '5' for unit tool.");
		LogUtil.logger.info("          'e' toggle between actual tool's operations (raise, low, noise, smooth, propagate, uniform...");
		LogUtil.logger.info("          'd' toggle between actual tool's sets (cliff styles, textures...)");
		LogUtil.logger.info(" Pencil : 'a' toggle between pencil's shapes (square, diamond and circle)");
		LogUtil.logger.info("          'z' toggle between pencil's modes (rough, brush and noise)");
		LogUtil.logger.info("          'q' & 'w' increase/decrease radius");
		LogUtil.logger.info("");
		LogUtil.logger.info("------ Lighting (numpad)");
		LogUtil.logger.info(" '7' toggle between sunlight components : sunlight/shadowcaster/both/ambient");
		LogUtil.logger.info(" '8' & '5' increase/decrease daytime");
		LogUtil.logger.info(" '4' & '6' rotate compass");
		LogUtil.logger.info(" '1', '2' & '3' decrease red, green and blue component");
		LogUtil.logger.info(" '0' reset color");
		LogUtil.logger.info(" '+' & '-' change intensity");
		LogUtil.logger.info("");
		LogUtil.logger.info("------- General");
		LogUtil.logger.info("F5 to save, F9 to load, F12 for a new map");
	}

	@Override
	protected void unregisterInputs(InputManager inputManager) {
		for (String s : mappings) {
			if (inputManager.hasMapping(s)) {
				inputManager.deleteMapping(s);
			}
		}
		inputManager.removeListener(this);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (analogUnpressed) {
			ModelManager.toolManager.releasePencils();
			analogUnpressed = false;
		} else {
			switch (name) {
				case PRIMARY_ACTION:
					ModelManager.toolManager.analogPrimaryAction();
					break;
				case SECONDARY_ACTION:
					ModelManager.toolManager.analogSecondaryAction();
					break;
				case INC_DAYTIME:
					ModelManager.battlefield.sunLight.incDayTime();
					break;
				case DEC_DAYTIME:
					ModelManager.battlefield.sunLight.decDayTime();
					break;
				case COMPASS_EAST:
					ModelManager.battlefield.sunLight.turnCompassEast();
					break;
				case COMPASS_WEST:
					ModelManager.battlefield.sunLight.turnCompassWest();
					break;
				case INC_INTENSITY:
					ModelManager.battlefield.sunLight.incIntensity();
					break;
				case DEC_INTENSITY:
					ModelManager.battlefield.sunLight.decIntensity();
					break;
				case DEC_RED:
					ModelManager.battlefield.sunLight.decRed();
					break;
				case DEC_GREEN:
					ModelManager.battlefield.sunLight.decGreen();
					break;
				case DEC_BLUE:
					ModelManager.battlefield.sunLight.decBlue();
					break;
			}
		}
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (!isPressed) {
			switch (name) {
				case PRIMARY_ACTION:
					ModelManager.toolManager.primaryAction();
					analogUnpressed = true;
					break;
				case SECONDARY_ACTION:
					ModelManager.toolManager.secondaryAction();
					analogUnpressed = true;
					break;
				case INC_DAYTIME:
				case DEC_DAYTIME:
				case COMPASS_EAST:
				case COMPASS_WEST:
				case INC_INTENSITY:
				case DEC_INTENSITY:
				case DEC_RED:
				case DEC_GREEN:
				case DEC_BLUE:
					analogUnpressed = true;
					break;

				case SWITCH_CTRL_1:
					ctrl.notifyListeners("CTRL1");
					break;
				case SWITCH_CTRL_2:
					ctrl.notifyListeners("CTRL2");
					break;
				case SWITCH_CTRL_3:
					ctrl.notifyListeners("CTRL3");
					break;
				case TOGGLE_PENCIL_SHAPE:
					ModelManager.toolManager.actualTool.pencil.toggleShape();
					break;
				case TOGGLE_PENCIL_MODE:
					ModelManager.toolManager.actualTool.pencil.toggleMode();
					break;
				case INC_SELECTOR_RADIUS:
					ModelManager.toolManager.actualTool.pencil.incRadius();
					break;
				case DEC_SELECTOR_RADIUS:
					ModelManager.toolManager.actualTool.pencil.decRadius();
					break;
				case SET_CLIFF_TOOL:
					ModelManager.toolManager.setCliffTool();
					break;
				case SET_HEIGHT_TOOL:
					ModelManager.toolManager.setHeightTool();
					break;
				case SET_ATLAS_TOOL:
					ModelManager.toolManager.setAtlasTool();
					break;
				case SET_RAMP_TOOL:
					ModelManager.toolManager.setRampTool();
					break;
				case SET_UNIT_TOOL:
					ModelManager.toolManager.setUnitTool();
					break;

				case TOGGLE_OPERATION:
					ModelManager.toolManager.toggleOperation();
					break;
				case TOGGLE_SET:
					ModelManager.toolManager.toggleSet();
					break;
				case TOGGLE_GRID:
					((EditorView) ctrl.view).editorRend.toggleGrid();
					break;
				case TOGGLE_LIGHT_COMP:
					ModelManager.battlefield.sunLight.toggleLight();
					break;
				case TOGGLE_SPEED:
					ModelManager.battlefield.sunLight.toggleSpeed();
					break;
				case RESET_COLOR:
					ModelManager.battlefield.sunLight.resetColor();
					break;
				case SAVE:
					ModelManager.saveBattlefield();
					break;
				case LOAD:
					ModelManager.loadBattlefield();
					break;
				case NEW:
					ModelManager.setNewBattlefield();
					break;
			}
			ctrl.guiController.askRedraw();
		}
	}
}
