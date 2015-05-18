package view;

import java.awt.event.ActionListener;

import model.ModelManager;
import view.mapDrawing.EditorRenderer;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

public class EditorView extends MapView implements ActionListener {

	// Renderers
	public EditorRenderer editorRend;

	public EditorView(Node rootNode, Node gui, PhysicsSpace physicsSpace, AssetManager am, ViewPort vp) {
		super(rootNode, gui, physicsSpace, am, vp);
		editorRend = new EditorRenderer(this, materialManager);
		rootNode.attachChild(editorRend.mainNode);
		ModelManager.toolManager.addListener(editorRend);
	}

	@Override
	public void reset() {
		super.reset();
		rootNode.detachChild(editorRend.mainNode);
		ModelManager.toolManager.removeListener(editorRend);

		editorRend = new EditorRenderer(this, materialManager);
		rootNode.attachChild(editorRend.mainNode);
		ModelManager.toolManager.addListener(editorRend);

	}

}
