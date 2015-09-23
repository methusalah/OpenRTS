package brainless.openrts.app.example.states.gui;

import javax.inject.Inject;

import tonegod.gui.controls.windows.LoginBox;
import brainless.openrts.app.example.states.AppStateCommon;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;


public class UserLoginAppState extends AppStateCommon {
	LoginBox loginWindow;
	String user;

	@Inject
	public UserLoginAppState() {
		super();
		displayName = "User Login";
		show = true;
	}

	//	@Override
	//	public void initialize(AppStateManager stateManager, Application app) {
	//		super.initialize(stateManager, app);
	//
	//		initLoginWindow();
	//	}

	public void finalizeUserLogin(String user) {
		main.sucessfullLoggedIn(user);
	}

	@Override
	public void updateState(float tpf) {
	}

	@Override
	public void cleanupState() {
		screen.removeElement(loginWindow);
	}

	@Override
	public void reshape() {
	}

	@Override
	protected void initState() {
		if (!initialized) {
			loginWindow = new LoginBox(screen,
					"loginWindow",
					new Vector2f((Float) (screen.getWidth()/2-175),(Float)(screen.getHeight()/2-125))) {
						@Override
						public void onButtonLoginPressed(MouseButtonEvent evt, boolean toggled) {
							// Some call to the server to log the client in
							//@TODO handle passowrd aswell
							finalizeUserLogin(loginWindow.getTextUserName());

						}

						@Override
						public void onButtonCancelPressed(MouseButtonEvent arg0, boolean arg1) {
							System.exit(0);

						}
					};
			loginWindow.setTextUserName("peter");
			loginWindow.getPassword().setIsEnabled(false);
			screen.addElement(loginWindow);


			initialized = true;
		}
	}
}