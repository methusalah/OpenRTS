/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package openrts.app.example.states;

import openrts.app.example.MultiplayerGame;
import groovy.transform.CompileStatic
import tonegod.gui.core.Screen

import com.jme3.app.Application
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager

/**
 *
 * @author t0neg0d
 */
@CompileStatic
public abstract class AppStateCommon extends AbstractAppState {
	protected String displayName = "Display Name";
	protected boolean show = true;
	protected MultiplayerGame main;
	protected Screen screen;
	protected boolean init = false;

	public AppStateCommon(MultiplayerGame main) {
		this.main = main;
		this.screen = main.getScreen();
	}

	public abstract void reshape();

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		initState();
	}

	protected abstract void initState();

	@Override
	public void update(float tpf) {
		updateState(tpf);
	}

	public abstract void updateState(float tpf);

	@Override
	public void cleanup() {
		super.cleanup();
		cleanupState();
	}

	public abstract void cleanupState();

	public String getDisplayName() {
		return this.displayName;
	}
}
