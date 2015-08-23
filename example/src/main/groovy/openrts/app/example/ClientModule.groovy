package openrts.app.example;

import openrts.app.example.states.GameAppState
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
		bind(GameAppState.class).in(Singleton.class)
		bind(GameInputInterpreter.class).in(Singleton.class)
		
	}
}