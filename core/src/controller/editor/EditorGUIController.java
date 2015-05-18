/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package controller.editor;

import java.util.List;

import model.ModelManager;
import model.builders.MapStyleBuilder;
import view.EditorView;
import app.MainRTS;
import controller.Controller;
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

	public EditorGUIController(Nifty nifty, Controller controller) {
		super(controller, nifty);
		drawer = new EditorGUIDrawer(this);
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
	}

	@Override
	public void onStartScreen() {
	}

	@Override
	public void onEndScreen() {
	}

	@NiftyEventSubscriber(pattern = ".*slider")
	public void onSliderChanged(final String id, final SliderChangedEvent event) {
		switch (id) {
			case "sizeslider":
				if (event.getValue() < ModelManager.toolManager.actualTool.pencil.size) {
					ModelManager.toolManager.actualTool.pencil.decRadius();
				} else if (event.getValue() > ModelManager.toolManager.actualTool.pencil.size) {
					ModelManager.toolManager.actualTool.pencil.incRadius();
				}
				break;
			case "strslider":
				ModelManager.toolManager.actualTool.pencil.strength = event.getValue();
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
				ModelManager.toolManager.actualTool.getSet().set(selectionIndices.get(0));
				break;
		}
	}

	@NiftyEventSubscriber(pattern = ".*dropdown")
	public void onDropDownSelectionChanged(final String id, final DropDownSelectionChangedEvent event) {
		if (!event.getDropDown().isEnabled()) {
			return;
		}
		int selectionIndex = event.getSelectionItemIndex();
		MapStyleBuilder builder = ModelManager.lib.getAllMapStyleBuilders().get(selectionIndex);
		if (!ModelManager.battlefield.map.mapStyleID.equals(builder.getId())) {
			ModelManager.battlefield.map.mapStyleID = builder.getId();
			ModelManager.reload();
		}
	}

	public void load() {
		ModelManager.loadBattlefield();
	}

	public void save() {
		ModelManager.saveBattlefield();
	}

	public void newMap() {
		ModelManager.setNewBattlefield();
	}

	public void settings() {
		MainRTS.appInstance.changeSettings();
	}

	public void toggleGrid() {
		((EditorView) ctrl.view).editorRend.toggleGrid();
	}

	public void setCliffTool() {
		ModelManager.toolManager.setCliffTool();
		askRedraw();
	}

	public void setHeightTool() {
		ModelManager.toolManager.setHeightTool();
		askRedraw();
	}

	public void setAtlasTool() {
		ModelManager.toolManager.setAtlasTool();
		askRedraw();
	}

	public void setRampTool() {
		ModelManager.toolManager.setRampTool();
		askRedraw();
	}

	public void setUnitTool() {
		ModelManager.toolManager.setUnitTool();
		askRedraw();
	}

	public void setTrincketTool() {
		ModelManager.toolManager.setTrinketTool();
		askRedraw();
	}

	public void setOperation(String indexString) {
		ModelManager.toolManager.actualTool.setOperation(Integer.parseInt(indexString));
		askRedraw();
	}

	public void setSet(String indexString) {
		if (ModelManager.toolManager.actualTool.hasSet()) {
			ModelManager.toolManager.actualTool.getSet().set(Integer.parseInt(indexString));
		}
		askRedraw();
	}

	public void setRoughMode() {
		ModelManager.toolManager.actualTool.pencil.setRoughMode();
		askRedraw();
	}

	public void setAirbrushMode() {
		ModelManager.toolManager.actualTool.pencil.setAirbrushMode();
		askRedraw();
	}

	public void setNoiseMode() {
		ModelManager.toolManager.actualTool.pencil.setNoiseMode();
		askRedraw();
	}

	public void setSquareShape() {
		ModelManager.toolManager.actualTool.pencil.setSquareShape();
		askRedraw();
	}

	public void setDiamondShape() {
		ModelManager.toolManager.actualTool.pencil.setDiamondShape();
		askRedraw();
	}

	public void setCircleShape() {
		ModelManager.toolManager.actualTool.pencil.setCircleShape();
		askRedraw();
	}
}
