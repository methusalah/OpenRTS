package controller.editor;

import model.ModelManager;
import model.battlefield.lighting.SunLight;
import model.editor.ToolManager;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

import controller.InputInterpreter;
import controller.Reporter;
import event.ControllerChangeEvent;
import event.EventManager;

public class EditorInputInterpreter extends InputInterpreter {
	protected final static String SWITCH_CTRL_1 = "ctrl1";
	protected final static String SWITCH_CTRL_2 = "ctrl2";
	protected final static String SWITCH_CTRL_3 = "ctrl3";

	protected final static String PRIMARY_ACTION = "lc";
	protected final static String SECONDARY_ACTION = "rc";
	protected final static String TOGGLE_GRID = "GridDisplay";
	protected final static String TOGGLE_SOWER = "togglesower";
	protected final static String STEP_SOWER = "stepsower";

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
	protected final static String REPORT = "report";

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

				TOGGLE_GRID, TOGGLE_SOWER, STEP_SOWER, TOGGLE_SET, TOGGLE_OPERATION, INC_AIRBRUSH_FALLOF, DEC_AIRBRUSH_FALLOF,

				TOGGLE_LIGHT_COMP, INC_DAYTIME, DEC_DAYTIME, COMPASS_EAST, COMPASS_WEST, INC_INTENSITY, DEC_INTENSITY, TOGGLE_SPEED, DEC_RED, DEC_GREEN,
				DEC_BLUE, RESET_COLOR, SAVE, LOAD, NEW, REPORT};
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
		inputManager.addMapping(TOGGLE_SOWER, new KeyTrigger(KeyInput.KEY_H));
		inputManager.addMapping(STEP_SOWER, new KeyTrigger(KeyInput.KEY_B));
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
		inputManager.addMapping(REPORT, new KeyTrigger(KeyInput.KEY_SPACE));

		inputManager.addListener(this, mappings);
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
			ToolManager.releasePencils();
			analogUnpressed = false;
		} else {
			SunLight sunLight = ModelManager.getBattlefield().getSunLight();
			switch (name) {
				case PRIMARY_ACTION:
					ToolManager.analogPrimaryAction();
					break;
				case SECONDARY_ACTION:
					ToolManager.analogSecondaryAction();
					break;
				case INC_DAYTIME:
					sunLight.incDayTime();
					break;
				case DEC_DAYTIME:
					sunLight.decDayTime();
					break;
				case COMPASS_EAST:
					sunLight.turnCompassEast();
					break;
				case COMPASS_WEST:
					sunLight.turnCompassWest();
					break;
				case INC_INTENSITY:
					sunLight.incIntensity();
					break;
				case DEC_INTENSITY:
					sunLight.decIntensity();
					break;
				case DEC_RED:
					sunLight.decRed();
					break;
				case DEC_GREEN:
					sunLight.decGreen();
					break;
				case DEC_BLUE:
					sunLight.decBlue();
					break;
			}
		}
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (!isPressed) {
			switch (name) {
				case PRIMARY_ACTION:
					ToolManager.primaryAction();
					analogUnpressed = true;
					break;
				case SECONDARY_ACTION:
					ToolManager.secondaryAction();
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
					EventManager.post(new ControllerChangeEvent(0));
					break;
				case SWITCH_CTRL_2:
					EventManager.post(new ControllerChangeEvent(1));
					break;
				case SWITCH_CTRL_3:
					EventManager.post(new ControllerChangeEvent(2));
					break;
				case TOGGLE_PENCIL_SHAPE:
					ToolManager.getActualTool().pencil.toggleShape();
					break;
				case TOGGLE_PENCIL_MODE:
					ToolManager.getActualTool().pencil.toggleMode();
					break;
				case INC_SELECTOR_RADIUS:
					ToolManager.getActualTool().pencil.incRadius();
					break;
				case DEC_SELECTOR_RADIUS:
					ToolManager.getActualTool().pencil.decRadius();
					break;
				case SET_CLIFF_TOOL:
					ToolManager.setCliffTool();
					break;
				case SET_HEIGHT_TOOL:
					ToolManager.setHeightTool();
					break;
				case SET_ATLAS_TOOL:
					ToolManager.setAtlasTool();
					break;
				case SET_RAMP_TOOL:
					ToolManager.setRampTool();
					break;
				case SET_UNIT_TOOL:
					ToolManager.setUnitTool();
					break;

				case TOGGLE_OPERATION:
					ToolManager.toggleOperation();
					break;
				case TOGGLE_SET:
					ToolManager.toggleSet();
					break;
				case TOGGLE_GRID:
					((EditorController) ctrl).view.editorRend.toggleGrid();
					break;
				case TOGGLE_SOWER:
					ToolManager.toggleSower();
					break;
				case STEP_SOWER:
					ToolManager.stepSower();
					break;
				case TOGGLE_LIGHT_COMP:
					ModelManager.getBattlefield().getSunLight().toggleLight();
					break;
				case TOGGLE_SPEED:
					ModelManager.getBattlefield().getSunLight().toggleSpeed();
					break;
				case RESET_COLOR:
					ModelManager.getBattlefield().getSunLight().resetColor();
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
				case REPORT:
					Reporter.reportAll();
					break;
			}
			ctrl.guiController.askRedraw();
		}
	}
}
