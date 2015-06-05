/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.parcel.ParcelManager;
import model.battlefield.map.parcel.ParcelMesh;
import model.editor.tools.AtlasTool;
import model.editor.tools.CliffTool;
import model.editor.tools.HeightTool;
import model.editor.tools.RampTool;
import model.editor.tools.Tool;
import model.editor.tools.TrinketTool;
import model.editor.tools.UnitTool;
import event.EventManager;
import event.ParcelUpdateEvent;
import event.SetToolEvent;
import event.TilesEvent;
import event.UpdateGroundAtlasEvent;
import geometry.geom2d.Point2D;
import geometry.tools.LogUtil;

/**
 * @author Benoît
 */
public class ToolManager {
	private static String pointedSpatialLabel;
	private static long pointedSpatialEntityId;

	private static HeightTool heightTool;
	private static CliffTool cliffTool;
	private static AtlasTool atlasTool;
	private static RampTool rampTool;
	private static UnitTool unitTool;
	private static TrinketTool trinketTool;

	private static Tool actualTool;

	private static double delay = 0;
	private static long lastAction = 0;
	
	private ToolManager() {

	}

	static {
		setHeightTool(new HeightTool());
		setCliffTool(new CliffTool());
		setAtlasTool(new AtlasTool());
		setRampTool(new RampTool());
		unitTool = new UnitTool();
		trinketTool = new TrinketTool();

		actualTool = getCliffTool();
	}

	public static void setCliffTool() {
		actualTool = cliffTool;
		EventManager.post(new SetToolEvent());
	}

	public static void setHeightTool() {
		actualTool = heightTool;
		EventManager.post(new SetToolEvent());
	}

	public static void setAtlasTool() {
		actualTool = atlasTool;
		EventManager.post(new SetToolEvent());
	}

	public static void setRampTool() {
		actualTool = rampTool;
		EventManager.post(new SetToolEvent());
	}

	public static void setUnitTool() {
		actualTool = unitTool;
		EventManager.post(new SetToolEvent());
	}

	public static void setTrinketTool() {
		actualTool = trinketTool;
		EventManager.post(new SetToolEvent());
	}

	public static void toggleSet() {
		if (actualTool.hasSet()) {
			actualTool.getSet().toggle();
		}
	}

	public static void toggleOperation() {
		actualTool.toggleOperation();
	}

	public static void analogPrimaryAction() {
		if (actualTool.isAnalog()) {
			if (lastAction + delay < System.currentTimeMillis()) {
				// LogUtil.logger.info((System.currentTimeMillis()-lastAction)+" ms since last call");
				lastAction = System.currentTimeMillis();
				actualTool.primaryAction();
			}
		}
	}

	public static void primaryAction() {
		if (!actualTool.isAnalog()) {
			actualTool.primaryAction();
		}
	}

	public static void analogSecondaryAction() {
		if (actualTool.isAnalog()) {
			if (lastAction + delay < System.currentTimeMillis()) {
				lastAction = System.currentTimeMillis();
				actualTool.secondaryAction();
			}
		}
	}

	public static void secondaryAction() {
		if (!actualTool.isAnalog()) {
			actualTool.secondaryAction();
		}
	}

	public static void updateTiles(List<Tile> tiles) {
		List<Tile> extended = getExtendedZone(tiles);

		for (Tile t : extended) {
			int minLevel = t.level;
			int maxLevel = t.level;
			for (Tile n : ModelManager.getBattlefield().getMap().get8Around(t)) {
				maxLevel = Math.max(maxLevel, n.level);
			}
			if (t.hasCliff()) {
				t.unsetCliff();
			}

			if (minLevel != maxLevel) {
				t.setCliff(minLevel, maxLevel);
			}
		}

		for (Tile t : extended) {
			for (Cliff c : t.getCliffs()) {
				c.connect(ModelManager.getBattlefield().getMap());
			}
		}
		for (Tile t : extended) {
			for (Cliff c : t.getCliffs()) {
				getCliffTool().buildShape(c);
			}
		}
		EventManager.post(new TilesEvent(extended));
		updateParcelsForExtended(tiles);
	}

	public static void updateParcelsForExtended(List<Tile> tiles) {
		List<ParcelMesh> toUpdate = ParcelManager.updateParcelsFor(tiles);
		EventManager.post(new ParcelUpdateEvent(toUpdate));
	}

	public static void updateParcels(List<Tile> tiles) {
		updateParcelsForExtended(getExtendedZone(tiles));
	}

	private static List<Tile> getExtendedZone(List<Tile> tiles) {
		List<Tile> res = new ArrayList<>();
		res.addAll(tiles);
		for (Tile t : tiles) {
			for (Tile n : ModelManager.getBattlefield().getMap().get8Around(t)) {
				if (!res.contains(n)) {
					res.add(n);
				}
			}
		}
		return res;
	}

	public static void updateGroundAtlas() {
		EventManager.post(new UpdateGroundAtlasEvent());
		// notifyListeners("ground", new ArrayList<Tile>());
	}

	// private void notifyListeners(String command, Object o) {
	// ActionEvent event = new ActionEvent(o, 0, command);
	// for (ActionListener l : listeners) {
	// l.actionPerformed(event);
	// }
	// }

	public static void updatePencilsPos(Point2D pos) {
		actualTool.pencil.setPos(pos);
		// updateTiles(actualTool.pencil.getTiles());
	}

	public static void releasePencils() {
		actualTool.pencil.release();
	}

	public String getPointedSpatialLabel() {
		return pointedSpatialLabel;
	}

	public static void setPointedSpatialLabel(String pointedSpatialLabel) {
		ToolManager.pointedSpatialLabel = pointedSpatialLabel;
	}

	public static long getPointedSpatialEntityId() {
		return pointedSpatialEntityId;
	}

	public static void setPointedSpatialEntityId(long pointedSpatialEntityId) {
		ToolManager.pointedSpatialEntityId = pointedSpatialEntityId;
	}

	public static Tool getActualTool() {
		return actualTool;
	}

	public static CliffTool getCliffTool() {
		return cliffTool;
	}

	public static void setCliffTool(CliffTool cliffTool) {
		ToolManager.cliffTool = cliffTool;
	}

	public static RampTool getRampTool() {
		return rampTool;
	}

	public static void setRampTool(RampTool rampTool) {
		ToolManager.rampTool = rampTool;
	}

	public static HeightTool getHeightTool() {
		return heightTool;
	}

	public static void setHeightTool(HeightTool heightTool) {
		ToolManager.heightTool = heightTool;
	}

	public static AtlasTool getAtlasTool() {
		return atlasTool;
	}

	public static void setAtlasTool(AtlasTool atlasTool) {
		ToolManager.atlasTool = atlasTool;
	}
}
