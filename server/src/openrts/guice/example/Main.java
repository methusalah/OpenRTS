package openrts.guice.example;

import app.OpenRTSApplicationWithDI;

import com.google.inject.Inject;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;

/**
 * Paying with JMonkeyEngine and Guice
 */
public class Main extends OpenRTSApplicationWithDI {

	public static void main(String[] args) {
		Main app = new Main();
		app.start();
	}

	@Inject
	private BulletAppState bulletAppState;
	@Inject
	private MessageManager messageManager;;


	@Override
	public void simpleInitApp() {
		/** Set up Physics */
		this.stateManager.attach(bulletAppState);

		bulletAppState.getPhysicsSpace().setGravity(Vector3f.ZERO);

		/* Clean text on the screen */
		guiNode.detachAllChildren();
	}

	@Override
	public void simpleUpdate(float tpf) {
		// if (ball.getLocalTranslation().getY() < paddle.getLocalTranslation().getY()) {
		// this.messageManager.setMessage("You Suck!");
		// }
	}



}
