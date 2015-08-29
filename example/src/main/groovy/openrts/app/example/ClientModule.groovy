package openrts.app.example;

import groovy.transform.CompileStatic
import openrts.app.example.states.GameBattlefieldAppState
import openrts.app.example.states.GameHudState
import openrts.app.example.states.LoadingMapState;
import openrts.app.example.states.ServerConfigState
import openrts.app.example.states.UserLoginAppState
import tonegod.gui.core.Screen

import com.google.inject.AbstractModule
import com.google.inject.Singleton
import com.google.inject.matcher.Matchers


@CompileStatic
class ClientModule extends AbstractModule {

	MultiplayerGame app
	Screen screen

	@Override
	protected void configure() {
		
		bind(MultiplayerGame.class).toInstance(app)
		bind(Screen.class).toInstance(screen)
		bind(ServerConfigState.class).in(Singleton.class)
		bind(UserLoginAppState.class).in(Singleton.class)
		bind(GameBattlefieldAppState.class).in(Singleton.class)
		bind(GameInputInterpreter.class).in(Singleton.class)
		bind(GameHudState.class).in(Singleton.class)
		
		bind(LoadingMapState.class).in(Singleton.class)
		
	}
	
	
}

