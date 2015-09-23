package brainless.openrts.app.example;

import tonegod.gui.core.Screen;
import brainless.openrts.app.example.states.gui.DashboardState;
import brainless.openrts.app.example.states.gui.LoadingMapState;
import brainless.openrts.app.example.states.gui.UserLoginAppState;
import brainless.openrts.app.example.states.gui.game.BattlefieldState;
import brainless.openrts.app.example.states.gui.game.HudState;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;


class ClientModule extends AbstractModule {

	private MultiplayerGame app;
	private Screen screen;
	

	public ClientModule(MultiplayerGame app, Screen screen) {
		super();
		this.app = app;
		this.screen = screen;
	}


	@Override
	protected void configure() {
		
		bind(MultiplayerGame.class).toInstance(app);
		bind(Screen.class).toInstance(screen);
		bind(DashboardState.class).in(Singleton.class);
		bind(UserLoginAppState.class).in(Singleton.class);
		bind(BattlefieldState.class).in(Singleton.class);
		bind(GameInputInterpreter.class).in(Singleton.class);
		bind(HudState.class).in(Singleton.class);
		
		bind(LoadingMapState.class).in(Singleton.class);
		
	}
	
	
}

