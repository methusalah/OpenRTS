package openrts.app.example;

import network.client.ClientManager
import openrts.app.example.states.ServerConfigState
import openrts.app.example.states.UserLoginAppState

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