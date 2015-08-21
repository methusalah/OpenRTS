package view;

import openrts.guice.annotation.GuiNodeRef;
import openrts.guice.annotation.RootNodeRef;
import openrts.guice.annotation.ViewPortRef;
import view.mapDrawing.EditorRenderer;
import view.material.MaterialManager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

public class EditorView extends MapView {

	
	// Renderers
	public EditorRenderer editorRend;

	@Inject
	public EditorView(@RootNodeRef Node rootNode, @GuiNodeRef Node gui, PhysicsSpace physicsSpace, AssetManager am, @ViewPortRef ViewPort vp, MaterialManager mm, Injector injector) {
		super(rootNode, gui, physicsSpace, am, vp, mm, injector);
		editorRend = injector.getInstance(EditorRenderer.class);
	}

	@Override
	public void reset() {
		super.reset();

		if(editorRend != null) {
			rootNode.detachChild(editorRend.mainNode);
		}


		editorRend = injector.getInstance(EditorRenderer.class);
		rootNode.attachChild(editorRend.mainNode);
	}

}
