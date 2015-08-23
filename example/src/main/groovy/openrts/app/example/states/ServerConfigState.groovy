/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package openrts.app.example.states;

import java.awt.DisplayMode

import model.ModelManager
import network.client.ClientManager
import openrts.app.example.MultiplayerGame
import tonegod.gui.controls.buttons.ButtonAdapter
import tonegod.gui.controls.buttons.CheckBox
import tonegod.gui.controls.lists.Slider
import tonegod.gui.controls.text.LabelElement
import tonegod.gui.controls.text.TextElement
import tonegod.gui.controls.text.TextField
import tonegod.gui.controls.windows.Panel
import tonegod.gui.core.Element
import tonegod.gui.core.Screen
import tonegod.gui.core.Element.Borders
import tonegod.gui.core.Element.Docking
import tonegod.gui.core.Element.Orientation
import tonegod.gui.core.layouts.FlowLayout
import tonegod.gui.core.layouts.LayoutHelper
import tonegod.gui.core.utils.UIDUtil
import tonegod.gui.tests.Main

import com.google.inject.Inject
import com.google.inject.Injector
import com.jme3.font.BitmapFont
import com.jme3.input.event.MouseButtonEvent
import com.jme3.math.Vector2f
import com.jme3.math.Vector4f

import event.EventManager
import event.network.ClientTrysToConnectEvent
import event.network.CreateGameEvent
import groovy.transform.CompileStatic

/**
 *
 * @author t0neg0d
 */
@CompileStatic
public class ServerConfigState extends AppStateCommon {
	private float contentPadding = 14;

	private DisplayMode[] modes;
	private String initResolution;
	protected Vector2f prevScreenSize = new Vector2f();

	private Element content;
	private Panel panel;
	private TextField serverAddress
	private CheckBox vSync, audio, cursors, cursorFX, toolTips;
	private Slider uiAlpha, audioVol;
	private LabelElement dispTitle, extTitle, testTitle;
	protected ButtonAdapter close,connect, startMap;
	protected ClientManager clientManager

	protected static String mapfilename = "assets/maps/test.btf";
	
	

	@Inject
	Injector injector

	@Inject
	public ServerConfigState(MultiplayerGame main, ClientManager clientManager) {
		super(main);
		this.clientManager = clientManager
		displayName = "Harness";
		show = false;
		prevScreenSize.set(main.getViewPort().getCamera().getWidth(),main.getViewPort().getCamera().getHeight());
		initResolution = prevScreenSize.x + "x" + prevScreenSize.y;
	}

	@Override
	public void reshape() {
		if (panel != null) {
			panel.resize(panel.getWidth(),screen.getHeight(),Borders.SE);
		}
	}

	@Override
	protected void initState() {
		if (!init) {
			FlowLayout layout = new FlowLayout(screen,"clip","margins 0 0 0 0","pad 5 5 5 5");
			// Container for harness panel content
			content = new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, new Vector2f(200,screen.getHeight()), Vector4f.ZERO, null);
			content.setAsContainerOnly();
			content.setLayout(layout);

			// Reset layout helper
			//	LayoutHelper.reset();
			initServerControls()

			close = new ButtonAdapter(screen, Vector2f.ZERO) {
						@Override
						public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
							System.exit(0);
						}
					};
			close.setDocking(Docking.SW);
			close.setText("Exit");
			close.setToolTipText("Close Application");

			// Position content container and size it to it's contents
			content.getLayout().layoutChildren();
			content.sizeToContent();
			content.getLayout().layoutChildren();
			content.setPosition(LayoutHelper.absPosition(contentPadding,contentPadding));

			// Create the main display panel
			panel = new Panel(
					screen,
					Vector2f.ZERO,
					LayoutHelper.dimensions((Float)(content.getWidth()+(contentPadding*2)),screen.getHeight())
					);
			panel.addChild(content);
			panel.addChild(close);
			panel.setIsMovable(false);
			panel.setIsResizable(false);
			screen.addElement(panel, true);

			// Set control defaults
			close.centerToParent();
			close.setY(contentPadding);
			dispTitle.centerToParentH();
			//			extTitle.centerToParentH();
			//			testTitle.centerToParentH();
			//			uiAlpha.setSelectedIndexWithCallback(100);
			//			audioVol.setSelectedIndexWithCallback(100);

			init = true;
		}

		panel.show();
	}

	private void initServerControls() {
		// Add title label for Display
		dispTitle = getLabel("Server");
		dispTitle.setTextAlign(BitmapFont.Align.Center);
		content.addChild(dispTitle);

		// Add title label for mode selection
		content.addChild(getLabel("Address:"));

		// Add drop-down with available screen modes
		serverAddress = new TextField(screen, Vector2f.ZERO);
		//		loadDisplayModes();
		serverAddress.text = "127.0.0.1"

		serverAddress.toolTipText = "Which Server?";
		serverAddress.getLayoutHints().set("wrap");
		content.addChild(serverAddress);


		connect = new ButtonAdapter(screen, Vector2f.ZERO) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {	
						startMap.isEnabled = true
						connect.isEnabled = false
						clientManager.startClient(serverAddress.text)
						ClientTrysToConnectEvent evt1 = new ClientTrysToConnectEvent(main.user);
						EventManager.post(evt1);
						
					}
				};
		connect.setDocking(Docking.SW);
		connect.setText("connect");
		connect.setToolTipText("connect to Server");
		content.addChild(connect)
		
		
		startMap = new ButtonAdapter(screen, Vector2f.ZERO) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				ModelManager.loadBattlefield(mapfilename);
				CreateGameEvent evt1 = new CreateGameEvent(mapfilename);
				EventManager.post(evt1);
				main.loadMap()
			}
		};
		startMap.isEnabled = false
		startMap.setDocking(Docking.SW);
		startMap.setText("startMap");
		startMap.setToolTipText("start the testmap");
		content.addChild(startMap)

	}



	public Panel getHarnessPanel() { return this.panel; }

	@Override
	public void updateState(float tpf) {

	}

	@Override
	public void cleanupState() {
		panel.hide();
	}

	private LabelElement getLabel(String text) {
		LabelElement te = new LabelElement(screen, LayoutHelper.position(), LayoutHelper.dimensions(150,20));
		te.setSizeToText(true);
		te.setText(text);
		te.getLayoutHints().set("wrap");
		return te;
	}


	public Vector2f getPreviousScreenSize() { return this.prevScreenSize; }
}
