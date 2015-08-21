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

			//			initDisplayControls();
			//			initUIExtrasControls();
			//			initTestControls();
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

		// Add v-sync checkbox
		//		String labelText = "Enable Vertical Sync?";
		//		vSync = new CheckBox(screen, Vector2f.ZERO) {
		//					@Override
		//					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
		//						main.getContext().getSettings().setVSync(toggled);
		//						main.restart();
		//					}
		//				};
		//		vSync.setIsCheckedNoCallback(main.getContext().getSettings().isVSync());
		//		vSync.setToolTipText(labelText);
		//		content.addChild(vSync);

		// Add v-sync label
		//		content.addChild(getLabel(labelText));
	}

	//	private void initDisplayControls() {
	//		// Add title label for Display
	//		dispTitle = getLabel("DISPLAY");
	//		dispTitle.setTextAlign(BitmapFont.Align.Center);
	//		content.addChild(dispTitle);
	//
	//		// Add title label for mode selection
	//		content.addChild(getLabel("Screen Resolution:"));
	//
	//		// Add drop-down with available screen modes
	//		serverAddress = new SelectBox(screen, Vector2f.ZERO) {
	//					@Override
	//					public void onChange(int selectedIndex, Object value) {
	//						if (!Screen.isAndroid()) {
	//							prevScreenSize.set(main.getViewPort().getCamera().getWidth(),main.getViewPort().getCamera().getHeight());
	//							main.getContext().getSettings().setWidth(((DisplayMode)value).getWidth());
	//							main.getContext().getSettings().setHeight(((DisplayMode)value).getHeight());
	//							if (((DisplayMode)value).getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN) {
	//								main.getContext().getSettings().setFrequency(((DisplayMode)value).getRefreshRate());
	//							}
	//							main.restart();
	//						}
	//					}
	//				};
	//		loadDisplayModes();
	//		if (!Screen.isAndroid()) {
	//			serverAddress.setSelectedByCaption(initResolution, true);
	//		}
	//		serverAddress.setToolTipText("Select Screen Resolution");
	//		serverAddress.getLayoutHints().set("wrap");
	//		content.addChild(serverAddress);
	//
	//		// Add v-sync checkbox
	//		String labelText = "Enable Vertical Sync?";
	//		vSync = new CheckBox(screen, Vector2f.ZERO) {
	//					@Override
	//					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
	//						main.getContext().getSettings().setVSync(toggled);
	//						main.restart();
	//					}
	//				};
	//		vSync.setIsCheckedNoCallback(main.getContext().getSettings().isVSync());
	//		vSync.setToolTipText(labelText);
	//		content.addChild(vSync);
	//
	//		// Add v-sync label
	//		content.addChild(getLabel(labelText));
	//	}

	private void initUIExtrasControls() {
		// Screen extras toggles
		// Add title label for GUI Extras
		extTitle = getLabel("GUI EXTRAS");
		extTitle.setTextAlign(BitmapFont.Align.Center);
		content.addChild(extTitle);

		// UI Global Alpha
		TextElement l = getLabel("UI Alpha:");
		//	l.getLayoutHints().setUseLayoutPadY(false);
		content.addChild(l);

		uiAlpha = new Slider(screen, Vector2f.ZERO, LayoutHelper.dimensions(200, 24), Orientation.HORIZONTAL, false) {
					@Override
					public void onChange(int selectedIndex, Object value) {
						screen.setGlobalAlpha((Float) (Float.valueOf((Integer)value)/100f));
						setToolTipText("Current UI alpha: " + (Float.valueOf((Integer)value)/100f));
					}
				};
		uiAlpha.getLayoutHints().set("wrap");
		content.addChild(uiAlpha);

		// UI Audio Volume
		l = getLabel("Audio Volume:");
		//	l.getLayoutHints().setUseLayoutPadY(false);
		content.addChild(l);

		audioVol = new Slider(screen, Vector2f.ZERO, LayoutHelper.dimensions(200, 24), Orientation.HORIZONTAL, false) {
					@Override
					public void onChange(int selectedIndex, Object value) {
						screen.setUIAudioVolume((Float) (Float.valueOf((Integer)value)/100f));
						setToolTipText("Current volume: " + (Float.valueOf((Integer)value)/100f));
					}
				};
		audioVol.getLayoutHints().set("wrap");
		content.addChild(audioVol);

		// UI Audio
		String labelText = "Enable UI Audio?";
		audio = new CheckBox(screen, Vector2f.ZERO) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						screen.setUseUIAudio(toggled);
					}
				};
		audio.setIsCheckedNoCallback(Main.USE_UI_AUDIO);
		audio.setToolTipText(labelText);
		content.addChild(audio);

		content.addChild(getLabel(labelText));

		// Custom cursors
		labelText = "Enable Cursors?";
		cursors = new CheckBox(screen, Vector2f.ZERO) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						screen.setUseCustomCursors(toggled);
					}
				};
		cursors.setIsCheckedNoCallback(Main.USE_CUSTOM_CURSORS);
		cursors.setToolTipText(labelText);
		content.addChild(cursors);

		content.addChild(getLabel(labelText));

		// Cursors FX
		labelText = "Enable Cursor FX?";
		cursorFX = new CheckBox(screen, Vector2f.ZERO) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						screen.setUseCursorEffects(toggled);
					}
				};
		cursorFX.setIsCheckedNoCallback(Main.USE_CURSOR_EFFECTS);
		cursorFX.setToolTipText(labelText);
		content.addChild(cursorFX);

		content.addChild(getLabel(labelText));

		// ToolTips
		labelText = "Enable ToolTips?";
		toolTips = new CheckBox(screen, Vector2f.ZERO) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						screen.setUseToolTips(toggled);
					}
				};
		toolTips.setIsCheckedNoCallback(Main.USE_TOOLTIPS);
		toolTips.setToolTipText(labelText);
		content.addChild(toolTips);

		l = getLabel(labelText);
		//	l.getLayoutHints().setElementPadY(5);
		content.addChild(l);
	}

	//	private void initTestControls() {
	//		// Screen extras toggles
	//		// Add title label for GUI Extras
	//		testTitle = getLabel("TEST SETTINGS");
	//		testTitle.setTextAlign(BitmapFont.Align.Center);
	//		//	testTitle.getLayoutHints().setElementPadY(5);
	//		content.addChild(testTitle);
	//
	//		// Add test slect box
	//		testSelect = new SelectBox(screen, Vector2f.ZERO) {
	//					@Override
	//					public void onChange(int selectedIndex, Object value) {
	//
	//					}
	//				};
	//		testSelect.setToolTipText("Available GUI Tests");
	//		content.addChild(testSelect);
	//
	//		// Add load button
	//		load = new ButtonAdapter(screen, Vector2f.ZERO, LayoutHelper.dimensions((Float) (testSelect.getWidth()/4*3), testSelect.getHeight())) {
	//					@Override
	//					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
	//						if (!testSelect.getListItems().isEmpty()) {
	//							main.getStateManager().attach((AppStateCommon)testSelect.getSelectedListItem().getValue());
	//						}
	//					}
	//				};
	//		load.setText("Load");
	//		load.setToolTipText("Load Selected GUI Test");
	//		load.getLayoutHints().define("wrap","pad 25 0 0 0");
	//		content.addChild(load);
	//
	//		// Add test slect box
	//		cTestSelect = new SelectBox(screen, Vector2f.ZERO) {
	//					@Override
	//					public void onChange(int selectedIndex, Object value) {
	//
	//					}
	//				};
	//		cTestSelect.setToolTipText("Currently Loaded GUI Test");
	//		content.addChild(cTestSelect);
	//
	//		// Add un button
	//		unload = new ButtonAdapter(screen, Vector2f.ZERO, LayoutHelper.dimensions((Float) (cTestSelect.getWidth()/4*3), cTestSelect.getHeight())) {
	//					@Override
	//					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
	//						if (!cTestSelect.getListItems().isEmpty()) {
	//							main.getStateManager().detach((AppStateCommon)cTestSelect.getSelectedListItem().getValue());
	//						}
	//					}
	//				};
	//		unload.setText("Unload");
	//		unload.setToolTipText("Unload Selected GUI Test");
	//		unload.getLayoutHints().define("wrap","pad 25 0 0 0");
	//		content.addChild(unload);
	//	}

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

	//	private void loadDisplayModes() {
	//		if (!Screen.isAndroid()) {
	//			if (modes == null) {
	//				GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	//				modes = device.getDisplayModes();
	//
	//				Arrays.sort(modes, new DisplayModeSorter());
	//				int listIndex = 0;
	//				for (DisplayMode mode : modes) {
	//					boolean add = true;
	//					if (listIndex > 0) {
	//						int index = listIndex - 1;
	//						if (serverAddress.getListItemByIndex(index).getCaption().equals(mode.getWidth() + "x" + mode.getHeight())) {
	//							add = false;
	//						}
	//					}
	//					if (add) {
	//						serverAddress.addListItem(mode.getWidth() + "x" + mode.getHeight(), mode);
	//						listIndex++;
	//					}
	//				}
	//			}
	//		}
	//	}

	private class DisplayModeSorter implements Comparator<DisplayMode> {
		@Override
		public int compare(DisplayMode a, DisplayMode b) {
			if (a.getWidth() != b.getWidth()) {
				return (a.getWidth() > b.getWidth()) ? 1 : -1;
			}
			if (a.getHeight() != b.getHeight()) {
				return (a.getHeight() > b.getHeight()) ? 1 : -1;
			}
			if (a.getBitDepth() != b.getBitDepth()) {
				return (a.getBitDepth() > b.getBitDepth()) ? 1 : -1;
			}
			if (a.getRefreshRate() != b.getRefreshRate()) {
				return (a.getRefreshRate() > b.getRefreshRate()) ? 1 : -1;
			}
			return 0;
		}
	}

	public Vector2f getPreviousScreenSize() { return this.prevScreenSize; }
}
