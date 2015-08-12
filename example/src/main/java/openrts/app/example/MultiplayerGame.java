package openrts.app.example;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import openrts.app.example.states.AppStateCommon;
import openrts.app.example.states.HarnessState;
import openrts.app.example.states.UserLoginAppState;
import tonegod.gui.core.Screen;
import tonegod.gui.tests.states.TestState;
import tonegod.gui.tests.states.buttons.ButtonState;
import tonegod.gui.tests.states.emitter.EmitterState;
import tonegod.gui.tests.states.spatial.SpatialState;
import tonegod.gui.tests.states.sprite.SpriteState;
import tonegod.gui.tests.states.subscreen.EmbeddedGUIState;
import tonegod.gui.tests.states.text.AnimatedTextState;
import tonegod.gui.tests.states.text.TextLabelState;
import tonegod.gui.tests.states.windows.WindowState;
import app.OpenRTSApplicationWithDI;

import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;

public class MultiplayerGame extends OpenRTSApplicationWithDI {

	private static final Logger logger = Logger.getLogger(MultiplayerGame.class.getName());

	//<editor-fold desc="VARIABLES">
	// Config settings for initial load
	// Library default theme
	public static final String BASE_THEME_PATH = "tonegod/gui/style/";
	public static final String DEFAULT_PATH = "def/";
	public static final String ATLAS_PATH = "atlasdef/";
	public static final String ASSET_PATH = "atlasdef/";
	// Theme pack
	//	public static final String BASE_THEME_PATH = "tonegod/gui/themes/fallout/";
	//	public static final String DEFAULT_PATH = "";
	//	public static final String ATLAS_PATH = "atlas/";
	//	public static final String ASSET_PATH = "assets/";
	// Initial GUI Extras settings
	public static final boolean USE_ATLAS = true;
	public static final boolean USE_UI_AUDIO = true;
	public static final boolean USE_CUSTOM_CURSORS = true;
	public static final boolean USE_CURSOR_EFFECTS = true;
	public static final boolean USE_TOOLTIPS = true;

	private boolean fixCam = false;
	private boolean hasLights = false;
	private DirectionalLight dl;
	private AmbientLight al;

	// GUI Variables
	private Screen screen;
	private BitmapFont defaultFont;

	// States
	private List<AppStateCommon> states = new ArrayList();
	private HarnessState harness;
	UserLoginAppState userlogin;
	private TestState tests;
	private WindowState winState;
	private SpriteState spriteState;
	private AnimatedTextState animatedTextState;
	private TextLabelState labelState;
	private ButtonState buttonState;
	private EmitterState emitterState;
	private EmbeddedGUIState subScreenState;
	private SpatialState spatialState;

	public static void main(String[] args) {
		// Properties preferences = new Properties();
		// try {
		// FileInputStream configFile = new FileInputStream("logging.properties");
		// preferences.load(configFile);
		// LogManager.getLogManager().readConfiguration(configFile);
		// } catch (IOException ex) {
		// System.err.println("WARNING: Could not open configuration file - please create a logging.properties for correct logging");
		// System.err.println("WARNING: Logging not configured (console output only)");
		// }
		MultiplayerGame app = new MultiplayerGame();
		app.start();
	}


	@Override
	public void simpleInitApp() {
		initScreen();
		initLights();
		userlogin = new UserLoginAppState(this);
		states.add(userlogin);
		stateManager.attach(userlogin);
	}

	private void initScreen() {
		screen = new Screen(this, BASE_THEME_PATH + (USE_ATLAS ? ATLAS_PATH : DEFAULT_PATH) + "style_map.gui.xml");
		if (USE_ATLAS) {
			screen.setUseTextureAtlas(true, BASE_THEME_PATH + ASSET_PATH + "atlas.png");
		}
		screen.setUseUIAudio(USE_UI_AUDIO);
		screen.setUseCustomCursors(USE_CUSTOM_CURSORS);
		screen.setUseCursorEffects(USE_CURSOR_EFFECTS);
		screen.setUseToolTips(USE_TOOLTIPS);
		guiNode.addControl(screen);

		defaultFont = getAssetManager().loadFont(screen.getStyle("Font").getString("defaultFont"));

		Camera cam2;

		flyCam.setEnabled(true);
		flyCam.setMoveSpeed(30);
		flyCam.setDragToRotate(true);
		inputManager.setCursorVisible(true);
	}

	private void initStates() {
		harness = new HarnessState(this);
		states.add(harness);
		// tests = new TestState(this);
		// states.add(tests);
		// winState = new WindowState(this);
		// states.add(winState);
		// spriteState = new SpriteState(this);
		// states.add(spriteState);
		// animatedTextState = new AnimatedTextState(this);
		// states.add(animatedTextState);
		// labelState = new TextLabelState(this);
		// states.add(labelState);
		// buttonState = new ButtonState(this);
		// states.add(buttonState);
		// emitterState = new EmitterState(this);
		// states.add(emitterState);
		// subScreenState = new EmbeddedGUIState(this);
		// states.add(subScreenState);
		// spatialState = new SpatialState(this);
		// states.add(spatialState);

		stateManager.attach(harness);
		// stateManager.attach(tests);

	}
	public Screen getScreen() {
		return this.screen;
	}

	public HarnessState getHarness() { return this.harness; }
	public TestState getTests() { return this.tests; }

	public BitmapFont getFont() {
		return this.defaultFont;
	}

	public List<AppStateCommon> getStates() { return states; }

	private void initLights() {
		al = new AmbientLight();
		al.setColor(new ColorRGBA(0.25f,0.25f,0.25f,1f));

		dl = new DirectionalLight();
		dl.setDirection(new Vector3f(1f,-1f,-1f).normalizeLocal());
		dl.setColor(ColorRGBA.White);
	}

	public void addSceneLights() {
		if (!hasLights) {
			rootNode.addLight(al);
			rootNode.addLight(dl);
			hasLights = true;
		}
	}

	public void removeSceneLights() {
		if (hasLights) {
			rootNode.removeLight(al);
			rootNode.removeLight(dl);
			hasLights = false;
		}
	}

	@Override
	public void reshape(int w, int h) {
		super.reshape(w, h);
		for (AppStateCommon state : states) {
			state.reshape();
		}
	}

	@Override
	public void simpleUpdate(float tpf) {

	}

	@Override
	public void simpleRender(RenderManager rm) {  }

	public void sucessfullLoggedIn() {
		stateManager.detach(userlogin);
		initStates();
	}

}
