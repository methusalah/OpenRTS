package brainless.openrts.app.example;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import tonegod.gui.controls.buttons.ButtonAdapter;
import brainless.openrts.app.example.states.gui.network.GameLobbyState;
import brainless.openrts.app.example.states.gui.network.OpenGameState;

import com.jme3.system.AppSettings;

/**
 * A test suite of the BasicGame Default Template.
 * I just want to make sure nobody breaks the normenhansen blue box.
 * @author Gisler Garces
 */
public class MainTest {
    public static MultiplayerGame app;//Our test target application or class.
	
	private static final Logger logger = Logger.getLogger(MainTest.class.getName());

    @BeforeClass
    public static void beforeRunTests() {
        app = new MultiplayerGame();
        //Yeah, load the defaults...
        AppSettings settings = new AppSettings(true);
        //We are not going to use any input, that should be cover by our game 
        //testers they know how to do it, is their job :).
//        settings.setUseInput(false);
        app.setSettings(settings);
        //This will run in our jenkins server so, no monitor, no display :).
        app.start();
        //This method normally is called in the GL/Rendering thread so any 
        //GL-dependent resources can be initialized.
        //We don't have any display in our jenkins server so it is fine 
        //to calling manually here...
		logger.info("start waiting for app instances");
//        waitUntil({app.initialized});
		logger.info("app instances ready");
//    	Guice.createInjector(new GameGuiceModule(app)).injectMembers(app);
    }

//    private static void waitUntil(Closure condition,Integer maxTimes = 10) {
//		int waitingCounter = 0;
//		boolean waiting = true;
//		while (waiting) {
//
//			if (condition()) {
//				waiting = false;
//				return;
//			}
//
//			if (maxTimes > 0 && waitingCounter > maxTimes) {
//				throw new TechnicalException("I am waiting too long..");
//			}
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException ie) {
//				ie.printStackTrace();
//			}
//			waitingCounter++;
//		}
//	}
    
    @AfterClass
    public static void afterRunTests() {
        //Typically cleanup of native resources should happen here. 
        //This method is called in the GL/Rendering thread.
        //We don't have any display in our jenkins server so calling manually...
        try {
            app.destroy();
        } catch (Exception e) {
            //Ignore any destroy exception. This tests are not running with a 
            //display, so anything can happend, in theory we are not testing 
            //the destroy() method, remenber this is the afterClass method run 
            //after all tests, so it is for cleaning purposes mainly.
            //You could log, print or whatever you want with the exception. 
            //I´m ignoring it on purpose. :)
        }
    }
        
    @Before
    public void beforeEachTest() {
	
        //Before each...
    }

    @After
    public void afterEachTest() {
        //After each...
    }

    /**
     * It shouldn´t be any obstacle between the box and the camera.
     */

    public void testShowLoginGUIState() {
//    	UserLoginAppState userLoginAppState = (UserLoginAppState)getState(UserLoginAppState.class);
		
//    	waitUntil({!app.stateManager.initializing});
    	
    	ButtonAdapter loginButton = (ButtonAdapter) app.getScreen().getElementById("loginWindow:btnOk");
		Assert.assertEquals(loginButton.getText(), "Login");
    }
	
	public void testShowOpenGameGUIState() {		
		app.createGame();
//		waitUntil({!app.stateManager.initializing});
		OpenGameState state = app.getStateManager().getState(OpenGameState.class);
		Assert.assertNotNull(state);
	}

	public void testShowNetworkLobbyGUIState() {
		app.openGame();
//		waitUntil({!app.stateManager.initializing});
		GameLobbyState state = app.getStateManager().getState(GameLobbyState.class);
		Assert.assertNotNull(state);
	}
	
  }