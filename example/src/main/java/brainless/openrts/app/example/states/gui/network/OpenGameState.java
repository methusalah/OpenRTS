package brainless.openrts.app.example.states.gui.network;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import model.ModelManager;
import model.battlefield.Battlefield;
import model.battlefield.army.Faction;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.lists.SelectList;
import tonegod.gui.controls.scrolling.ScrollArea;
import tonegod.gui.controls.text.LabelElement;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.core.Element;
import tonegod.gui.core.Element.Borders;
import tonegod.gui.core.Element.Docking;
import tonegod.gui.core.layouts.FlowLayout;
import tonegod.gui.core.layouts.LayoutHelper;
import tonegod.gui.core.utils.UIDUtil;
import brainless.openrts.app.example.states.AppStateCommon;
import brainless.openrts.event.ClientLoggedOutEvent;
import brainless.openrts.event.EventManager;
import brainless.openrts.util.FileUtil;

import com.google.inject.Inject;
import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;


public class OpenGameState extends AppStateCommon {

	private static final Logger logger = Logger.getLogger(OpenGameState.class.getName());

	private float contentPadding = 14;

	private Element content;
	private Panel panel;
	private TextField chatBox;
	private LabelElement dispTitle, extTitle, testTitle;
	protected ButtonAdapter close,openGame;

	private ScrollArea mapInfo;
	private SelectList mapSelect;

	@Inject
	ModelManager modelManager;

	@Inject
	public OpenGameState() {
		displayName = "OpenGame";
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

			FlowLayout layout = new FlowLayout(screen,"clip","margins 5 5 5 5","pad 5 5 5 5");
			// Container for harness panel content
			content = new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, new Vector2f(screen.getWidth(),screen.getHeight()), Vector4f.ZERO, null);
			content.setAsContainerOnly();
			content.setLayout(layout);
			// Add title label for Display
			dispTitle = getLabel("Open Game");
			dispTitle.setTextAlign(BitmapFont.Align.Center);
			content.addChild(dispTitle);

			close = new ButtonAdapter(screen, Vector2f.ZERO) {
						@Override
						public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
							ClientLoggedOutEvent evt1 = new ClientLoggedOutEvent(main.getGame().getMySelf().getId(), main.getGame().getMySelf().getName());
							EventManager.post(evt1);
							System.exit(0);
						}
					};
			close.setDocking(Docking.SW);
			close.setText("CloseGame");
			close.setToolTipText("Close Application");


			mapSelect = new SelectList( screen, Vector2f.ZERO) {
						public void onChange() {

							mapInfo.removeAllChildren();
							ListItem item = getSelectedListItems().get(0);
							File file = (File) item.getValue();
							Battlefield bfd = modelManager.loadOnlyStaticValues(file);

							main.getGame().setFile(file);

							String mapDescription = "You selected Map : " + item.getCaption() + "\n";
							mapDescription += "Size: " + bfd.getMap().getWidth() + "x" + bfd.getMap().getHeight() + "\n";
							List<Faction> factions = bfd.getEngagement().getFactions();
							mapDescription += "Players:" + factions.size() + "\n";
							for (Faction faction : factions) {
								mapDescription += "Player " + 1 + " :" + faction.getName()  + "\n";
							}
							mapInfo.setText(mapDescription);
							
							openGame.setIsEnabled (getSelectedIndexes().isEmpty());

							logger.info("element is selected: " + getSelectedIndexes());
						}
					};
			mapSelect.setDimensions(200, 200);
			mapSelect.setDocking(Docking.SW);
			mapSelect.setToolTipText("Please select a Map");

			List<File> files = FileUtil.getFilesInDirectory(ModelManager.DEFAULT_MAP_PATH, "btf");

			files.forEach((File file) -> mapSelect.addListItem(file.getName(), file));

			content.addChild(mapSelect);

			mapInfo = new ScrollArea(screen,"mapInfo", Vector2f.ZERO,true);
			mapInfo.setToolTipText("infos about the selected Map");
			mapInfo.setDimensions(mapSelect.getWidth(),mapSelect.getHeight());
			content.addChild(mapInfo);
			mapInfo.getLayoutHints().set("wrap");

			openGame = new ButtonAdapter(screen, Vector2f.ZERO) {
						@Override
						public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
							main.openGame();
						}
					};
			openGame.setIsEnabled(false);
			openGame.setDocking(Docking.SW);
			openGame.setText("Open Game");
			openGame.setToolTipText("Opens a game with the current selected Map");
			content.addChild(openGame);

			// Create the main display panel
			panel = new Panel(screen,Vector2f.ZERO,	LayoutHelper.dimensions((Float)(content.getWidth() + (contentPadding*2)),screen.getHeight()));
			panel.addChild(content);
			panel.addChild(close);
			panel.setIsMovable(false);
			panel.setIsResizable(false);
			panel.layoutChildren();
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
		panel.removeAllChildren();
	}

	private LabelElement getLabel(String text) {
		LabelElement te = new LabelElement(screen, LayoutHelper.position(), LayoutHelper.dimensions(150,20));
		te.setSizeToText(true);
		te.setText(text);
		te.getLayoutHints().set("wrap");
		return te;
	}


}
