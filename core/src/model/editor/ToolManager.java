package model.editor;

import geometry.geom2d.Point2D;
import model.ModelManager;
import model.builders.entity.definitions.BuilderManager;
import model.editor.engines.Sower;
import model.editor.tools.AtlasTool;
import model.editor.tools.CliffTool;
import model.editor.tools.HeightTool;
import model.editor.tools.RampTool;
import model.editor.tools.Tool;
import model.editor.tools.TrinketTool;
import model.editor.tools.UnitTool;
import util.MapArtisanManager;
import brainless.openrts.event.EventManager;
import brainless.openrts.event.client.SetToolEvent;
import brainless.openrts.event.client.UpdateGroundAtlasEvent;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Benoît
 */
public class ToolManager {
	private static String pointedSpatialLabel;
	private static long pointedSpatialEntityId;

	@Inject
	private HeightTool heightTool;
	@Inject
	private CliffTool cliffTool;
	@Inject
	private AtlasTool atlasTool;
	@Inject
	private RampTool rampTool;
	@Inject
	private UnitTool unitTool;
	@Inject
	private TrinketTool trinketTool;

	private static Tool actualTool;

	private static double delay = 0;
	private static long lastAction = 0;
	
	@Inject
	private static Sower sower;
	
	@Inject
	public ToolManager(Injector injector) {
		actualTool = injector.getInstance(CliffTool.class);

		new Thread(sower).start();
	}

	public void setCliffTool() {
		actualTool = cliffTool;
		EventManager.post(new SetToolEvent());
	}

	public void setHeightTool() {
		actualTool = heightTool;
		EventManager.post(new SetToolEvent());
	}

	public void setAtlasTool() {
		actualTool = atlasTool;
		EventManager.post(new SetToolEvent());
	}

	public void setRampTool() {
		actualTool = rampTool;
		EventManager.post(new SetToolEvent());
	}

	public void setUnitTool() {
		actualTool = unitTool;
		EventManager.post(new SetToolEvent());
	}

	public void setTrinketTool() {
		actualTool = trinketTool;
		EventManager.post(new SetToolEvent());
	}

	public void toggleSet() {
		if (actualTool.hasSet()) {
			actualTool.getSet().toggle();
		}
	}

	public void toggleOperation() {
		actualTool.toggleOperation();
	}

	public void analogPrimaryAction() {
		if (actualTool.isAnalog()) {
			if (lastAction + delay < System.currentTimeMillis()) {
				lastAction = System.currentTimeMillis();
				actualTool.primaryAction();
			}
		}
	}

	public void primaryAction() {
		if (!actualTool.isAnalog()) {
			actualTool.primaryAction();
		}
	}

	public void analogSecondaryAction() {
		if (actualTool.isAnalog()) {
			if (lastAction + delay < System.currentTimeMillis()) {
				lastAction = System.currentTimeMillis();
				actualTool.secondaryAction();
			}
		}
	}

	public void secondaryAction() {
		if (!actualTool.isAnalog()) {
			actualTool.secondaryAction();
		}
	}

	public void toggleSower(){
		synchronized (sower) {
			if(sower.isPaused()){
				sower.unpause();
			}else{
				sower.askForPause();
			}
		}
	}

	public void stepSower(){
		if(sower.isPaused()){
			sower.stepByStep();
		}
	}
	
	public void killSower(){
		sower.destroy();
	}
	
	public void updateGroundAtlas() {
		EventManager.post(new UpdateGroundAtlasEvent());
	}

	public void updatePencilsPos(Point2D pos) {
		actualTool.pencil.setPos(pos);
	}

	public void releasePencils() {
		actualTool.pencil.release();
	}

	public String getPointedSpatialLabel() {
		return pointedSpatialLabel;
	}

	public void setPointedSpatialLabel(String pointedSpatialLabel) {
		ToolManager.pointedSpatialLabel = pointedSpatialLabel;
	}

	public long getPointedSpatialEntityId() {
		return pointedSpatialEntityId;
	}

	public void setPointedSpatialEntityId(long pointedSpatialEntityId) {
		ToolManager.pointedSpatialEntityId = pointedSpatialEntityId;
	}

	public Tool getActualTool() {
		return actualTool;
	}
	
}

