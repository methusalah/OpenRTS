/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package controller.editor;

import java.util.List;
import java.util.logging.Logger;

import model.ModelManager;
import model.builders.entity.MapStyleBuilder;
import model.builders.entity.definitions.BuilderManager;
import model.editor.ToolManager;
import model.editor.tools.Tool;
import app.OpenRTSApplication;

import com.google.inject.Inject;
import com.google.inject.Injector;

import controller.GUIController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.ListBoxSelectionChangedEvent;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.screen.Screen;

/**
 * @author Benoît
 */
public class EditorGUIController extends GUIController {

	private static final Logger logger = Logger.getLogger(EditorGUIController.class.getName());

	@Inject
	private Injector injector;

	@Inject
	private BuilderManager builderManager;

	@Inject
	private ModelManager modelManager;
	
	@Inject
	private ToolManager toolManager;
	
	@Inject
	private EditorGUIDrawer drawer;
	
	@Inject
	public EditorGUIController(Nifty nifty, Injector injector) {
		super(nifty);
	}

	@Override
	public void activate() {
		nifty.gotoScreen("editor");
		nifty.update();
		askRedraw();
	}

	@Override
	public void update() {
		if (!nifty.getCurrentScreen().getScreenId().equals("editor")) {
			throw new RuntimeException("updating editor screen but is not current screen.");
		}
		if (redrawAsked) {
			drawer.draw();
			redrawAsked = false;
		}
	}

	@Override
	public void bind(Nifty nifty, Screen screen) {
		logger.info("bind");
		this.nifty = nifty;
	}

	@Override
	public void onStartScreen() {
		logger.info("onStartScreen");
	}

	@Override
	public void onEndScreen() {
		logger.info("onEndScreen");
	}

	@NiftyEventSubscriber(pattern = ".*slider")
	public void onSliderChanged(final String id, final SliderChangedEvent event) {
		Tool actualTooL = toolManager.getActualTool();
		switch (id) {
			case "sizeslider":
				if (event.getValue() < toolManager.getActualTool().pencil.size) {
					toolManager.getActualTool().pencil.decRadius();
				} else if (event.getValue() > toolManager.getActualTool().pencil.size) {
					toolManager.getActualTool().pencil.incRadius();
				}
				break;
			case "strslider":
				toolManager.getActualTool().pencil.strength = event.getValue();
				break;
		}
	}

	@NiftyEventSubscriber(pattern = ".*list")
	public void onListSelectionChanged(final String id, final ListBoxSelectionChangedEvent event) {
		List<Integer> selectionIndices = event.getSelectionIndices();
		if (selectionIndices.isEmpty()) {
			return;
		}
		switch (id) {
			case "selectionlist":
				toolManager.getActualTool().getSet().set(selectionIndices.get(0));
				break;
		}
	}

	@NiftyEventSubscriber(pattern = ".*dropdown")
	public void onDropDownSelectionChanged(final String id, final DropDownSelectionChangedEvent event) {
		if (!event.getDropDown().isEnabled()) {
			return;
		}
		int selectionIndex = event.getSelectionItemIndex();
		MapStyleBuilder builder = builderManager.getAllMapStyleBuilders().get(selectionIndex);
		if (!modelManager.getBattlefield().getMap().getMapStyleID().equals(builder.getId())) {
			modelManager.getBattlefield().getMap().setMapStyleID(builder.getId());
			modelManager.reload();
		}
	}

	public void load() {
		modelManager.loadBattlefield();
	}

	public void save() {
		modelManager.saveBattlefield();
	}

	public void newMap() {
		modelManager.setNewBattlefield();
	}

	public void settings() {
		OpenRTSApplication.appInstance.changeSettings();
	}

	public void toggleGrid() {
		injector.getInstance(EditorController.class).view.editorRend.toggleGrid();
	}

	public void setCliffTool() {
		toolManager.setCliffTool();
		askRedraw();
	}

	public void setHeightTool() {
		toolManager.setHeightTool();
		askRedraw();
	}

	public void setAtlasTool() {
		toolManager.setAtlasTool();
		askRedraw();
	}

	public void setRampTool() {
		toolManager.setRampTool();
		askRedraw();
	}

	public void setUnitTool() {
		toolManager.setUnitTool();
		askRedraw();
	}

	public void setTrincketTool() {
		toolManager.setTrinketTool();
		askRedraw();
	}

	public void setOperation(String indexString) {
		toolManager.getActualTool().setOperation(Integer.parseInt(indexString));
		askRedraw();
	}

	public void setSet(String indexString) {
		if (toolManager.getActualTool().hasSet()) {
			toolManager.getActualTool().getSet().set(Integer.parseInt(indexString));
		}
		askRedraw();
	}

	public void setRoughMode() {
		toolManager.getActualTool().pencil.setRoughMode();
		askRedraw();
	}

	public void setAirbrushMode() {
		toolManager.getActualTool().pencil.setAirbrushMode();
		askRedraw();
	}

	public void setNoiseMode() {
		toolManager.getActualTool().pencil.setNoiseMode();
		askRedraw();
	}

	public void setSquareShape() {
		toolManager.getActualTool().pencil.setSquareShape();
		askRedraw();
	}

	public void setDiamondShape() {
		toolManager.getActualTool().pencil.setDiamondShape();
		askRedraw();
	}

	public void setCircleShape() {
		toolManager.getActualTool().pencil.setCircleShape();
		askRedraw();
	}
}
