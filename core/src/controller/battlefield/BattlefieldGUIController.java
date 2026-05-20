/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.battlefield;

import java.util.ArrayList;
import java.util.List;

import model.CommandManager;
import model.battlefield.army.Unity;
import model.battlefield.army.components.Unit;
import controller.Controller;
import controller.GUIController;
import controller.Reporter;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import event.ControllerChangeEvent;
import event.EventManager;

import view.jme.LemurHUD;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import app.OpenRTSApplication;
import model.ModelManager;
import com.simsilica.lemur.Container;

/**
 *
 * @author Benoît
 */
public class BattlefieldGUIController extends GUIController {
	private LemurHUD lemurHUD;
	private Camera minimapCam;
	private ViewPort minimapViewPort;

	public BattlefieldGUIController(Nifty nifty, Controller controller) {
		super(controller, nifty);
		EventManager.register(this);
	}

	@Override
	public void activate(){
		nifty.gotoScreen("hud");
		
		// Hide legacy nifty overlay elements
		de.lessvoid.nifty.elements.Element bottomL = getElement("bottomlayer");
		if (bottomL != null) {
			bottomL.hide();
		}
		de.lessvoid.nifty.elements.Element statusL = getElement("statuslayer");
		if (statusL != null) {
			statusL.hide();
		}
		
		// Initialize and show Lemur HUD
		if (lemurHUD == null) {
			lemurHUD = new LemurHUD(
				OpenRTSApplication.appInstance.getGuiNode(), 
				OpenRTSApplication.appInstance, 
				this
			);
		}
		lemurHUD.show();
	}

	public void selectAll(){
		CommandManager.selectAll();
	}

	public void orderStop(){
		CommandManager.orderStop();
	}

	public void orderHold(){
		CommandManager.orderHold();
	}

	public void orderAttack(){
		CommandManager.setMoveAttack();
	}

	public void orderSpeedBoost(){
		CommandManager.orderSpeedBoost();
	}

	// Tab switching methods
	public void switchToGameMode(){
		EventManager.post(new ControllerChangeEvent(0));
	}

	public void switchToEditorMode(){
		EventManager.post(new ControllerChangeEvent(1));
	}

	public void switchToNetworkMode(){
		EventManager.post(new ControllerChangeEvent(2));
	}

	private String getGroupHealthString() {
		int current = 0;
		int max = 0;
		for (Unit u : CommandManager.selection) {
			current += u.health;
			max += u.maxHealth;
		}
		int percent = (max > 0) ? (int)((double)current / max * 100) : 0;
		return current + " / " + max + " (" + percent + "%)";
	}

	private String getSelectionBreakdown() {
		java.util.Map<String, Integer> counts = new java.util.HashMap<>();
		for (Unit u : CommandManager.selection) {
			counts.put(u.UIName, counts.getOrDefault(u.UIName, 0) + 1);
		}
		java.util.List<String> list = new java.util.ArrayList<>();
		for (java.util.Map.Entry<String, Integer> entry : counts.entrySet()) {
			list.add(entry.getValue() + " " + entry.getKey() + (entry.getValue() > 1 ? "s" : ""));
		}
		return String.join(", ", list);
	}

	@Override
	public void update() {
		if(!nifty.isActive("interface/screen.xml", "hud")) {
			if (lemurHUD != null) {
				lemurHUD.hide();
			}
			cleanupMinimap();
			return;
		}

		// Update Lemur HUD selection details
		if (lemurHUD != null) {
			if (CommandManager.selection.size() == 1) {
				Unit u = CommandManager.selection.get(0);
				lemurHUD.updateSelection(
					Reporter.getName(u),
					Reporter.getHealth(u),
					Reporter.getState(u),
					Reporter.getOrder(u),
					Reporter.getHolding(u),
					true
				);
			} else if (CommandManager.selection.size() > 1) {
				lemurHUD.updateSelection(
					CommandManager.selection.size() + " Units Selected",
					"Group Health: " + getGroupHealthString(),
					"Faction: " + CommandManager.selection.get(0).faction.getName(),
					"Composition: " + getSelectionBreakdown(),
					"Use Command Card / Hotkeys to issue orders.",
					true
				);
			} else {
				lemurHUD.updateSelection("", "", "", "", "", false);
			}
		}

		// Setup/Update Minimap Viewport
		if (minimapViewPort == null && ModelManager.getBattlefield() != null && ModelManager.getBattlefield().getMap() != null) {
			float xSize = ModelManager.getBattlefield().getMap().xSize();
			float ySize = ModelManager.getBattlefield().getMap().ySize();
			
			minimapCam = new Camera(128, 128);
			minimapCam.setParallelProjection(true);
			minimapCam.setLocation(new Vector3f(xSize / 2f, ySize / 2f, 100f));
			minimapCam.lookAt(new Vector3f(xSize / 2f, ySize / 2f, 0f), Vector3f.UNIT_Y);
			
			float halfX = xSize / 2f;
			float halfY = ySize / 2f;
			minimapCam.setFrustum(1.0f, 1000.0f, -halfX, halfX, halfY, -halfY);
			
			RenderManager rm = OpenRTSApplication.appInstance.getRenderManager();
			minimapViewPort = rm.createMainView("Minimap Viewport", minimapCam);
			minimapViewPort.setClearFlags(true, true, true);
			minimapViewPort.attachScene(ctrl.spatialSelector.view.getRootNode());
		}

		if (lemurHUD != null && minimapViewPort != null) {
			Container mapC = lemurHUD.getMapOverlayContainer();
			if (mapC != null) {
				Camera mainCam = OpenRTSApplication.appInstance.getCamera();
				float scrW = mainCam.getWidth();
				float scrH = mainCam.getHeight();
				
				Vector3f pos = mapC.getLocalTranslation();
				Vector3f size = mapC.getPreferredSize();
				
				float left = pos.x / scrW;
				float right = (pos.x + size.x) / scrW;
				float bottom = (pos.y - size.y) / scrH;
				float top = pos.y / scrH;
				
				minimapCam.setViewPort(left + 0.005f, right - 0.005f, bottom + 0.005f, top - 0.005f);
			}
		}

		// Campaign script ticking
		if (model.campaign.CampaignManager.isCampaignActive()) {
			model.campaign.MissionScript script = model.campaign.CampaignManager.getActiveScript();
			if (script != null) {
				float tpf = OpenRTSApplication.appInstance.getTimer().getTimePerFrame();
				script.update(tpf, ModelManager.getBattlefield());
				
				if (script.isVictoryAchieved()) {
					model.campaign.CampaignManager.setCampaignActive(false);
					lemurHUD.showEndScreen(true);
				} else if (script.isDefeatTriggered()) {
					model.campaign.CampaignManager.setCampaignActive(false);
					lemurHUD.showEndScreen(false);
				}
			}
		}
	}

	@com.google.common.eventbus.Subscribe
	public void handleDialogue(event.DialogueEvent event) {
		if (lemurHUD != null) {
			lemurHUD.showDialogue(event.speaker, event.text);
		}
	}

	@com.google.common.eventbus.Subscribe
	public void handleObjective(event.ObjectiveEvent event) {
		if (lemurHUD != null) {
			lemurHUD.updateObjective(event.objective, event.active, event.completed);
		}
	}

	public void nextCampaignMission() {
		model.campaign.CampaignManager.nextMission();
	}

	public void restartCampaignMission() {
		model.campaign.CampaignManager.restartMission();
	}

	@Override
	public void onStartScreen() {
	}

	@Override
	public void bind(de.lessvoid.nifty.Nifty nifty, de.lessvoid.nifty.screen.Screen screen) {
	}

	public void cleanupMinimap() {
		if (minimapViewPort != null) {
			RenderManager rm = OpenRTSApplication.appInstance.getRenderManager();
			rm.removeMainView(minimapViewPort);
			minimapViewPort = null;
			minimapCam = null;
		}
	}

	@Override
	public void onEndScreen() {
		cleanupMinimap();
		if (lemurHUD != null) {
			lemurHUD.hide();
		}
	}
}
