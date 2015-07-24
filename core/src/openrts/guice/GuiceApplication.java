/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package openrts.guice;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.jme3.app.Application;

public abstract class GuiceApplication extends Application {

	private static final Logger logger = Logger.getLogger(GuiceApplication.class.getName());

	protected Injector injector;
	protected Collection<Module> modules;

	public final void simpleInitApp() {
		// Application app = this;
		// /* building modules */
		// modules = new LinkedList<Module>();
		// modules.add(new AbstractModule() {
		//
		// @Override
		// protected void configure() {
		// bind(AssetManager.class).annotatedWith(AssetManagerRef.class).toInstance(assetManager);
		// bind(Node.class).annotatedWith(GuiNodeRef.class).toInstance(guiNode);
		// bind(AppSettings.class).annotatedWith(AppSettingsRef.class).toInstance(settings);
		// bind(AppStateManager.class).annotatedWith(StateManagerRef.class).toInstance(stateManager);
		// bind(Node.class).annotatedWith(Names.named("RootNode")).toInstance(rootNode);
		// bind(Node.class).annotatedWith(Names.named("GuiNode")).toInstance(guiNode);
		// bind(ViewPort.class).annotatedWith(Names.named("ViewPort")).toInstance(viewPort);
		// bind(ViewPort.class).annotatedWith(Names.named("GuiViewPort")).toInstance(guiViewPort);
		// bind(AudioRenderer.class).annotatedWith(AudioRendererRef.class).toInstance(audioRenderer);
		// bind(InputManager.class).annotatedWith(InputManagerRef.class).toInstance(inputManager);
		// bind(Camera.class).annotatedWith(Names.named("Camera")).toInstance(cam);
		// bind(FlyByCamera.class).annotatedWith(Names.named("FlyByCamera")).toInstance(flyCam);
		//
		// bind(Application.class).toInstance(app);
		//
		// bind(ClientManager.class).in(Singleton.class);
		// bind(NetworkNiftyController.class).in(Singleton.class);
		//
		// }
		// });
		//
		// this.addApplicationModules(modules);
		//
		// this.injector = Guice.createInjector(modules);
		// this.injector.injectMembers(this);

		this.guiceAppInit();
	}

	public abstract void guiceAppInit();

	protected void addApplicationModules(Collection<Module> modules) {
	}
}
