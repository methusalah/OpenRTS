package brainless.openrts.app.example.states.gui.network;

import groovy.transform.CompileStatic

import java.util.logging.Logger

import model.ModelManager
import tonegod.gui.controls.buttons.ButtonAdapter
import tonegod.gui.controls.scrolling.ScrollArea
import tonegod.gui.controls.text.LabelElement
import tonegod.gui.controls.text.TextField
import tonegod.gui.controls.windows.Panel
import tonegod.gui.core.Element
import tonegod.gui.core.Screen
import tonegod.gui.core.Element.Borders
import tonegod.gui.core.Element.Docking
import tonegod.gui.core.layouts.FlowLayout
import tonegod.gui.core.layouts.LayoutHelper
import tonegod.gui.core.utils.UIDUtil
import brainless.openrts.app.example.states.AppStateCommon
import brainless.openrts.event.ClientLoggedOutEvent
import brainless.openrts.event.EventManager

import com.google.inject.Inject
import com.jme3.font.BitmapFont
import com.jme3.input.event.MouseButtonEvent
import com.jme3.math.Vector2f
import com.jme3.math.Vector4f

@CompileStatic
public class GameLobbyState extends AppStateCommon {

	private static final Logger logger = Logger.getLogger(GameLobbyState.class.getName());

	private float contentPadding = 14;

	private Element content;
	private Panel panel;
	private TextField chatBox
	private LabelElement dispTitle, extTitle, testTitle;
	protected ButtonAdapter close,openGame;

	ScrollArea mapInfo

	@Inject
	ModelManager modelManager

	@Inject
	public ServerConfigState() {
		displayName = "Game Lobby";
		show = false;
	}

	@Override
	public void reshape() {
		if (panel != null) {
			panel.resize(panel.getWidth(),screen.getHeight(),Borders.SE);
		}
	}

	@Override
	protected void initState() {
		if (!initialized) {

			FlowLayout layout = new FlowLayout(screen,"clip","margins 0 0 0 0","pad 5 5 5 5");
			// Container for harness panel content
			content = new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, new Vector2f(screen.width,screen.height), Vector4f.ZERO, null);
			content.setAsContainerOnly();
			content.setLayout(layout);
			// Add title label for Display
			dispTitle = getLabel("Game Lobby");
			dispTitle.setTextAlign(BitmapFont.Align.Center);
			content.addChild(dispTitle);

			close = new ButtonAdapter(screen, Vector2f.ZERO) {
						@Override
						public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
							ClientLoggedOutEvent evt1 = new ClientLoggedOutEvent(main.game.mySelf.id, main.game.mySelf.name);
							EventManager.post(evt1);
							System.exit(0);
						}
					};
			close.setDocking(Docking.SW);
			close.setText("CloseGame");
			close.setToolTipText("Close Application");

			mapInfo = new ScrollArea(screen,"mapInfo", Vector2f.ZERO,true);
			mapInfo.setToolTipText("infos about the selected Map");
			mapInfo.setDimensions(200,200)
			content.addChild(mapInfo)
			mapInfo.layoutHints.set("wrap")

			openGame = new ButtonAdapter(screen, Vector2f.ZERO) {
						@Override
						public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
							main.openGame()
						}
					};
			openGame.isEnabled = false
			openGame.setDocking(Docking.SW);
			openGame.setText("Open Game");
			openGame.setToolTipText("Opens a game");
			content.addChild(openGame)

			// Create the main display panel
			panel = new Panel(screen,Vector2f.ZERO,	LayoutHelper.dimensions((Float)(content.width + (contentPadding*2)),screen.getHeight()));
			panel.addChild(content);
			panel.addChild(close);
			panel.setIsMovable(false);
			panel.setIsResizable(false);
			screen.addElement(panel, true);

			// Set control defaults
			close.centerToParent();
			close.setY(contentPadding);
			dispTitle.centerToParentH();


			initialized = true;
		}

		panel.show();
	}

	@Override
	public void updateState(float tpf) {

	}

	@Override
	public void cleanupState() {
		panel.hide();
		panel.detachAllChildren();
		panel.removeAllChildren()
	}

	private LabelElement getLabel(String text) {
		LabelElement te = new LabelElement(screen, LayoutHelper.position(), LayoutHelper.dimensions(150,20));
		te.setSizeToText(true);
		te.setText(text);
		te.getLayoutHints().set("wrap");
		return te;
	}


}
