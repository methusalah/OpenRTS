package view;

import openrts.guice.annotation.AssetManagerRef;
import openrts.guice.annotation.GuiNodeRef;
import openrts.guice.annotation.RootNodeRef;
import openrts.guice.annotation.ViewPortRef;
import view.mapDrawing.EditorRenderer;

import com.google.inject.Inject;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

public class EditorView extends MapView {

	// Renderers
	public EditorRenderer editorRend;

	@Inject
	public EditorView(@RootNodeRef Node rootNode, @GuiNodeRef Node gui, PhysicsSpace physicsSpace, @AssetManagerRef AssetManager am, @ViewPortRef ViewPort vp) {
		super(rootNode, gui, physicsSpace, am, vp);
		editorRend = new EditorRenderer(this, materialManager);
	}

	@Override
	public void reset() {
		super.reset();

		if(editorRend != null) {
			rootNode.detachChild(editorRend.mainNode);
		}

		editorRend = new EditorRenderer(this, materialManager);
		rootNode.attachChild(editorRend.mainNode);
	}

}
