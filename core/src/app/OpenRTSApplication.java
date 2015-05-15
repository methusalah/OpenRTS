package app;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
import com.jme3.system.JmeSystem;
import com.jme3.util.BufferUtils;

import exception.TechnicalException;
import geometry.math.MyRandom;
import geometry.tools.LogUtil;

public abstract class OpenRTSApplication extends Application implements PhysicsTickListener {

	protected static OpenRTSApplication appInstance;

    protected Node rootNode = new Node("Root Node");
    protected Node guiNode = new Node("Gui Node");

    protected float secondCounter = 0.0f;
    
    protected BitmapText fpsText;
    protected MyDebugger debugger;
    protected StatsView statsView;

    protected AzertyFlyByCamera flyCam;
    protected boolean showSettings = true;

    private AppActionListener actionListener = new AppActionListener();

    BulletAppState bulletAppState;
    
    private class AppActionListener implements ActionListener {
        @Override
		public void onAction(String name, boolean value, float tpf) {
            if (!value)
                return;

            if (name.equals("SIMPLEAPP_Exit")){
                    stop();
                }else if (name.equals("SIMPLEAPP_CameraPos")){
                    if (cam != null){
                        Vector3f loc = cam.getLocation();
                        Quaternion rot = cam.getRotation();
                        System.out.println("Camera Position: ("+
                                loc.x+", "+loc.y+", "+loc.z+")");
                        System.out.println("Camera Rotation: "+rot);
                        System.out.println("Camera Direction: "+cam.getDirection());
                    }
                }else if (name.equals("SIMPLEAPP_Memory")){
                    BufferUtils.printCurrentDirectMemory(null);
                }
        }
    }

    @Override
    public void start(){
        // set some default settings in-case
        // settings dialog is not shown
        if (settings == null)
            setSettings(new AppSettings(true));

        // show settings dialog
        if (showSettings){
        	// TODO erreur ici apres l'installation du nightly build d'avril, � voir si �a marche.
            if (!JmeSystem.showSettingsDialog(settings, showSettings))
                return;
        }

        super.start();
    }

    public AzertyFlyByCamera getFlyByCamera() {
        return flyCam;
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public boolean isShowSettings() {
        return showSettings;
    }

    public void setShowSettings(boolean showSettings) {
        this.showSettings = showSettings;
    }

    private void initTexts(){
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        fpsText = new BitmapText(guiFont, false);
        fpsText.setLocalTranslation(0, fpsText.getLineHeight(), 0);
        fpsText.setText("Frames per second");
        guiNode.attachChild(fpsText);
        
        debugger = new MyDebugger(0, settings.getHeight(), assetManager.loadFont("Interface/Fonts/Console.fnt"));
        guiNode.attachChild(debugger.getNode());
    }
    
    public void loadStatsView(){
        statsView = new StatsView("Statistics View", assetManager, renderer.getStatistics());
//         move it up so it appears above fps text
        statsView.setLocalTranslation(0, fpsText.getLineHeight(), 0);
        guiNode.attachChild(statsView);
    }

    @Override
    public void initialize(){
        bulletAppState=new BulletAppState();
        bulletAppState.startPhysics();

        super.initialize();

        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        initTexts();
        loadStatsView();
        viewPort.attachScene(rootNode);
        guiViewPort.attachScene(guiNode);

        if (inputManager != null){
            flyCam = new AzertyFlyByCamera(cam);
            flyCam.setMoveSpeed(1f);
            flyCam.registerWithInput(inputManager);

            if (context.getType() == Type.Display)
                inputManager.addMapping("SIMPLEAPP_Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
            
            inputManager.addMapping("SIMPLEAPP_CameraPos", new KeyTrigger(KeyInput.KEY_C));
            inputManager.addMapping("SIMPLEAPP_Memory", new KeyTrigger(KeyInput.KEY_M));
            inputManager.addListener(actionListener, "SIMPLEAPP_Exit",
                                     "SIMPLEAPP_CameraPos", "SIMPLEAPP_Memory");
        }

        // call user code
        simpleInitApp();
        stateManager.attach(bulletAppState);
        getPhysicsSpace().addTickListener(this);
    }

    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused)
            return;
        
        float tpf = timer.getTimePerFrame() * speed;

        secondCounter += timer.getTimePerFrame();
        int fps = (int) timer.getFrameRate();
        if (secondCounter >= 1.0f){
            fpsText.setText("Frames per second: "+fps);
            secondCounter = 0.0f;
        }

        // update states
        stateManager.update(tpf);

        // simple update and root node
        debugger.reset();
        simpleUpdate(tpf);
        debugger.getNode();
        
        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        stateManager.render(renderManager);
        renderManager.render(tpf, true);
        simpleRender(renderManager);
        stateManager.postRender();
    }

    public abstract void simpleInitApp();

    public void simpleUpdate(float tpf){
    }

    public void simpleRender(RenderManager rm){
    }

    public void simplePhysicsUpdate(float tpf){
    }

    @Override
	public void physicsTick(PhysicsSpace space, float f) {
        simplePhysicsUpdate(f);
    }

    public PhysicsSpace getPhysicsSpace(){
        return bulletAppState.getPhysicsSpace();
    }

	@Override
	public void prePhysicsTick(PhysicsSpace arg0, float arg1) {
	}

	public abstract AppSettings getDefaultSetting();

	public void toggleToFullscreen() {
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode[] modes = device.getDisplayModes();
		int i = 0; // note: there are usually several, let's pick the first
		settings.setResolution(modes[i].getWidth(), modes[i].getHeight());
		settings.setFrequency(modes[i].getRefreshRate());
		settings.setBitsPerPixel(modes[i].getBitDepth());
		settings.setFullscreen(device.isFullScreenSupported());
		appInstance.setSettings(settings);
		appInstance.restart(); // restart the context to apply changes
	}

	public void resetSettings() {
		appInstance.settings.clear();
		try {
			appInstance.settings.save("openrts.example");
		} catch (BackingStoreException e) {
			throw new TechnicalException(e);
		}
	}

	public static void main(OpenRTSApplication app) {
		appInstance = app;
		Logger.getLogger("").setLevel(Level.INFO);
		LogUtil.init();
		LogUtil.logger.info("seed : " + MyRandom.SEED);

		appInstance.start();

	}

}
