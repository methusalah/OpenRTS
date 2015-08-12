/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package openrts.server.gui;

import tonegod.gui.controls.buttons.ButtonAdapter
import tonegod.gui.controls.buttons.ButtonAdapter
import tonegod.gui.controls.form.Form
import tonegod.gui.controls.text.Label
import tonegod.gui.controls.text.Password
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
public abstract class ServerStartBox extends Window {
	private ButtonAdapter btnLogin, btnCancel;
	private Element responseMsg;
	private Label lblUserName, lblPassword;
	private TextField userName;
	private Password password;
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

		lblUserName = new Label(screen, UID + ":Lbl:UserName",new Vector2f(indents.y,(Float) indents.x+controlSpacing),new Vector2f((Float) (getWidth()/3)-indents.y-indents.z,controlSize));
		lblUserName.setTextAlign(BitmapFont.Align.Right);
		lblUserName.setText("User ID:");
		this.addWindowContent(lblUserName);

		userName = new TextField(screen, UID + ":userName",new Vector2f((Float)getWidth()/3,(Float)indents.x+controlSpacing),new Vector2f((Float)getWidth()-(getWidth()/3)-indents.z,controlSize));
		this.addWindowContent(userName);
		form.addFormElement(userName);

		lblPassword = new Label(screen, UID + ":Lbl:Password",new Vector2f(indents.y,(Float)indents.x+controlSize+(controlSpacing*2)),new Vector2f((Float)(getWidth()/3)-indents.y-indents.z,controlSize));
		lblPassword.setTextAlign(BitmapFont.Align.Right);
		lblPassword.setText("Password:");
		this.addWindowContent(lblPassword);

		password = new Password(screen, UID + "password",new Vector2f((Float)getWidth()/3,(Float)indents.x+controlSize+(controlSpacing*2)),new Vector2f((Float)getWidth()-(getWidth()/3)-indents.z,(Float)controlSize));
		this.addWindowContent(password);
		form.addFormElement(password);

		responseMsg = new Element(screen,UID+":resonse",new Vector2f(indents.y,(Float)password.getHeight()+indents.x+controlSize+(controlSpacing*3)),
				new Vector2f((Float)contentArea.getWidth()-indents.y-indents.z,(Float)contentArea.getHeight()-(password.getHeight()+indents.x+controlSize+(controlSpacing*3))-getHeight()-screen.getStyle("Button").getVector2f("defaultSize").y-indents.w),new Vector4f(0,0,0,0),null);
		responseMsg.setIsResizable(false);
		responseMsg.setIgnoreMouse(true);
		responseMsg.setDocking(Docking.NW);
		responseMsg.setScaleEW(true);
		responseMsg.setScaleNS(true);
		responseMsg.setFontColor(ColorRGBA.Red);
		responseMsg.setTextAlign(BitmapFont.Align.Center);
		responseMsg.setFontSize(screen.getStyle("Label").getFloat("fontSize"));

		addWindowContent(responseMsg);

		btnLogin = new ButtonAdapter(screen,  UID + ":btnOk",
				new Vector2f((Float)contentArea.getWidth()-screen.getStyle("Button").getVector2f("defaultSize").x-indents.z,(Float)contentArea.getHeight()-screen.getStyle("Button").getVector2f("defaultSize").y-indents.w)
				) {
					@Override
					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
						onButtonStartPressed(evt, toggled);
					}
				};
		btnLogin.setText("Start");
		btnLogin.setDocking(Docking.SE);
		addWindowContent(btnLogin);
		form.addFormElement(btnLogin);

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

		this.setWindowTitle("Login");
	}

	public void setMsg(String text) {
		responseMsg.setText(text);
	}

	public TextField getUserName() {
		return userName;
	}

	public String getTextUserName() {
		return this.userName.getText();
	}

	public void setTextUserName(String text) {
		this.userName.setText(text);
	}

	public Password getPassword() {
		return this.password;
	}

	public String getTextPassword() {
		return this.password.getText();
	}

	public void setTextPassword(String text) {
		this.password.setText(text);
	}

	public void setButtonLoginText(String text) {
		btnLogin.setText(text);
	}

	public abstract void onButtonStartPressed(MouseButtonEvent evt, boolean toggled);

	public void setButtonCancelText(String text) {
		btnCancel.setText(text);
	}

	public abstract void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled);

	public void setToolTipLoginInput(String tip) {
		userName.setToolTipText(tip);
	}
	public void setToolTipPasswordInput(String tip) {
		password.setToolTipText(tip);
	}
	public void setToolTipLoginButton(String tip) {
		this.btnLogin.setToolTipText(tip);
	}
	public void setToolTipCancelButton(String tip) {
		this.btnCancel.setToolTipText(tip);
	}
}
