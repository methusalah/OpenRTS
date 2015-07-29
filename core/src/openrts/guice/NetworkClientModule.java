package openrts.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import controller.game.MultiplayerGameNiftyController;

public class NetworkClientModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(MultiplayerGameNiftyController.class).in(Singleton.class);

	}

}