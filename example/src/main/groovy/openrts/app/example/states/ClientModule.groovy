package openrts.app.example.states;

import network.client.ClientManager

import com.google.inject.AbstractModule
import com.google.inject.Singleton

class ClientModule extends AbstractModule {

	MultiplayerGame app

	@Override
	protected void configure() {

		bind(MultiplayerGame.class).toInstance(app)
		bind(ServerConfigState.class).in(Singleton.class)
		bind(UserLoginAppState.class).in(Singleton.class)
		bind(ClientManager.class).in(Singleton.class)
	}
}