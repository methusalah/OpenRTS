package openrts.app.example.states;

import com.google.inject.AbstractModule
import com.google.inject.Singleton

class ClientModule extends AbstractModule {

	MultiplayerGame app

	@Override
	protected void configure() {

		bind(MultiplayerGame.class).toInstance(app);
		bind(HarnessState.class).in(Singleton.class);
		bind(UserLoginAppState.class).in(Singleton.class);
	}
}