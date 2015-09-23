package brainless.openrts.app.example;

import groovy.transform.CompileStatic
import tonegod.gui.core.Screen
import brainless.openrts.app.example.states.gui.DashboardState;
import brainless.openrts.app.example.states.gui.UserLoginAppState;
import brainless.openrts.app.example.states.gui.LoadingMapState;
import brainless.openrts.app.example.states.gui.game.BattlefieldState;
import brainless.openrts.app.example.states.gui.game.HudState;

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
		bind(DashboardState.class).in(Singleton.class)
		bind(UserLoginAppState.class).in(Singleton.class)
		bind(BattlefieldState.class).in(Singleton.class)
		bind(GameInputInterpreter.class).in(Singleton.class)
		bind(HudState.class).in(Singleton.class)
		
		bind(LoadingMapState.class).in(Singleton.class)
		
	}
	
	
}

