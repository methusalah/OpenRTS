/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package openrts.app.example.states;

import com.jme3.math.Vector2f;
import java.util.ArrayList;
import java.util.List;
import tonegod.gui.controls.scrolling.ScrollPanel;
import tonegod.gui.controls.text.LabelElement;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.core.Element;
import tonegod.gui.core.Element.Borders;
import tonegod.gui.core.layouts.LayoutHelper;
import tonegod.gui.tests.Main;
import tonegod.gui.tests.controls.CollapsePanel;

/**
 *
 * @author t0neg0d
 */
public class TestState extends AppStateCommon {
	public static float contentIndent = 15;
	public static float padding = 5;
	List<CollapsePanel> ctrlPanels = new ArrayList();
	Panel panel;
	ScrollPanel scrollPanel;
	
	public TestState(Main main) {
		super(main);
		displayName = "Tests";
		show = false;
	}
	
	public void addCtrlPanel(CollapsePanel el) {
		ctrlPanels.add(el);
		scrollPanel.addScrollableContent(el);
		pack();
	}
	
	public void removeCtrlPanel(CollapsePanel el) {
		ctrlPanels.remove(el);
		scrollPanel.removeScrollableContent(el);
		pack();
	}
	
	public void pack() {
		float h = 0;
		for (CollapsePanel cp : ctrlPanels) {
			cp.setY(scrollPanel.getScrollableArea().getHeight()-cp.getHeight()-h);
			h += cp.getHeight();
		}
		scrollPanel.reshape();
		if (scrollPanel.getVerticalScrollDistance() > 0)
			scrollPanel.scrollToBottom();
		if (!ctrlPanels.isEmpty()) {
			for (CollapsePanel cp : ctrlPanels) {
				if (scrollPanel.getVerticalScrollDistance() > 0) {
					if (cp.getWidth() > scrollPanel.getScrollBoundsWidth()) {
						cp.setWidth(scrollPanel.getScrollBoundsWidth());
						cp.btnCollapse.setX(cp.btnCollapse.getX()-scrollPanel.getScrollSize());
					}
				} else {
					if (cp.getWidth() < scrollPanel.getScrollBoundsWidth()) {
						cp.setWidth(scrollPanel.getScrollBoundsWidth());
						cp.btnCollapse.setX(cp.btnCollapse.getX()+scrollPanel.getScrollSize());
					}
				}
			}
		}
	}
	
	public float getInnerWidth() {
		return scrollPanel.getScrollBounds().getWidth();
	}
	
	public LabelElement getLabel(String text, Vector2f position) {
		LabelElement te = new LabelElement(screen, position, 
			LayoutHelper.dimensions(200,20)) {
		};
		te.setDocking(Element.Docking.SW);
		te.setSizeToText(true);
		te.setText(text);
		return te;
	}

	@Override
	public void reshape() {
		if (panel != null) {
			panel.setPosition(screen.getWidth()-panel.getWidth(),0);
			panel.resize(screen.getWidth(), screen.getHeight(), Borders.SE);
		}
	}

	@Override
	protected void initState() {
		if (!init) {
			panel = new Panel(screen, Vector2f.ZERO,
				new Vector2f(
					main.getHarness().getHarnessPanel().getWidth(),
					main.getHarness().getHarnessPanel().getHeight()
				)
			);
			panel.setIsResizable(false);
			panel.setIsMovable(false);
			
			scrollPanel = new ScrollPanel(screen,
				LayoutHelper.absPosition(contentIndent, contentIndent),
				LayoutHelper.absPosition(
					panel.getWidth()-(contentIndent*2),
					panel.getHeight()-(contentIndent*2)
				)
			);
			panel.addChild(scrollPanel);
			
			scrollPanel.setPosition(contentIndent, contentIndent);
			panel.setPosition(screen.getWidth()-panel.getWidth(),0);
			screen.addElement(panel, true);
			
			init = true;
		}
		panel.show();
	}

	@Override
	public void updateState(float tpf) {
		
	}

	@Override
	public void cleanupState() {
		panel.hide();
	}
}
