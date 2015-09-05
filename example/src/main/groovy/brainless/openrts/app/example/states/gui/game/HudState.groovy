package brainless.openrts.app.example.states.gui.game;

import groovy.transform.CompileStatic
import openrts.guice.annotation.RootNodeRef

import org.lwjgl.opengl.Display

import tonegod.gui.controls.buttons.ButtonAdapter
import tonegod.gui.controls.text.Label
import tonegod.gui.controls.windows.Panel
import tonegod.gui.controls.windows.Window
import tonegod.gui.core.Element
import tonegod.gui.core.Element.Docking
import tonegod.gui.core.layouts.FlowLayout
import tonegod.gui.core.layouts.LayoutHelper
import tonegod.gui.core.utils.UIDUtil
import brainless.openrts.app.example.states.AppStateCommon;
import brainless.openrts.event.EventManager
import brainless.openrts.event.network.SelectEntityEvent

import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import com.jme3.input.event.JoyAxisEvent
import com.jme3.input.event.JoyButtonEvent
import com.jme3.input.event.KeyInputEvent
import com.jme3.input.event.MouseButtonEvent
import com.jme3.input.event.MouseMotionEvent
import com.jme3.input.event.TouchEvent
import com.jme3.math.Vector2f
import com.jme3.math.Vector4f
import com.jme3.scene.Node
import com.jme3.texture.Texture

/**
 * 
 * @author t0neg0d
 */
@CompileStatic
class HudState extends AppStateCommon {

	private Panel cPanel;
	private float iconSize = 20;
	private ButtonAdapter cursor;
//	private InteractiveNode obj1Node, obj2Node, obj3Node;
	private Texture objIcons;
	private String iconDefault;
	List<ButtonAdapter> slots = []
	
	Panel informationPanel;
	
	@Inject
	@RootNodeRef
	private Node rootNode;
	
	@Inject
	public HudState() {
		super();
		displayName = "Spatial Support";
		show = true;
		
		iconDefault = "x=64|y=64|w=64|h=64";
		EventManager.register(this);
		
	}
	
	@Override
	public void reshape() {
		
	}
	
	@Override
	protected void initState() {
//		main.getInputManager().addRawInputListener(this);
		if (!init) {
//			main.getInputManager().deleteMapping("FLYCAM_RotateDrag");
//			main.getInputManager().addMapping("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
//			main.getInputManager().addListener(main.getFlyByCamera(), "FLYCAM_RotateDrag");
			
			objIcons = main.getAssetManager().loadTexture("Textures/Spatials/ObjIcons.png");
			
			initCtrlPanel();
			
			cursor = new ButtonAdapter(screen, UIDUtil.getUID(), Vector2f.ZERO, new Vector2f(iconSize, iconSize), Vector4f.ZERO, null) {
				@Override
				public void update(float tpf) {
					setPosition(screen.getMouseXY().x+25,screen.getMouseXY().y-50);
				}
			};
			cursor.setIsEnabled(false);
			cursor.setInterval(1);
			cursor.setTextureAtlasImage(objIcons, iconDefault);
			cursor.setIsModal(true);
			cursor.setIsGlobalModal(true);
			cursor.setEffectZOrder(false);
			cursor.setIgnoreMouse(true);
			
			init = true;
		}
		screen.setUse3DSceneSupport(true);

		screen.addElement(cursor);
		cursor.move(0,0,20);
		main.addSceneLights();
	}

	
	
	private void initCtrlPanel() {
		
		Window HUD =  new Window(screen, "HudWindow", new Vector2f(15f, 15f));
		
		screen.addElement(HUD);
		HUD.setDimensions(new Vector2f(Display.getWidth(), 100));
		HUD.setLocalTranslation(Display.getWidth()- HUD.getWidth(), 0, 0);
		
	   
		HUD.setLayout(
				new FlowLayout(screen, "margins 8 8 8 8", "padding 0 0 0 0")
			);
		
		Panel Minimap = new Panel(screen, "MiniMap", new Vector2f(0f,0f));
		Minimap.setDimensions(200,HUD.getHeight());
		Minimap.setToolTipText("This is the Minimap");
		Minimap.setIsResizable(false);
		Minimap.setIsMovable(false);
		HUD.addChild(Minimap);
		
		
		informationPanel = new Panel(screen, "InformationPanel", new Vector2f(Minimap.getWidth(),0f));
		informationPanel.setDimensions(400,HUD.getHeight());
		informationPanel.setToolTipText("This is the Informationpanel. Please select a unit.");
		informationPanel.setIsResizable(false);
		informationPanel.setIsMovable(false);
		HUD.addChild(informationPanel);
		
		
		Panel ActionPanel = new Panel(screen, "ActionPanel", new Vector2f(Minimap.getWidth() + informationPanel.getWidth(),0f));
		ActionPanel.setDimensions(200,HUD.getHeight());
		ActionPanel.setToolTipText("This is the ActionPanel. Please select a unit.");
		ActionPanel.setIsResizable(false);
		ActionPanel.setIsMovable(false);
		HUD.addChild(ActionPanel);
		
		cPanel = new Panel(screen,displayName, Vector2f.ZERO, Vector2f.ZERO);
		cPanel.setEffectZOrder(false);
		
		LayoutHelper.reset();
		Element lastEl = null;
		
//		SelectBox invType = new SelectBox(screen, LayoutHelper.position()) {
//			@Override
//			public void onChange(int selectedIndex, Object value) {
//				inventoryType = (InventoryType)value;
//				changeInventoryType();
//			}
//		};
//		invType.setDocking(Docking.SW);
//		invType.setEffectZOrder(false);
//		invType.addListItem("Drag & Drop", InventoryType.DragDrop);
//		invType.addListItem("Point & Click", InventoryType.PointClick);
//		cPanel.getContentArea().addChild(invType);
		
//		lastEl = invType;
		
		Element content = new Element(screen, UIDUtil.getUID(), LayoutHelper.position(), Vector2f.ZERO, Vector4f.ZERO, null);
		content.setAsContainerOnly();
		content.setDocking(Docking.SW);
		cPanel.addChild(content);
		
		int index = 0;
		for (int y = 0; y < 4; y++) {
			LayoutHelper.resetX();
			if (lastEl) {
				LayoutHelper.advanceY(lastEl, true);
			}
			for (int x = 0; x < 4; x++) {
				ButtonAdapter e = createInventorySlot(index, LayoutHelper.position());
				e.setScaleEW(false);
				e.setScaleNS(false);
				e.setDocking(Element.Docking.SW);
				e.setEffectZOrder(false);
				content.addChild(e);
				slots.add(e);
				lastEl = e;
				LayoutHelper.advanceX(lastEl, true);
				index++;
			}
		}
		
		ActionPanel.addChild(cPanel)
//		cPanel.pack();
	}
	
	@Subscribe
	def handleSelectUnit(SelectEntityEvent evt) {
		informationPanel.removeAllChildren();
		Label name = new Label(screen, new Vector2f(20, 20))
		name.setText("Unit selected: "+ evt.getUnitId() );
		name.setDimensions(300, 100);
		informationPanel.addChild(name);
	}

	private ButtonAdapter createInventorySlot(int index, Vector2f position) {
		ButtonAdapter slot = new ButtonAdapter(
			screen,
			"InvSlot" + index,
			position,
			LayoutHelper.dimensions(iconSize,iconSize),
			screen.getStyle("CheckBox").getVector4f("resizeBorders"),
			screen.getStyle("CheckBox").getString("defaultImg")
		) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//				if (inventoryType == InventoryType.PointClick) {
//					InteractiveNode prevNode = null;
//					InteractiveNode nextNode = null;
//					if (getUserData("worldObject") != null) {
//						prevNode = getUserData("worldObject");
//						setToolTipText("");
//						setUserData("worldObject", null);
//						setButtonIcon(iconSize, iconSize, iconDefault);
//					}
//					if (cursor.getUserData("worldObject") != null) {
//						nextNode = cursor.getUserData("worldObject");
//						cursor.setTextureAtlasImage(objIcons, iconDefault);
//						cursor.setUserData("worldObject", null);
//					}
//					if (nextNode != null) {
//						String ttt = nextNode.getToolTipText();
//						setToolTipText(ttt.substring(0,ttt.indexOf("\n")) + "\n\nDrop me back into\nthe world.");
//						setUserData("worldObject", nextNode);
//						setButtonIcon(iconSize, iconSize, nextNode.getIcon());
//					}
//					if (prevNode != null) {
//						cursor.setUserData("worldObject", prevNode);
//						cursor.setTextureAtlasImage(objIcons, prevNode.getIcon());
//					}
//				}
				System.out.println("buttonPressed");
			}
		};
		slot.clearAltImages();
		slot.setEffectZOrder(false);
		slot.setButtonIcon(iconSize, iconSize, iconDefault);
		slot.getButtonIcon().setTextureAtlasImage(objIcons, iconDefault);
		slot.setIsDragDropDropElement(true);
		return slot;
	}
	
	@Override
	public void updateState(float tpf) {
		
	}

	@Override
	public void cleanupState() {
		screen.removeElement(cursor);
		screen.setUse3DSceneSupport(false);
//		main.getInputManager().removeRawInputListener(this);
	}
	
	public void beginInput() {  }
	public void endInput() {  }
	public void onJoyAxisEvent(JoyAxisEvent evt) {  }
	public void onJoyButtonEvent(JoyButtonEvent evt) {  }
	public void onMouseMotionEvent(MouseMotionEvent evt) {  }
	public void onMouseButtonEvent(MouseButtonEvent evt) {
		if (evt.getButtonIndex() == 0 && evt.isPressed()) {
//			if (inventoryType == InventoryType.PointClick) {
//				if (cursor.getUserData("worldObject") != null) {
//					InteractiveNode node = cursor.getUserData("worldObject");
//					rootNode.attachChild(node);
//					node.setIsInScene(true);
//					cursor.setUserData("worldObject", null);
//					cursor.setTextureAtlasImage(objIcons, iconDefault);
//				}
//			}
			System.out.println("button pressed2");
		}
	}
	public void onKeyEvent(KeyInputEvent evt) {  }
	public void onTouchEvent(TouchEvent evt) {  }
}
