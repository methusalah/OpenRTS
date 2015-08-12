/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package openrts.server.gui;

import tonegod.gui.controls.buttons.ButtonAdapter
import groovy.transform.CompileStatic
import tonegod.gui.controls.buttons.ButtonAdapter
import tonegod.gui.controls.form.Form
import tonegod.gui.controls.text.Label
import tonegod.gui.controls.text.TextField
import tonegod.gui.controls.windows.Window
import tonegod.gui.core.Element
import tonegod.gui.core.ElementManager
import tonegod.gui.core.Element.Docking
import tonegod.gui.core.utils.UIDUtil

import com.jme3.font.BitmapFont
import com.jme3.input.event.MouseButtonEvent
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector2f
import com.jme3.math.Vector4f

/**
 *
 * @author t0neg0d
 */
@CompileStatic
public abstract class ServerStartBox extends Window {
	private ButtonAdapter btnStart, btnCancel;
	private Element responseMsg;
	private Label lblGameName, lblVersion;
	private TextField gameName;
	private TextField version;
	private Form form;

	public ServerStartBox(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO,
		screen.getStyle("Window").getVector2f("defaultSize"),
		screen.getStyle("Window").getVector4f("resizeBorders"),
		screen.getStyle("Window").getString("defaultImg")
		);
	}

	public ServerStartBox(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position,
		screen.getStyle("Window").getVector2f("defaultSize"),
		screen.getStyle("Window").getVector4f("resizeBorders"),
		screen.getStyle("Window").getString("defaultImg")
		);
	}

	public ServerStartBox(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions,
		screen.getStyle("Window").getVector4f("resizeBorders"),
		screen.getStyle("Window").getString("defaultImg")
		);
	}

	public ServerStartBox(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	public ServerStartBox(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position,
		screen.getStyle("Window").getVector2f("defaultSize"),
		screen.getStyle("Window").getVector4f("resizeBorders"),
		screen.getStyle("Window").getString("defaultImg")
		);
	}

	public ServerStartBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions,
		screen.getStyle("Window").getVector4f("resizeBorders"),
		screen.getStyle("Window").getString("defaultImg")
		);
	}

	public ServerStartBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		form = new Form(screen);

		Vector4f indents = screen.getStyle("Window").getVector4f("contentIndents");
		float controlSize = screen.getStyle("Common").getFloat("defaultControlSize");
		float controlSpacing = screen.getStyle("Common").getFloat("defaultControlSpacing");

		lblGameName = new Label(screen, UID + ":Lbl:UserName",new Vector2f(indents.y,(Float) indents.x+controlSpacing),new Vector2f((Float) (getWidth()/3)-indents.y-indents.z,controlSize));
		lblGameName.setTextAlign(BitmapFont.Align.Right);
		lblGameName.setText("Game name:");
		this.addWindowContent(lblGameName);

		gameName = new TextField(screen, UID + ":gameName",new Vector2f((Float)getWidth()/3,(Float)indents.x+controlSpacing),new Vector2f((Float)getWidth()-(getWidth()/3)-indents.z,controlSize));
		this.addWindowContent(gameName);
		gameName.setText("OpenRTS Server");
		form.addFormElement(gameName);

		lblVersion = new Label(screen, UID + ":Lbl:Version",new Vector2f(indents.y,(Float)indents.x+controlSize+(controlSpacing*2)),new Vector2f((Float)(getWidth()/3)-indents.y-indents.z,controlSize));
		lblVersion.setTextAlign(BitmapFont.Align.Right);
		lblVersion.setText("version:");
		this.addWindowContent(lblVersion);

		version = new TextField(screen, UID + "version",new Vector2f((Float)getWidth()/3,(Float)indents.x+controlSize+(controlSpacing*2)),new Vector2f((Float)getWidth()-(getWidth()/3)-indents.z,(Float)controlSize));
		this.addWindowContent(version);
		version.setText("1");
		form.addFormElement(version);

		responseMsg = new Element(screen,UID+":resonse",new Vector2f(indents.y,(Float)version.getHeight()+indents.x+controlSize+(controlSpacing*3)),
				new Vector2f((Float)contentArea.getWidth()-indents.y-indents.z,(Float)contentArea.getHeight()-(version.getHeight()+indents.x+controlSize+(controlSpacing*3))-getHeight()-screen.getStyle("Button").getVector2f("defaultSize").y-indents.w),new Vector4f(0,0,0,0),null);
		responseMsg.setIsResizable(false);
		responseMsg.setIgnoreMouse(true);
		responseMsg.setDocking(Docking.NW);
		responseMsg.setScaleEW(true);
		responseMsg.setScaleNS(true);
		responseMsg.setFontColor(ColorRGBA.Red);
		responseMsg.setTextAlign(BitmapFont.Align.Center);
		responseMsg.setFontSize(screen.getStyle("Label").getFloat("fontSize"));

		addWindowContent(responseMsg);

		btnStart = new ButtonAdapter(screen,  UID + ":btnOk",
				new Vector2f((Float)contentArea.getWidth()-screen.getStyle("Button").getVector2f("defaultSize").x-indents.z,(Float)contentArea.getHeight()-screen.getStyle("Button").getVector2f("defaultSize").y-indents.w)
				) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						onButtonStartPressed(evt, toggled);
					}
				};
		btnStart.setText("Start");
		btnStart.setDocking(Docking.SE);
		addWindowContent(btnStart);
		form.addFormElement(btnStart);

		btnCancel = new ButtonAdapter(screen, UID + ":btnCancel",
				new Vector2f(
				indents.y,(Float)contentArea.getHeight()-screen.getStyle("Button").getVector2f("defaultSize").y-indents.w)) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						onButtonCancelPressed(evt, toggled);
					}
				};
		btnCancel.setText("Cancel");
		btnCancel.setDocking(Docking.SW);
		addWindowContent(btnCancel);
		form.addFormElement(btnCancel);

		addClippingLayer(this);

		this.setWindowTitle("Start the OpenRTS Server");
	}

	public void setMsg(String text) {
		responseMsg.setText(text);
	}

	public abstract void onButtonStartPressed(MouseButtonEvent evt, boolean toggled);


	public abstract void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled);

	public void setToolTipLoginInput(String tip) {
		gameName.setToolTipText(tip);
	}
	public void setToolTipPasswordInput(String tip) {
		version.setToolTipText(tip);
	}
	public void setToolTipLoginButton(String tip) {
		this.btnStart.setToolTipText(tip);
	}
	public void setToolTipCancelButton(String tip) {
		this.btnCancel.setToolTipText(tip);
	}
}
