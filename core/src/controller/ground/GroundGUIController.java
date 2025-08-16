/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ground;

import controller.Controller;
import controller.GUIController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import event.ControllerChangeEvent;
import event.EventManager;

/**
 *
 * @author Benoît
 */
public class GroundGUIController extends GUIController {
	public GroundGUIController(Nifty nifty, Controller controller) {
		super(controller, nifty);
	}

	@Override
	public void activate(){
		nifty.gotoScreen("network");
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

	// Network screen button handlers
	public void create(){
		// Start the server and connect as client (host game)
		System.out.println("Starting server and hosting game...");
		try {
			// Start the server in a separate thread
			new Thread(() -> {
				network.server.OpenRTSServer.main(new String[]{});
			}).start();
			
			// Give the server time to start
			Thread.sleep(2000);
			
			// Connect as client to our own server
			if (app.OpenRTSApplication.appInstance != null) {
				app.OpenRTSApplication.appInstance.startClient();
				System.out.println("Server created and client connected!");
			}
		} catch (Exception e) {
			System.err.println("Failed to create game: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void join(){
		// Connect to existing server
		System.out.println("Joining existing game...");
		try {
			if (app.OpenRTSApplication.appInstance != null) {
				app.OpenRTSApplication.appInstance.startClient();
				System.out.println("Connected to game!");
			}
		} catch (Exception e) {
			System.err.println("Failed to join game: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void exit(){
		// Exit the application
		System.out.println("Exit button clicked");
		try {
			if (app.OpenRTSApplication.appInstance != null) {
				app.OpenRTSApplication.appInstance.stopClient();
			}
		} catch (Exception e) {
			// Ignore cleanup errors
		}
		System.exit(0);
	}

	@Override
	public void update() {
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
}
