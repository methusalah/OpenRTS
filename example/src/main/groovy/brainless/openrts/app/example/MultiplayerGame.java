package brainless.openrts.app.example;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import tonegod.gui.core.Screen;
import app.OpenRTSApplicationWithDI;
import brainless.openrts.app.example.states.NetworkClientState;
import brainless.openrts.app.example.states.gui.DashboardState;
import brainless.openrts.app.example.states.gui.LoadingMapState;
import brainless.openrts.app.example.states.gui.UserLoginAppState;
import brainless.openrts.app.example.states.gui.game.BattlefieldState;
import brainless.openrts.app.example.states.gui.game.HudState;
import brainless.openrts.app.example.states.gui.network.GameLobbyState;
import brainless.openrts.app.example.states.gui.network.NetworkDashboardState;
import brainless.openrts.app.example.states.gui.network.OpenGameState;
import brainless.openrts.model.Game;

import com.google.inject.Module;
import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;


public class MultiplayerGame extends OpenRTSApplicationWithDI {

	private static final Logger logger = Logger.getLogger(MultiplayerGame.class.getName());
	
	public static final String BASE_THEME_PATH = "tonegod/gui/style/";
	public static final String DEFAULT_PATH = "def/";
	public static final String ATLAS_PATH = "atlasdef/";
	public static final String ASSET_PATH = "atlasdef/";

	// Initial GUI Extras settings
	public static final boolean USE_ATLAS = true;

	private Game game;
 
	private boolean fixCam = false;
	private boolean hasLights = false;
	private DirectionalLight dl;
	private AmbientLight al;

	// GUI Variables
	private Screen screen;
	private BitmapFont defaultFont;

	private NetworkDashboardState networkDashboardState;
	private BattlefieldState gameState;
	private HudState hudState;
	private LoadingMapState loadingMapState;
	private DashboardState dashboardState;
	private GameLobbyState gameLobbyState;
	private OpenGameState openGameState;
	private UserLoginAppState userlogin;

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

		AppSettings settings = new AppSettings(true);
		settings.setResolution(800,600);

		MultiplayerGame app = new MultiplayerGame();
		app.setSettings(settings);
		app.start();
	}


	@Override
	public void simpleInitApp() {
		initScreen();
		List<Module> modules = new ArrayList<Module>([
			new ClientModule(app: this, screen: screen)
		])
		initGuice(modules);
		initLights();
		game = new Game();
		userlogin = injector.getInstance(UserLoginAppState.class);
		stateManager.attach(userlogin);
		pauseOnLostFocus = false
	}

	private void initScreen() {
		screen = new Screen(this, BASE_THEME_PATH + (USE_ATLAS ? ATLAS_PATH : DEFAULT_PATH) + "style_map.gui.xml");

		if (USE_ATLAS) {
			screen.setUseTextureAtlas(true, BASE_THEME_PATH + ASSET_PATH + "atlas.png");
		}
		screen.setUseUIAudio(true);
		screen.setUseCustomCursors(true);
		screen.setUseCursorEffects(true);
		screen.setUseToolTips(true);
		guiNode.addControl(screen);

		defaultFont = getAssetManager().loadFont(screen.getStyle("Font").getString("defaultFont"));

		inputManager.setCursorVisible(true);
	}

	public Screen getScreen() {
		return this.screen;
	}

	public BitmapFont getFont() {
		return this.defaultFont;
	}

	//	public List<AppStateCommon> getStates() { return states; }

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
	}

	@Override
	public void simpleUpdate(float tpf) {

	}
 
	@Override
	public void simpleRender(RenderManager rm) {  }

	private void  sucessfullLoggedIn(String username) {
		stateManager.detach(userlogin);
		dashboardState = injector.getInstance(DashboardState.class);
		game.getMySelf().name = username;
		stateManager.attach(dashboardState);
	}

	def createGame(){
		stateManager.detach(dashboardState)
		stateManager.detach(networkDashboardState)

		openGameState = injector.getInstance(OpenGameState.class);
		stateManager.attach(openGameState)
	}

	def openGame(){
		stateManager.detach(openGameState)
		stateManager.detach(networkDashboardState)
		
		gameLobbyState = injector.getInstance(GameLobbyState.class)
		stateManager.attach(gameLobbyState)

	}

	def joinGame(){
		stateManager.detach(networkDashboardState)
		gameLobbyState = injector.getInstance(GameLobbyState.class);
		stateManager.attach(gameLobbyState)
	}

	def startGame() {
		stateManager.detach(gameLobbyState)
		loadingMapState = injector.getInstance(LoadingMapState.class);
		stateManager.attach(loadingMapState)
	}
	
	def runGame() {
		stateManager.detach(loadingMapState)
		loadingMapState.setEnabled(false)

		gameState = injector.getInstance(BattlefieldState.class);
		stateManager.attach(gameState);

		hudState = injector.getInstance(HudState.class);
		stateManager.attach(hudState);

	}

	def connectToServer(String host) {
		def client = injector.getInstance(NetworkClientState.class);
		client.host = host
		stateManager.attach(client);

		stateManager.detach(networkDashboardState)
		userlogin.enabled = false

		networkDashboardState = injector.getInstance(NetworkDashboardState.class);
		//		states.add(loadingMapState)
		stateManager.attach(networkDashboardState)

	}


	@Override
	public void destroy() {
		super.destroy();
		this.stop();
	}

}
