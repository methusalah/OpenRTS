package app.example;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import model.ModelManager;
import model.battlefield.Battlefield;
import model.builders.MapArtisanUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;

public class TestLoadingScreen extends SimpleApplication implements ScreenController, Controller {

	private NiftyJmeDisplay niftyDisplay;
	private Nifty nifty;
	private Element progressBarElement;
	private float frameCount = 0;
	private boolean load = false;
	private TextRenderer textRenderer;
	protected static String mapfilename = "assets/maps/test.btf";
	Battlefield bField = null;

	public static void main(String[] args) {
		TestLoadingScreen app = new TestLoadingScreen();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		flyCam.setEnabled(false);
		niftyDisplay = new NiftyJmeDisplay(assetManager,
				inputManager,
				audioRenderer,
				guiViewPort);
		nifty = niftyDisplay.getNifty();

		nifty.fromXml("Interface/nifty_loading.xml", "start", this);

		guiViewPort.addProcessor(niftyDisplay);
	}

	@Override
	public void simpleUpdate(float tpf) {

		if (load) { //loading is done over many frames
			if (frameCount == 1) {
				File file = new File(mapfilename);
				Element element = nifty.getScreen("loadlevel").findElementByName("loadingtext");
				textRenderer = element.getRenderer(TextRenderer.class);
				
				ModelManager.setBattlefieldUnavailable();

				try {
					ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
					bField = mapper.readValue(file, Battlefield.class);
					bField.setFileName(file.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}

				setProgress(0.2f, "Build Map");

			} else if (frameCount == 2) {
				MapArtisanUtil.buildMap(bField);

				setProgress(0.4f, "Loading texture atlas");

			} else if (frameCount == 3) {
				bField.getMap().getAtlas().loadFromFile(bField.getFileName(), "atlas");

				setProgress(0.5f, "Loading Cover");

			} else if (frameCount == 4) {
				bField.getMap().getCover().loadFromFile(bField.getFileName(), "cover");
				// setProgress(0.6f, "Creating terrain");

				// } else if (frameCount == 5) {
				// AbstractHeightMap heightmap = null;
				// Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
				// heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
				//
				// heightmap.load();
				// terrain = new TerrainQuad("my terrain", 65, 513, heightmap.getHeightMap());
				// setProgress(0.8f, "Positioning terrain");
				//
				// } else if (frameCount == 6) {
				// terrain.setMaterial(mat_terrain);
				//
				// terrain.setLocalTranslation(0, -100, 0);
				// terrain.setLocalScale(2f, 1f, 2f);
				// rootNode.attachChild(terrain);
				// setProgress(0.9f, "Loading cameras");
				//
				// } else if (frameCount == 7) {
				// List<Camera> cameras = new ArrayList<Camera>();
				// cameras.add(getCamera());
				// TerrainLodControl control = new TerrainLodControl(terrain, cameras);
				// terrain.addControl(control);
				setProgress(1f, "Loading complete");

			} else if (frameCount == 8) {
				nifty.gotoScreen("end");
				nifty.exit();
				guiViewPort.removeProcessor(niftyDisplay);
				flyCam.setEnabled(true);
				flyCam.setMoveSpeed(50);
			}

			frameCount++;
		}
	}

	public void setProgress(final float progress, String loadingText) {
		final int MIN_WIDTH = 32;
		int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
		progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
		progressBarElement.getParent().layoutElements();

		textRenderer.setText(loadingText);
	}

	public void showLoadingMenu() {
		nifty.gotoScreen("loadlevel");
		load = true;
	}

	@Override
	public void onStartScreen() {
	}

	@Override
	public void onEndScreen() {
	}

	@Override
	public void bind(Nifty nifty, Screen screen) {
		progressBarElement = nifty.getScreen("loadlevel").findElementByName("progressbar");
	}

	// methods for Controller
	@Override
	public boolean inputEvent(final NiftyInputEvent inputEvent) {
		return false;
	}

	@Override
	public void bind(Nifty nifty, Screen screen, Element elmnt, Properties prprts, Attributes atrbts) {
		progressBarElement = elmnt.findElementByName("progressbar");
	}

	@Override
	public void init(Properties prprts, Attributes atrbts) {
	}

	@Override
	public void onFocus(boolean getFocus) {
	}
}