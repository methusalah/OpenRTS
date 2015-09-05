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
public class GuiBattleNetLobbyAppState extends AppStateCommon {
	
	private static final Logger logger = Logger.getLogger(GuiBattleNetLobbyAppState.class.getName());
	
	private float contentPadding = 14;

	private Element content;
	private Panel panel;
	private TextField chatBox
	private LabelElement dispTitle, extTitle, testTitle;
	protected ButtonAdapter close;
	
	ScrollArea mapInfo
	
	
	

	protected static String mapfilename = "assets/maps/test.btf";
	

	@Inject
	Injector injector

	@Inject
	public ServerConfigState() {
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

			close = new ButtonAdapter(screen, Vector2f.ZERO) {
						@Override
						public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
							//@TODO move to LobbyAppState
						}
					};
			close.setDocking(Docking.SW);
			close.setText("CloseGame");
			close.setToolTipText("Close Application");
			
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
