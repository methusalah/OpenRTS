package brainless.openrts.app.example;

import groovy.transform.CompileStatic
import tonegod.gui.core.Screen
import brainless.openrts.app.example.states.GameBattlefieldAppState
import brainless.openrts.app.example.states.GameHudState
import brainless.openrts.app.example.states.LoadingMapState
import brainless.openrts.app.example.states.GuiServerConfigState
import brainless.openrts.app.example.states.UserLoginAppState

import com.google.inject.AbstractModule
import com.google.inject.Singleton


@CompileStatic
class ClientModule extends AbstractModule {

	MultiplayerGame app
	Screen screen

	@Override
	protected void configure() {
		
		bind(MultiplayerGame.class).toInstance(app)
		bind(Screen.class).toInstance(screen)
		bind(GuiServerConfigState.class).in(Singleton.class)
		bind(UserLoginAppState.class).in(Singleton.class)
		bind(GameBattlefieldAppState.class).in(Singleton.class)
		bind(GameInputInterpreter.class).in(Singleton.class)
		bind(GameHudState.class).in(Singleton.class)
		
		bind(LoadingMapState.class).in(Singleton.class)
		
	}
	
	
}

