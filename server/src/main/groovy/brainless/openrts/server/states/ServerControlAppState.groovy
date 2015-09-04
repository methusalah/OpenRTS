package brainless.openrts.server.states

import groovy.transform.CompileStatic

import java.util.logging.Logger

import org.lwjgl.opengl.Display

import tonegod.gui.controls.lists.SelectList
import tonegod.gui.controls.scrolling.ScrollArea
import tonegod.gui.controls.windows.Panel
import tonegod.gui.core.Screen
import tonegod.gui.core.layouts.FlowLayout
import brainless.openrts.event.ClientDisconnectedEvent
import brainless.openrts.event.ClientLoggedOutEvent
import brainless.openrts.event.ClientTrysToLoginEvent
import brainless.openrts.event.EventManager
import brainless.openrts.event.ServerEvent
import brainless.openrts.event.network.NetworkEvent
import brainless.openrts.server.ServerMain

import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import com.jme3.app.Application
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.math.Vector2f

@CompileStatic
class ServerControlAppState extends AbstractAppState {

	static final Logger logger = Logger.getLogger(ServerControlAppState.class.getName());
	ServerMain app;
	Screen screen;

	Panel logPanel
	
	SelectList users
	ScrollArea eventlog, chatlog;

	@Inject
	ServerControlAppState( ServerMain app , Screen screen) {
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
		
		screen.useToolTips = true
		
		logPanel = new Panel(screen, "LogPanel", new Vector2f(0,0))
		logPanel.setDimensions(Display.width,200)
		logPanel.isResizable = false
		logPanel.isMovable = false
	
		FlowLayout layout = new FlowLayout(screen, "margins 8 8 8 8", "padding 25 25 25 25")
		logPanel.layout = layout
	
		
		screen.addElement(logPanel)
		
		users = new SelectList(screen, "UserList", Vector2f.ZERO) {
			void onChange() {
				def idx = users.selectedIndexes.first()
				logger.info("select user: " + users.listItems.get(idx).caption)
			}
		}
		users.isMultiselect = false
		users.setDimensions(100, logPanel.height - 16)
		users.isResizable = false
		users.isMovable = false
		users.setToolTipText("The connected users are displayed here")
		logPanel.addChild(users)
		users.addListItem("test","test")
		
		eventlog = new ScrollArea(screen, "EventLog", new Vector2f(users.width, 0), new Vector2f(200, logPanel.height - 16), true)
		eventlog.isResizable = false
		eventlog.isMovable = false
		eventlog.toolTipText = "the events are displayed here"
		logPanel.addChild(eventlog)
		eventlog.setText("test2")
		eventlog.isScrollable = false
		
		chatlog = new ScrollArea(screen, "ChatLog", new Vector2f(users.width + eventlog.width, 0),new Vector2f(300 - 25, logPanel.height -16), true)
		chatlog.isResizable = false
		chatlog.isMovable = false
		chatlog.setToolTipText("Chatmessages are displayed here")
		logPanel.addChild(chatlog)
		
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
	def logNetworkEvents(NetworkEvent evt) {
		eventlog.text = "" + evt + "\n" + eventlog.text
	}


	@Subscribe
	def logSeverEvents(ServerEvent evt) {
		eventlog.text = "" + evt + "\n" + eventlog.text
	}
	
	@Subscribe
	def logSeverEvents(ClientTrysToLoginEvent evt) {
		users.addListItem(evt.user, evt)
	}
	
	@Subscribe
	def logSeverEvents(ClientLoggedOutEvent evt) {
		users.removeListItem(evt.user)
	}
	
	@Subscribe
	def logSeverEvents(ClientDisconnectedEvent evt) {
		EventManager.post(new ClientLoggedOutEvent())
		users.removeListItem(evt.id)
	}
}
