package app;

import java.util.logging.Logger;

import view.EditorView;
import view.material.MaterialManager;
import model.ModelManager;
import event.EventManager;

import com.jme3.math.Vector3f;

/**
 * TeaVM entry point for the OpenRTS web build.
 *
 * Replaces MainRTS with a stripped-down version that avoids:
 *   - NiftyGUI (no jme3-niftygui in webmonkeyengine)
 *   - Desktop controllers (BattlefieldController / EditorController / GroundController)
 *   - jme3-networking
 * The editor view and model pipeline are retained so the 3-D scene renders.
 */
public class WebMainRTS extends OpenRTSApplication {

    private static final Logger logger = Logger.getLogger(WebMainRTS.class.getName());

    EditorView view;

    /** TeaVM entry point — called by the generated JavaScript bootstrap. */
    public static void main(String[] args) {
        WebMainRTS app = new WebMainRTS();
        appInstance = app;
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setUpVector(new Vector3f(0, 0, 1));
        flyCam.setEnabled(false);
        flyCam.setMoveSpeed(10f);

        MaterialManager.setAssetManager(assetManager);

        // Pass stub physicsSpace (no-op on web)
        view = new EditorView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

        EventManager.register(this);

        ModelManager.setNewBattlefield();
    }

    @Override
    public void simpleUpdate(float tpf) {
        float capped = Math.min(tpf, 0.1f);
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
        view.getActorManager().render();
        ModelManager.updateConfigs();
    }
}
