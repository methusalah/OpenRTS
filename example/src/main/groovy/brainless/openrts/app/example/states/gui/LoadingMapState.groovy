/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brainless.openrts.app.example.states.gui;

import java.util.logging.Logger

import model.ModelManager
import model.battlefield.Battlefield
import tonegod.gui.controls.extras.Indicator
import tonegod.gui.controls.text.Label
import tonegod.gui.controls.windows.Panel
import tonegod.gui.core.Element
import tonegod.gui.core.Screen
import tonegod.gui.core.Element.Borders
import tonegod.gui.core.Element.Orientation
import tonegod.gui.core.layouts.FlowLayout
import tonegod.gui.core.layouts.LayoutHelper
import tonegod.gui.core.utils.UIDUtil
import util.MapArtisanManager
import brainless.openrts.app.example.MultiplayerGame
import brainless.openrts.app.example.states.AppStateCommon

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Injector
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector2f
import com.jme3.math.Vector4f


//@CompileStatic
public class LoadingMapState extends AppStateCommon {

	private static final Logger logger = Logger.getLogger(LoadingMapState.class.getName());

	private float contentPadding = 14;

	Element content;
	Panel panel;

	Indicator ind
	Label loadingtext

	boolean load = false;
	private float frameCount = 0;

	Battlefield bField = null;

	@Inject
	Injector injector

	@Inject
	MultiplayerGame main

	@Inject
	ModelManager modelManager

	@Inject
	MapArtisanManager mapArtisanManager

	@Inject
	public LoadingMapState() {
		displayName = "LoadingMap";
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


			final ColorRGBA color = new ColorRGBA();

			ind = new Indicator(screen,new Vector2f(50,300),new Vector2f(300,30),Orientation.HORIZONTAL) {
						@Override
						public void onChange(float currentValue, float currentPercentage) {  }
					};
			ind.setBaseImage(screen.getStyle("Window").getString("defaultImg"));
			//ind.setIndicatorImage(screen.getStyle("Window").getString("defaultImg"));
			ind.setIndicatorColor(ColorRGBA.randomColor());
			ind.setAlphaMap(screen.getStyle("Indicator").getString("alphaImg"));
			ind.setIndicatorPadding(new Vector4f(7,7,7,7));
			ind.setMaxValue(100);
			ind.setDisplayPercentage();
			ind.centerToParentH()

			content.addChild(ind);

			loadingtext = new Label(screen, "loadingtext", new Vector2f(200,200), new Vector2f(300,30))
			content.addChild(loadingtext)
			loadingtext.centerToParentH()
			content.setPosition(LayoutHelper.absPosition(contentPadding,contentPadding));

			// Create the main display panel
			panel = new Panel(screen,Vector2f.ZERO,	LayoutHelper.dimensions((Float)(content.width + (contentPadding*2)),screen.getHeight()));
			panel.addChild(content);
			//			panel.addChild(close);
			panel.setIsMovable(false);
			panel.setIsResizable(false);
			screen.addElement(panel, true);

			load = true
			init = true;
		}

		panel.show();
	}

	@Override
	public void updateState(float tpf) {
		if (load) { //loading is done over many frames
			if (frameCount == 1) {
				File file = main.game.file
				modelManager.updateConfigs();
				modelManager.setBattlefieldUnavailable();

				try {
					ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
					bField = mapper.readValue(file, Battlefield.class);
					bField.setFileName(file.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}

				setProgress(20, "Build Map");

			} else if (frameCount == 2) {
				mapArtisanManager.buildMap(bField);

				setProgress(40, "Loading texture atlas");

			} else if (frameCount == 3) {
				bField.getMap().getAtlas().loadFromFile(bField.getFileName(), "atlas");

				setProgress(60, "Loading Cover");

			} else if (frameCount == 4) {
				bField.getMap().getCover().loadFromFile(bField.getFileName(), "cover");
				setProgress(100, "Loading complete");

			} else if (frameCount == 8) {
				modelManager.setBattlefield(bField)
				main.startGame();
			}

			frameCount++;
		}

	}

	@Override
	public void cleanupState() {
		panel.hide();
	}

	public void setProgress(int value, String loadingText) {
		ind.setCurrentValue(value);
		loadingtext.text  = loadingText
	}

}
