package app;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.jme3.niftygui.NiftyJmeDisplay;

import controller.battlefield.BattlefieldController;
import controller.battlefield.BattlefieldGUIController;
import controller.battlefield.BattlefieldInputInterpreter;
import controller.editor.EditorController;
import controller.editor.EditorGUIController;
import controller.editor.EditorInputInterpreter;
import controller.ground.GroundController;
import controller.ground.GroundGUIController;
import controller.ground.GroundInputInterpreter;
import de.lessvoid.nifty.Nifty;

class MainGuiceModule extends AbstractModule {

	NiftyJmeDisplay niftyDisplay;
	
	public MainGuiceModule(NiftyJmeDisplay niftyDisplay) {
		this.niftyDisplay = niftyDisplay;
	}

	@Override
	protected void configure() {

		 bind(BattlefieldController.class).annotatedWith(Names.named("BattlefieldController")).to(BattlefieldController.class).in(Singleton.class);
		 bind(BattlefieldGUIController.class).annotatedWith(Names.named("BattlefieldGUIController")).to(BattlefieldGUIController.class)
		 .in(Singleton.class);
		 bind(BattlefieldInputInterpreter.class).annotatedWith(Names.named("BattlefieldInputInterpreter")).to(BattlefieldInputInterpreter.class)
		 .in(Singleton.class);

		 bind(EditorGUIController.class).annotatedWith(Names.named("EditorGUIController")).to(EditorGUIController.class).in(Singleton.class);
		 bind(EditorInputInterpreter.class).annotatedWith(Names.named("EditorInputInterpreter")).to(EditorInputInterpreter.class).in(Singleton.class);
		 bind(EditorController.class).annotatedWith(Names.named("EditorController")).to(EditorController.class).in(Singleton.class);

		 bind(GroundController.class).annotatedWith(Names.named("GroundController")).to(GroundController.class).in(Singleton.class);
		 bind(GroundGUIController.class).annotatedWith(Names.named("GroundGUIController")).to(GroundGUIController.class).in(Singleton.class);
		 bind(GroundInputInterpreter.class).annotatedWith(Names.named("GroundInputInterpreter")).to(GroundInputInterpreter.class).in(Singleton.class);	
		 
		 bind(NiftyJmeDisplay.class).toInstance(niftyDisplay);
		 bind(Nifty.class).toInstance(niftyDisplay.getNifty());
	}
	
	
	
	
}
