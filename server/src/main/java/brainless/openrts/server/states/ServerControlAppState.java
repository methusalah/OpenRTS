package brainless.openrts.server.states;

import java.util.logging.Logger;

import org.lwjgl.opengl.Display;

import tonegod.gui.controls.lists.SelectList;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.core.Screen;
import tonegod.gui.core.layouts.FlowLayout;
import brainless.openrts.event.ClientLoggedOutEvent;
import brainless.openrts.event.ClientTrysToLoginEvent;
import brainless.openrts.event.EventManager;
import brainless.openrts.event.ServerEvent;
import brainless.openrts.event.network.NetworkEvent;
import brainless.openrts.server.ServerMain;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;


public class ServerControlAppState extends AbstractAppState {

	private static final Logger logger = Logger.getLogger(ServerControlAppState.class.getName());
	private ServerMain app;
	private Screen screen;

	private Panel logPanel;

	private SelectList users;
	private ScrollArea eventlog, chatlog;

	@Inject
	public ServerControlAppState( ServerMain app , Screen screen) {
		this.app = app;
		this.screen = screen;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		initControlWindow();
		EventManager.register(this);
	}

	public void initControlWindow() {

		screen.setUseToolTips(true);

		logPanel = new Panel(screen, "LogPanel", new Vector2f(0,0));
		logPanel.setDimensions(Display.getWidth(),Display.getHeight());
		logPanel.setIsResizable(false);
		logPanel.setIsMovable(false);

		FlowLayout layout = new FlowLayout(screen, "margins 8 8 8 8", "padding 25 25 25 25");
		logPanel.setLayout(layout);

		screen.addElement(logPanel);

		users = new SelectList(screen, "UserList", Vector2f.ZERO) {
					public void onChange() {
						int idx = users.getSelectedIndexes().get(0);
						logger.info("select user: " + users.getListItems().get(idx).getCaption());
					}
				};
		users.setIsMultiselect(false);
		users.setDimensions(100, (Display.getHeight() / 2) - 16);
		users.setIsResizable(false);
		users.setIsMovable(false);
		users.setToolTipText("The connected users are displayed here");
		logPanel.addChild(users);
		logPanel.getLayoutHints().set("wrap");

		eventlog = new ScrollArea(screen, "EventLog", new Vector2f(users.getWidth(), 0), new Vector2f(200, logPanel.getHeight() - 16), true);
		eventlog.setIsResizable(false);
		eventlog.setIsMovable(false);
		eventlog.setToolTipText("the events are displayed here");
		logPanel.addChild(eventlog);
		eventlog.setText("test2");
//		eventlog.setIsScrollable(false);

		chatlog = new ScrollArea(screen, "ChatLog", new Vector2f(users.getWidth() + eventlog.getWidth(), 0),new Vector2f(300 - 25, logPanel.getHeight() -16), true);
		chatlog.setIsResizable(false);
		chatlog.setIsMovable(false);
		chatlog.setToolTipText("Chatmessages are displayed here");
		logPanel.addChild(chatlog);

		logPanel.getLayout().layoutChildren();
	}

	@Override
	public void cleanup() {
		super.cleanup();

		screen.removeElement(logPanel);
		EventManager.unregister(this);
	}

	public void finalizeUserLogin() {
		// Some call to your app to unload this AppState and load the next AppState
		//app.switchToServerControlAppStates();
		logger.info("Login was pressed");
	}

	@Subscribe
	private void logNetworkEvents(NetworkEvent evt) {
		eventlog.setText("" + evt + "\n" + eventlog.getText());
	}


	@Subscribe
	private void logSeverEvents(ServerEvent evt) {
		eventlog.setText("" + evt + "\n" + eventlog.getText());
	}

	@Subscribe
	private void logSeverEvents(ClientTrysToLoginEvent evt) {
		users.addListItem(evt.getUser(), evt);
	}

	@Subscribe
	private void logSeverEvents(ClientLoggedOutEvent evt) {
		users.removeListItem(evt.getUser());
	}
}
