/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import java.util.List;

import model.Model;
import model.builders.MapStyleBuilder;
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
 *
 * @author Benoît
 */
public class EditorGUIController extends GUIController {

	public EditorGUIController(Nifty nifty, Controller controller) {
		super(controller, nifty);
		drawer = new EditorGUIDrawer(this);
	}

	@Override
	public void activate(){
		nifty.gotoScreen("editor");
		nifty.update();
		askRedraw();
	}

	@Override
	public void update() {
		if(!nifty.getCurrentScreen().getScreenId().equals("editor")) {
			throw new RuntimeException("updating editor screen but is not current screen.");
		}
		if(redrawAsked){
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

	@NiftyEventSubscriber(pattern=".*slider")
	public void onSliderChanged(final String id, final SliderChangedEvent event) {
		switch(id){
			case "sizeslider" :
				if (event.getValue() < Model.toolManager.actualTool.pencil.size) {
					Model.toolManager.actualTool.pencil.decRadius();
				} else if (event.getValue() > Model.toolManager.actualTool.pencil.size) {
					Model.toolManager.actualTool.pencil.incRadius();
				}
				break;
			case "strslider":
				Model.toolManager.actualTool.pencil.strength = event.getValue();
				break;
		}
	}

	@NiftyEventSubscriber(pattern=".*list")
	public void onListSelectionChanged(final String id, final ListBoxSelectionChangedEvent event) {
		List<Integer> selectionIndices = event.getSelectionIndices();
		if(selectionIndices.isEmpty()) {
			return;
		}
		switch(id){
			case "selectionlist":
				Model.toolManager.actualTool.getSet().set(selectionIndices.get(0));
				break;
		}
	}

	@NiftyEventSubscriber(pattern=".*dropdown")
	public void onDropDownSelectionChanged(final String id, final DropDownSelectionChangedEvent event) {
		if(!event.getDropDown().isEnabled()) {
			return;
		}
		int selectionIndex = event.getSelectionItemIndex();
		MapStyleBuilder builder = Model.lib.getAllMapStyleBuilders().get(selectionIndex);
		if (!Model.battlefield.map.mapStyleID.equals(builder.getId())) {
			Model.battlefield.map.mapStyleID = builder.getId();
			Model.reload();
		}
	}


	public void load(){
		Model.loadBattlefield();
	}

	public void save(){
		Model.saveBattlefield();
	}

	public void newMap(){
		Model.setNewBattlefield();
	}

	public void settings(){
		MainRTS.appInstance.changeSettings();
	}

	public void toggleGrid(){
		ctrl.view.editorRend.toggleGrid();
	}
	public void setCliffTool(){
		Model.toolManager.setCliffTool();
		askRedraw();
	}
	public void setHeightTool(){
		Model.toolManager.setHeightTool();
		askRedraw();
	}
	public void setAtlasTool(){
		Model.toolManager.setAtlasTool();
		askRedraw();
	}
	public void setRampTool(){
		Model.toolManager.setRampTool();
		askRedraw();
	}
	public void setUnitTool(){
		Model.toolManager.setUnitTool();
		askRedraw();
	}
	public void setTrincketTool(){
		Model.toolManager.setTrinketTool();
		askRedraw();
	}

	public void setOperation(String indexString){
		Model.toolManager.actualTool.setOperation(Integer.parseInt(indexString));
		askRedraw();
	}

	public void setSet(String indexString){
		if (Model.toolManager.actualTool.hasSet()) {
			Model.toolManager.actualTool.getSet().set(Integer.parseInt(indexString));
		}
		askRedraw();
	}

	public void setRoughMode(){
		Model.toolManager.actualTool.pencil.setRoughMode();
		askRedraw();
	}
	public void setAirbrushMode(){
		Model.toolManager.actualTool.pencil.setAirbrushMode();
		askRedraw();
	}
	public void setNoiseMode(){
		Model.toolManager.actualTool.pencil.setNoiseMode();
		askRedraw();
	}
	public void setSquareShape(){
		Model.toolManager.actualTool.pencil.setSquareShape();
		askRedraw();
	}
	public void setDiamondShape(){
		Model.toolManager.actualTool.pencil.setDiamondShape();
		askRedraw();
	}
	public void setCircleShape(){
		Model.toolManager.actualTool.pencil.setCircleShape();
		askRedraw();
	}
}
