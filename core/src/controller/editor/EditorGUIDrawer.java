/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package controller.editor;

import static model.editor.Pencil.SHAPE.Circle;
import static model.editor.Pencil.SHAPE.Diamond;
import static model.editor.Pencil.SHAPE.Square;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.builders.entity.MapStyleBuilder;
import model.builders.entity.definitions.BuilderManager;
import model.editor.Pencil;
import model.editor.ToolManager;
import model.editor.tools.AtlasTool;
import model.editor.tools.CliffTool;
import model.editor.tools.HeightTool;
import model.editor.tools.RampTool;
import model.editor.tools.Tool;
import model.editor.tools.UnitTool;
import controller.GUIController;
import controller.GUIDrawer;
import de.lessvoid.nifty.controls.Slider;

/**
 * @author Benoît
 */
public class EditorGUIDrawer extends GUIDrawer {
	private static final String OPERATION_BUTTON_ID_PREFIX = "operation";
	private static final String CLIFF_TOOL_BUTTON_ID = "clifftool";
	private static final String HEIGHT_TOOL_BUTTON_ID = "heighttool";
	private static final String ATLAS_TOOL_BUTTON_ID = "atlastool";
	private static final String RAMP_TOOL_BUTTON_ID = "ramptool";
	private static final String UNIT_TOOL_BUTTON_ID = "unittool";
	private static final String ICON_SET_PANEL_ID = "iconsetpanel";
	private static final String LIST_SET_PANEL_ID = "listsetpanel";
	private static final String SELECTION_LIST_ID = "selectionlist";
	private static final String DROPDOWN_STYLE_ID = "mapstyledropdown";

	private static final String SET_BUTTON_ID_PREFIX = "set";
	private static final String SQUARE_BUTTON_ID = "square";
	private static final String DIAMOND_BUTTON_ID = "diamond";
	private static final String CIRCLE_BUTTON_ID = "circle";
	private static final String ROUGH_BUTTON_ID = "rough";
	private static final String AIRBRUSH_BUTTON_ID = "airbrush";
	private static final String NOISE_BUTTON_ID = "noise";
	private static final String _BUTTON_ID = "";

	public EditorGUIDrawer(GUIController guiCtrl) {
		super(guiCtrl);
	}

	@Override
	public void draw() {
		drawToolPanel();
		drawOperationPanel();
		drawSetPanel();
		drawPencilPanel();
		drawMapStyleDropDown();
	}

	private void drawToolPanel() {
		Tool tool = ToolManager.getActualTool();
		if (tool instanceof CliffTool) {
			maintainButton(CLIFF_TOOL_BUTTON_ID);
		} else {
			releaseButton(CLIFF_TOOL_BUTTON_ID);
		}

		if (tool instanceof HeightTool) {
			maintainButton(HEIGHT_TOOL_BUTTON_ID);
		} else {
			releaseButton(HEIGHT_TOOL_BUTTON_ID);
		}

		if (tool instanceof AtlasTool) {
			maintainButton(ATLAS_TOOL_BUTTON_ID);
		} else {
			releaseButton(ATLAS_TOOL_BUTTON_ID);
		}

		if (tool instanceof RampTool) {
			maintainButton(RAMP_TOOL_BUTTON_ID);
		} else {
			releaseButton(RAMP_TOOL_BUTTON_ID);
		}

		if (tool instanceof UnitTool) {
			maintainButton(UNIT_TOOL_BUTTON_ID);
		} else {
			releaseButton(UNIT_TOOL_BUTTON_ID);
		}
	}

	private void drawOperationPanel() {
		Tool tool = ToolManager.getActualTool();
		for (int i = 0; i < 3; i++) {
			if (!tool.getOperation(i).isEmpty()) {
				getElement(OPERATION_BUTTON_ID_PREFIX + i).show();
				changeButtonText(OPERATION_BUTTON_ID_PREFIX + i, tool.getOperation(i));
				if (tool.getActualOperation().equals(tool.getOperation(i))) {
					maintainButton(OPERATION_BUTTON_ID_PREFIX + i);
				} else {
					releaseButton(OPERATION_BUTTON_ID_PREFIX + i);
				}
			} else {
				getElement(OPERATION_BUTTON_ID_PREFIX + i).hide();
			}
		}

	}

	private void drawSetPanel() {
		Tool tool = ToolManager.getActualTool();
		if (tool.hasSet()) {
			if (tool.getSet().hasIcons()) {
				drawIconSetPanel();
			} else {
				drawListSetPanel();
			}
		} else {
			getElement(ICON_SET_PANEL_ID).hide();
			getElement(LIST_SET_PANEL_ID).hide();
		}
	}

	private void drawIconSetPanel() {
		Tool tool = ToolManager.getActualTool();
		getElement(ICON_SET_PANEL_ID).show();
		getElement(LIST_SET_PANEL_ID).hide();
		for (int i = 0; i < 16; i++) {
			if (i < tool.getSet().getCount() && tool.getSet().getAsset(i) != null) {
				getElement(SET_BUTTON_ID_PREFIX + i).show();
				setBackground(SET_BUTTON_ID_PREFIX + i, tool.getSet().getAsset(i));
				if (tool.getSet().actual == i) {
					maintainButton(SET_BUTTON_ID_PREFIX + i);
				} else {
					releaseButton(SET_BUTTON_ID_PREFIX + i);
				}
			} else {
				getElement(SET_BUTTON_ID_PREFIX + i).hide();
			}
		}
	}

	private void drawListSetPanel() {
		Tool tool = ToolManager.getActualTool();
		getElement(LIST_SET_PANEL_ID).show();
		getElement(ICON_SET_PANEL_ID).hide();
		fillList(SELECTION_LIST_ID, tool.getSet().getAllAssets());
	}

	private void drawMapStyleDropDown() {
		List<MapStyleBuilder> builders = BuilderManager.getAllMapStyleBuilders();
		List<String> ids = new ArrayList<>();
		for (MapStyleBuilder b : builders) {
			ids.add(b.getId());
		}
		int selIndex = ids.indexOf(ModelManager.getBattlefield().getMap().getMapStyleID());
		fillDropDown(DROPDOWN_STYLE_ID, ids, selIndex);
	}

	private void drawPencilPanel() {
		getElement("pencilpanel").hide();

		Pencil pencil = ToolManager.getActualTool().pencil;

		if (pencil.sizeIncrement != 0) {
			getElement("pencilpanel").show();

			// shape buttons
			if (pencil.shape.equals(Circle)) {
				maintainButton(CIRCLE_BUTTON_ID);
			} else {
				releaseButton(CIRCLE_BUTTON_ID);
			}
			if (pencil.shape.equals(Square)) {
				maintainButton(SQUARE_BUTTON_ID);
			} else {
				releaseButton(SQUARE_BUTTON_ID);
			}
			if (pencil.shape.equals(Diamond)) {
				maintainButton(DIAMOND_BUTTON_ID);
			} else {
				releaseButton(DIAMOND_BUTTON_ID);
			}

			// mode buttons
			if (pencil.mode.equals(Pencil.MODE.Rough)) {
				maintainButton(ROUGH_BUTTON_ID);
			} else {
				releaseButton(ROUGH_BUTTON_ID);
			}
			if (pencil.mode.equals(Pencil.MODE.Airbrush)) {
				maintainButton(AIRBRUSH_BUTTON_ID);
			} else {
				releaseButton(AIRBRUSH_BUTTON_ID);
			}
			if (pencil.mode.equals(Pencil.MODE.Noise)) {
				maintainButton(NOISE_BUTTON_ID);
			} else {
				releaseButton(NOISE_BUTTON_ID);
			}

			Slider sizeSlider = getSlider("sizeslider");
			sizeSlider.setMin(1);// (float)pencil.sizeIncrement);
			sizeSlider.setMax(Pencil.MAX_SIZE);
			sizeSlider.setStepSize(1);// (float)pencil.sizeIncrement);
			sizeSlider.setValue((float) pencil.size);

			if (pencil.mode == Pencil.MODE.Unique) {
				getElement("pencilmodepanel").hide();
			} else {
				getElement("pencilmodepanel").show();
			}

			if (pencil.strengthIncrement == 0) {
				getElement("strpanel").hide();
			} else {
				getElement("strpanel").show();
				Slider strengthSlider = getSlider("strslider");
				strengthSlider.setMin(0.1f);
				strengthSlider.setMax(1);
				strengthSlider.setStepSize((float) pencil.strengthIncrement);
				strengthSlider.setValue((float) pencil.strength);
			}
		}
	}

	private void maintainButton(String id) {
		setButtonCustomEffet(id, true);
		changeButtonTextColor(id, Color.green);
	}

	private void releaseButton(String id) {
		setButtonCustomEffet(id, false);
		changeButtonTextColor(id, Color.white);
	}

}
