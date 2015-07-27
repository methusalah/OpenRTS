package model.editor;

import model.battlefield.abstractComps.FieldComp;
import model.editor.engines.Sower;
import model.editor.tools.AtlasTool;
import model.editor.tools.CliffTool;
import model.editor.tools.HeightTool;
import model.editor.tools.RampTool;
import model.editor.tools.Tool;
import model.editor.tools.TrinketTool;
import model.editor.tools.UnitTool;
import event.EventManager;
import event.SetToolEvent;
import event.UpdateGroundAtlasEvent;
import geometry.geom2d.Point2D;

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
	private static Sower sower = new Sower();
	
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

		new Thread(sower).start();
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

	public static void toggleSower(){
		synchronized (sower) {
			if(sower.isPaused()){
				sower.unpause();
			}else{
				sower.askForPause();
			}
		}
	}

	public static void stepSower(){
		if(sower.isPaused()){
			sower.stepByStep();
		}
	}
	
	public static void killSower(){
		sower.destroy();
	}
	
	public static void updateGroundAtlas() {
		EventManager.post(new UpdateGroundAtlasEvent());
	}

	public static void updatePencilsPos(Point2D pos) {
		actualTool.pencil.setPos(pos);
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
