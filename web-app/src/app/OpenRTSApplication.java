package app;

import java.util.logging.Logger;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;

/**
 * Web build of OpenRTSApplication.
 *
 * Extends SimpleApplication (available in webmonkeyengine) instead of
 * LegacyApplication. Desktop-only features are stubbed out:
 *   - No java.util.prefs
 *   - No JmeSystem.showSettingsDialog
 *   - No jme3-networking / ClientManager
 *   - No NiftyGUI
 *   - Physics via no-op BulletAppState stub
 */
public abstract class OpenRTSApplication extends SimpleApplication implements PhysicsTickListener {

    private static final Logger logger = Logger.getLogger(OpenRTSApplication.class.getName());

    public static OpenRTSApplication appInstance;

    protected float secondCounter = 0.0f;
    protected BitmapText fpsText;
    protected MyDebugger debugger;
    protected BulletAppState bulletAppState;

    private class AppActionListener implements ActionListener {
        @Override
        public void onAction(String name, boolean value, float tpf) {
            if (!value) return;
            if ("SIMPLEAPP_Exit".equals(name)) stop();
        }
    }

    private final AppActionListener actionListener = new AppActionListener();

    // SimpleApplication.simpleInitApp() is declared abstract — subclasses fill it in.

    @Override
    public void initialize() {
        super.initialize();

        // Web build: physics is stubbed (no-op BulletAppState)
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        initTexts();
        viewPort.attachScene(rootNode);
        guiViewPort.attachScene(guiNode);

        if (inputManager != null) {
            inputManager.addMapping("SIMPLEAPP_Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
            inputManager.addListener(actionListener, "SIMPLEAPP_Exit");
        }
    }

    private void initTexts() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        fpsText = new BitmapText(guiFont, false);
        fpsText.setLocalTranslation(0, fpsText.getLineHeight(), 0);
        fpsText.setText("Frames per second");
        guiNode.attachChild(fpsText);

        debugger = new MyDebugger(0, 600, assetManager.loadFont("Interface/Fonts/Console.fnt"));
        guiNode.attachChild(debugger.getNode());
    }

    @Override
    public void update() {
        super.update();
        if (speed == 0 || paused) return;

        float tpf = timer.getTimePerFrame() * speed;

        secondCounter += timer.getTimePerFrame();
        int fps = (int) timer.getFrameRate();
        if (secondCounter >= 1.0f) {
            fpsText.setText("Frames per second: " + fps);
            secondCounter = 0.0f;
        }

        stateManager.update(tpf);

        debugger.reset();
        simpleUpdate(tpf);
        debugger.getNode();

        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        stateManager.render(renderManager);
        renderManager.render(tpf, true);
        simpleRender(renderManager);
        stateManager.postRender();
    }

    @Override
    public void simpleUpdate(float tpf) {}

    @Override
    public void simpleRender(RenderManager rm) {}

    public void simplePhysicsUpdate(float tpf) {}

    @Override
    public void physicsTick(PhysicsSpace space, float f) { simplePhysicsUpdate(f); }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float f) {}

    public PhysicsSpace getPhysicsSpace() { return bulletAppState.getPhysicsSpace(); }

    public void toggleToFullscreen() {
        // Not supported on web — fullscreen must be handled by the browser.
        logger.warning("Fullscreen toggle not supported on web.");
    }

    public static void main(OpenRTSApplication app) {
        appInstance = app;
        appInstance.start();
    }
}
