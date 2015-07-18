/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package openrts.guice;

import java.util.Collection;
import java.util.LinkedList;

import network.client.ClientManager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public abstract class GuiceApplication extends SimpleApplication {

	private Injector injector;

	@Override
	public final void simpleInitApp() {
		Application app = this;
		/* building modules */
		Collection<Module> modules = new LinkedList<Module>();
		modules.add(new AbstractModule() {

			@Override
			protected void configure() {
				bind(AssetManager.class).annotatedWith(AssetManagerRef.class).toInstance(assetManager);
				bind(Node.class).annotatedWith(GuiNodeRef.class).toInstance(guiNode);
				bind(AppSettings.class).annotatedWith(AppSettingsRef.class).toInstance(settings);
				bind(AppStateManager.class).annotatedWith(StateManagerRef.class).toInstance(stateManager);
				bind(Application.class).toInstance(app);

				bind(ClientManager.class).in(Singleton.class);
			}
		});

		this.addApplicationModules(modules);

		this.injector = Guice.createInjector(modules);
		this.injector.injectMembers(this);

		this.guiceAppInit();
	}

	public abstract void guiceAppInit();

	protected void addApplicationModules(Collection<Module> modules) {
	}
}
