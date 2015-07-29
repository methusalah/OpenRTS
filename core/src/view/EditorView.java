package view;

import openrts.guice.annotation.AssetManagerRef;
import openrts.guice.annotation.GuiNodeRef;
import openrts.guice.annotation.RootNodeRef;
import openrts.guice.annotation.ViewPortRef;
import view.mapDrawing.EditorRenderer;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

public class EditorView extends MapView {

	// Renderers
	public EditorRenderer editorRend;

	private Injector injector;

	@Inject
	public EditorView(@RootNodeRef Node rootNode, @GuiNodeRef Node gui, PhysicsSpace physicsSpace, @AssetManagerRef AssetManager am, @ViewPortRef ViewPort vp,
			Injector injector) {
		super(rootNode, gui, physicsSpace, am, vp);
		this.injector = injector;
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
