/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brainless.openrts.app.example.states;

import java.util.logging.Logger

import model.ModelManager
import model.battlefield.Battlefield
import tonegod.gui.controls.buttons.ButtonAdapter
import tonegod.gui.controls.buttons.CheckBox
import tonegod.gui.controls.lists.SelectList
import tonegod.gui.controls.lists.Slider
import tonegod.gui.controls.lists.SelectList.ListItem
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
import brainless.openrts.event.ClientLoggedOutEvent;
import brainless.openrts.event.EventManager;
import brainless.openrts.util.FileUtil;

import com.google.inject.Inject
import com.google.inject.Injector
import com.jme3.font.BitmapFont
import com.jme3.input.event.MouseButtonEvent
import com.jme3.math.Vector2f
import com.jme3.math.Vector4f

import groovy.transform.CompileStatic

/**
 *
 * @author t0neg0d
 */
@CompileStatic
public class GuiServerConfigState extends AppStateCommon {
	
	private static final Logger logger = Logger.getLogger(GuiServerConfigState.class.getName());
	
	private float contentPadding = 14;

	private Element content;
	private Panel panel;
	private TextField serverAddress
	private CheckBox vSync, audio, cursors, cursorFX, toolTips;
	private Slider uiAlpha, audioVol;
	private LabelElement dispTitle, extTitle, testTitle;
	protected ButtonAdapter close,connect, startMap;
	
	ScrollArea mapInfo
	
	
	

	protected static String mapfilename = "assets/maps/test.btf";
	

	@Inject
	Injector injector

	@Inject
	public GuiServerConfigState() {
		displayName = "ServerConfig";
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
		if (!init) {
			
			FlowLayout layout = new FlowLayout(screen,"clip","margins 0 0 0 0","pad 5 5 5 5");
			// Container for harness panel content
			content = new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, new Vector2f(screen.width,screen.height), Vector4f.ZERO, null);
			content.setAsContainerOnly();
			content.setLayout(layout);

			// Reset layout helper
//			LayoutHelper.reset();
			initServerControls()

			close = new ButtonAdapter(screen, Vector2f.ZERO) {
						@Override
						public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
							ClientLoggedOutEvent evt1 = new ClientLoggedOutEvent(main.game.mySelf.id, main.game.mySelf.name);
							EventManager.post(evt1);
							System.exit(0);
						}
					};
			close.setDocking(Docking.SW);
			close.setText("Exit");
			close.setToolTipText("Close Application");

			content.getLayout().layoutChildren();
			content.setPosition(LayoutHelper.absPosition(contentPadding,contentPadding));

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
						connect.isEnabled = false
						main.connectToServer(serverAddress.text)
						
					}
				};
		connect.setDocking(Docking.SW);
		connect.setText("connect");
		connect.setToolTipText("connect to Server");
		connect.getLayoutHints().set("wrap");
		content.addChild(connect)
		
		SelectList mapSelect = new SelectList( screen, Vector2f.ZERO) {
			public void onChange() {
				
				mapInfo.removeAllChildren();
				ListItem item = selectedListItems.first()
				File file = (File) item.value
				Battlefield bfd = ModelManager.loadOnlyStaticValues(file)
				
				main.game.file = file
				
				String mapDescription = "You selected Map : " + item.caption + "\n"
				mapDescription += "Size: " + bfd.map.getWidth() + "x" + bfd.map.getHeight()
				mapInfo.setText(mapDescription);
				
				logger.info("element is selected: " + selectedIndexes)
				startMap.isEnabled = selectedIndexes
			}
		}
		mapSelect.setDimensions(200, 200)
		mapSelect.docking = Docking.SW
		mapSelect.toolTipText = "Please select a Map"
		
		def files = FileUtil.getFilesInDirectory(ModelManager.DEFAULT_MAP_PATH, "btf")
		
		files.each { File file ->
			mapSelect.addListItem(file.name, file)			
		}
		
		content.addChild(mapSelect)
		
		mapInfo = new ScrollArea(screen,"mapInfo", Vector2f.ZERO,true);
		mapInfo.setToolTipText("infos about the selected Map");
		mapInfo.setDimensions(mapSelect.width,mapSelect.height)
		content.addChild(mapInfo)
		mapInfo.layoutHints.set("wrap")
		
		startMap = new ButtonAdapter(screen, Vector2f.ZERO) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {				
				main.loadMap()
			}
		};
		startMap.isEnabled = false
		startMap.setDocking(Docking.SW);
		startMap.setText("startMap");
		startMap.setToolTipText("start the selected Map");
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


}
