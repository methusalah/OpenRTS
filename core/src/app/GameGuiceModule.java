package app;

import model.ModelManager;
import model.battlefield.BattlefieldFactory;
import model.battlefield.army.ArmyManager;
import model.builders.entity.CliffShapeBuilder;
import model.builders.entity.EffectBuilder;
import model.builders.entity.ManmadeFaceBuilder;
import model.builders.entity.MapStyleBuilder;
import model.builders.entity.MoverBuilder;
import model.builders.entity.NaturalFaceBuilder;
import model.builders.entity.ProjectileBuilder;
import model.builders.entity.TrinketBuilder;
import model.builders.entity.TurretBuilder;
import model.builders.entity.UnitBuilder;
import model.builders.entity.WeaponBuilder;
import model.builders.entity.actors.ActorBuilder;
import model.builders.entity.actors.AnimationActorBuilder;
import model.builders.entity.actors.ModelActorBuilder;
import model.builders.entity.actors.ParticleActorBuilder;
import model.builders.entity.actors.PhysicActorBuilder;
import model.builders.entity.actors.SoundActorBuilder;
import model.builders.entity.definitions.BuilderManager;
import model.builders.entity.definitions.DefParser;
import model.editor.Pencil;
import model.editor.ToolManager;
import model.editor.tools.AtlasTool;
import model.editor.tools.CliffTool;
import model.editor.tools.HeightTool;
import model.editor.tools.RampTool;
import model.editor.tools.TrinketTool;
import model.editor.tools.UnitTool;
import openrts.guice.annotation.AppSettingsRef;
import openrts.guice.annotation.AudioRendererRef;
import openrts.guice.annotation.GuiNodeRef;
import openrts.guice.annotation.RootNodeRef;
import openrts.guice.annotation.StateManagerRef;
import openrts.guice.annotation.ViewPortRef;
import util.MapArtisanManager;
import util.TileArtisanManager;
import view.EditorView;
import view.MapView;
import view.acting.ActorDrawer;
import view.camera.GroundCamera;
import view.mapDrawing.EditorRenderer;
import view.mapDrawing.MapDrawer;
import view.material.MaterialManager;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import controller.CommandManager;
import controller.Reporter;
import controller.SpatialSelector;

class GameGuiceModule extends AbstractModule {

	private OpenRTSApplicationWithDI main;
	
	public GameGuiceModule(OpenRTSApplicationWithDI main ) {
		this.main = main;
	}

	@Override
	protected void configure() {

//		 bind(BattlefieldController.class).annotatedWith(Names.named("BattlefieldController")).to(BattlefieldController.class).in(Singleton.class);
//		 bind(BattlefieldGUIController.class).annotatedWith(Names.named("BattlefieldGUIController")).to(BattlefieldGUIController.class)
//		 .in(Singleton.class);
//		 bind(BattlefieldInputInterpreter.class).annotatedWith(Names.named("BattlefieldInputInterpreter")).to(BattlefieldInputInterpreter.class)
//		 .in(Singleton.class);

		 //Editor is currently disabled
//		 bind(EditorGUIController.class).annotatedWith(Names.named("EditorGUIController")).to(EditorGUIController.class).in(Singleton.class);
//		 bind(EditorInputInterpreter.class).annotatedWith(Names.named("EditorInputInterpreter")).to(EditorInputInterpreter.class).in(Singleton.class);
//		 bind(EditorController.class).annotatedWith(Names.named("EditorController")).to(EditorController.class).in(Singleton.class);
//		 bind(EditorGUIDrawer.class).annotatedWith(Names.named("EditorGUIDrawer")).to(EditorGUIDrawer.class).in(Singleton.class);
		 

//		 bind(MapLoadingScreen.class).annotatedWith(Names.named("MapLoadingScreen")).to(MapLoadingScreen.class).in(Singleton.class);
//		 bind(MultiplayerGameController.class).annotatedWith(Names.named("MultiplayerGameController")).to(MultiplayerGameController.class).in(Singleton.class);
//		 bind(MultiplayerGameInputInterpreter.class).annotatedWith(Names.named("MultiplayerGameInputInterpreter")).to(MultiplayerGameInputInterpreter.class).in(Singleton.class);
		 
		 
//		 bind(GroundController.class).annotatedWith(Names.named("GroundController")).to(GroundController.class).in(Singleton.class);
//		 bind(GroundGUIController.class).annotatedWith(Names.named("GroundGUIController")).to(GroundGUIController.class).in(Singleton.class);
//		 bind(GroundInputInterpreter.class).annotatedWith(Names.named("GroundInputInterpreter")).to(GroundInputInterpreter.class).in(Singleton.class);	
		 
		 bind(CommandManager.class).in(Singleton.class);
		 bind(Reporter.class).in(Singleton.class);
		 bind(SpatialSelector.class).in(Singleton.class);
//		 bind(Actor.class).in(Singleton.class);
//		 bind(CollisionManager.class).in(Singleton.class);
		 bind(ArmyManager.class).in(Singleton.class);
		 bind(BattlefieldFactory.class).in(Singleton.class);

		 bind(BuilderManager.class).in(Singleton.class);
//		 bind(Definition.class).in(Singleton.class); => no singleton
		 bind(DefParser.class).in(Singleton.class);
		 
//		 bind(AnimationActorBuilder.class).in(Singleton.class);
//		 bind(ParticleActorBuilder.class).in(Singleton.class);
//		 bind(PhysicActorBuilder.class).in(Singleton.class);
//		 bind(ModelActorBuilder.class).in(Singleton.class);
//		 bind(SoundActorBuilder.class).in(Singleton.class);
//		 bind(AnimationActorBuilder.class).in(Singleton.class);
//		 bind(ActorBuilder.class).in(Singleton.class); 
//		 
//		 
//		bind(CliffShapeBuilder.class).in(Singleton.class);
//		bind(EffectBuilder.class).in(Singleton.class);
//		bind(ManmadeFaceBuilder.class).in(Singleton.class);
//		bind(MapStyleBuilder.class).in(Singleton.class);
//		bind(MoverBuilder.class).in(Singleton.class);
//		bind(NaturalFaceBuilder.class).in(Singleton.class);
//		bind(ProjectileBuilder.class).in(Singleton.class);
//		bind(TrinketBuilder.class).in(Singleton.class);
//		bind(TurretBuilder.class).in(Singleton.class);
//		bind(UnitBuilder.class).in(Singleton.class);
//		bind(WeaponBuilder.class).in(Singleton.class);
		
		bind(AtlasTool.class).in(Singleton.class);
		bind(CliffTool.class).in(Singleton.class);
		bind(HeightTool.class).in(Singleton.class);
		bind(RampTool.class).in(Singleton.class);
		bind(TrinketTool.class).in(Singleton.class);
		bind(UnitTool.class).in(Singleton.class);
		bind(Pencil.class).in(Singleton.class);
		bind(ToolManager.class).in(Singleton.class);
		bind(ModelManager.class).in(Singleton.class);
		bind(MapArtisanManager.class).in(Singleton.class);
		bind(TileArtisanManager.class).in(Singleton.class);
		
		bind(ActorDrawer.class).in(Singleton.class);
		bind(GroundCamera.class).in(Singleton.class);
//		bind(IsometricCamera.class).in(Singleton.class);
		
		bind(MapArtisanManager.class).in(Singleton.class);
		bind(EditorRenderer.class).in(Singleton.class);

//		bind(LightDrawer.class).in(Singleton.class);
		bind(MapDrawer.class).in(Singleton.class);
		
		bind(MaterialManager.class).in(Singleton.class);
		
		bind(EditorView.class).in(Singleton.class);
		bind(MapView.class).in(Singleton.class);
		
		bind(AssetManager.class).toInstance(main.getAssetManager());
		bind(Node.class).annotatedWith(GuiNodeRef.class).toInstance(main.guiNode);
		bind(AppSettings.class).annotatedWith(AppSettingsRef.class).toInstance(main.getSettings());
		bind(AppStateManager.class).annotatedWith(StateManagerRef.class).toInstance(main.getStateManager());
		bind(ViewPort.class).annotatedWith(Names.named("GuiViewPort")).toInstance(main.getGuiViewPort());
		bind(AudioRenderer.class).annotatedWith(AudioRendererRef.class).toInstance(main.getAudioRenderer());
		bind(InputManager.class).toInstance(main.getInputManager());
		bind(Camera.class).toInstance(main.getCamera());

		bind(Application.class).toInstance(main);


		bind(Node.class).annotatedWith(RootNodeRef.class).toInstance(main.getRootNode());
		bind(ViewPort.class).annotatedWith(ViewPortRef.class).toInstance(main.getViewPort());

	}
	
	
	
	
}
