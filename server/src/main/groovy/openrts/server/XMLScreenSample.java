package openrts.server;

import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;

public class XMLScreenSample extends AbstractAppState {
	Screen screen;
	Window inventory;

	public XMLScreenSample(Screen screen) {
		// Store a pointer to the screen
		this.screen = screen;
		// Call the xml parser to load your new components
		screen.parseLayout("Interface/Inventory.gui.xml", this);

		// Here we can grab pointers to the loaded elements
		inventory = (Window)screen.getElementById("InventoryWindowID");
		// We'll hide the window initially so we can use an
		// effect to show it from the initialize method
		inventory.hide();
		// grab more pointers to other elements / child elements
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		// Useful place for running load effects
		inventory.show();
		((Menu) screen.getElementById("Menu1")).showWithEffect();
	}

	@Override
	public void update(float tpf) {
		//TODO: implement behavior during runtime
	}

	@Override
	public void cleanup() {
		super.cleanup();

		// We can alter the effect to destroy our inventory window
		// when we unload the AppState
		Effect hide = new Effect(Effect.EffectType.FadeOut, Effect.EffectEvent.Hide, 0.25f);
		hide.setDestroyOnHide(true);

		if (inventory.getIsVisible()) {
			// inventory.setEffect(hide);
			inventory.hideWithEffect();
		} else {
			screen.removeElement(inventory);
		}

		// Now our UI component scene fades out when the AppState in unloaded.
	}

	public void invSubmitButtonClick(MouseButtonEvent evt, boolean isToggled) {
		// We'll show the AlertBox we defined in the layout when this button is clicked
		// ((AlertBox) screen.getElementById("Alert1")).showWithEffect();

		((Menu) screen.getElementById("Menu1")).showWithEffect();
	}
}