package view.jme;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;

import controller.battlefield.BattlefieldGUIController;

public class LemurHUD {
    private final Node guiNode;
    private final Application app;
    private final BattlefieldGUIController controller;

    private Container bottomBar;
    private Container mapPlaceholder;
    private Container centerTelemetry;
    private Container commandCard;
    private Container topRightBar;

    // Telemetry labels
    private Label unitNameLabel;
    private Label unitHealthLabel;
    private Label unitStateLabel;
    private Label unitOrderLabel;
    private Label unitHoldingLabel;

    public LemurHUD(Node guiNode, Application app, BattlefieldGUIController controller) {
        this.guiNode = guiNode;
        this.app = app;
        this.controller = controller;
        buildHUD();
    }

    private void buildHUD() {
        float screenWidth = app.getCamera().getWidth();
        float screenHeight = app.getCamera().getHeight();

        // 1. Bottom Bar
        float bottomHeight = screenHeight * 0.22f;
        bottomBar = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.Even));
        bottomBar.setPreferredSize(new Vector3f(screenWidth - 20, bottomHeight, 0));
        bottomBar.setLocalTranslation(10, bottomHeight + 10, 0);

        // A. Left Column - Minimap Placeholder
        mapPlaceholder = new Container(new BorderLayout());
        mapPlaceholder.setPreferredSize(new Vector3f(screenWidth * 0.24f, bottomHeight - 20, 0));
        
        // Add "Select All" button inside Left Column
        Button selectAllBtn = createStyledButton("SELECT ALL");
        selectAllBtn.addClickCommands(cmd -> controller.selectAll());
        mapPlaceholder.addChild(selectAllBtn, BorderLayout.Position.North);
        
        // Create an empty sub-container for the actual viewport overlay coordinates
        Container mapOverlay = new Container();
        mapOverlay.setName("map"); // match element name
        mapPlaceholder.addChild(mapOverlay, BorderLayout.Position.Center);

        // B. Center Column - Telemetry
        centerTelemetry = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        centerTelemetry.setPreferredSize(new Vector3f(screenWidth * 0.48f, bottomHeight - 20, 0));

        unitNameLabel = centerTelemetry.addChild(new Label("", new ElementId("title")));
        unitNameLabel.setColor(new ColorRGBA(0.24f, 0.97f, 1.0f, 1f));
        unitHealthLabel = centerTelemetry.addChild(new Label(""));
        unitHealthLabel.setColor(ColorRGBA.White);
        unitStateLabel = centerTelemetry.addChild(new Label(""));
        unitStateLabel.setColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 1f));
        unitOrderLabel = centerTelemetry.addChild(new Label(""));
        unitOrderLabel.setColor(ColorRGBA.White);
        unitHoldingLabel = centerTelemetry.addChild(new Label(""));
        unitHoldingLabel.setColor(new ColorRGBA(0.53f, 0.53f, 1.0f, 1f));

        // C. Right Column - Command Card (2x2 grid)
        commandCard = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even));
        commandCard.setPreferredSize(new Vector3f(screenWidth * 0.24f, bottomHeight - 20, 0));

        // Row 1
        Container row1 = commandCard.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Even, FillMode.Even)));
        Button btnAttack = row1.addChild(createStyledButton("ATTACK (A)"));
        btnAttack.addClickCommands(cmd -> controller.orderAttack());
        Button btnHold = row1.addChild(createStyledButton("HOLD (H)"));
        btnHold.addClickCommands(cmd -> controller.orderHold());

        // Row 2
        Container row2 = commandCard.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Even, FillMode.Even)));
        Button btnStop = row2.addChild(createStyledButton("STOP"));
        btnStop.addClickCommands(cmd -> controller.orderStop());
        Button btnBoost = row2.addChild(createStyledButton("BOOST (B)"));
        btnBoost.addClickCommands(cmd -> controller.orderSpeedBoost());

        bottomBar.addChild(mapPlaceholder);
        bottomBar.addChild(centerTelemetry);
        bottomBar.addChild(commandCard);

        // 2. Top-Right Telemetry Bar
        topRightBar = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        topRightBar.setPreferredSize(new Vector3f(260, 140, 0));
        topRightBar.setLocalTranslation(screenWidth - 270, screenHeight - 10, 0);

        Label speedLabel = topRightBar.addChild(new Label("SYSTEM ONLINE (1.0x)"));
        speedLabel.setColor(new ColorRGBA(0.24f, 0.97f, 1.0f, 1f));
        Label factionLabel = topRightBar.addChild(new Label("Faction: Allied Forces"));
        factionLabel.setColor(ColorRGBA.White);
        Label telemetryLabel = topRightBar.addChild(new Label("Telemetry: Realtime"));
        telemetryLabel.setColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 1f));
        Label hotkeyLabel = topRightBar.addChild(new Label("Hotkeys: A (Attack), H (Hold)"));
        hotkeyLabel.setColor(new ColorRGBA(0.6f, 0.6f, 1.0f, 1f));
        Label footerLabel = topRightBar.addChild(new Label("OpenRTS Lemur Edition"));
        footerLabel.setColor(new ColorRGBA(0.4f, 0.4f, 1.0f, 1f));
    }

    private Button createStyledButton(String label) {
        Button btn = new Button(label);
        btn.setColor(new ColorRGBA(0.24f, 0.97f, 1.0f, 1f));
        btn.setTextHAlignment(HAlignment.Center);
        btn.setTextVAlignment(VAlignment.Center);
        
        // Hover highlights
        btn.addCommands(Button.ButtonAction.HighlightOn, cmd -> {
            btn.setColor(ColorRGBA.White);
        });
        btn.addCommands(Button.ButtonAction.HighlightOff, cmd -> {
            btn.setColor(new ColorRGBA(0.24f, 0.97f, 1.0f, 1f));
        });
        return btn;
    }

    public void show() {
        guiNode.attachChild(bottomBar);
        guiNode.attachChild(topRightBar);
    }

    public void hide() {
        bottomBar.removeFromParent();
        topRightBar.removeFromParent();
    }

    public void updateSelection(String name, String health, String state, String order, String holding, boolean showDetails) {
        if (showDetails) {
            unitNameLabel.setText(name);
            unitHealthLabel.setText(health);
            unitStateLabel.setText(state);
            unitOrderLabel.setText(order);
            unitHoldingLabel.setText(holding);
            centerTelemetry.setCullHint(com.jme3.scene.Spatial.CullHint.Inherit);
        } else {
            centerTelemetry.setCullHint(com.jme3.scene.Spatial.CullHint.Always);
        }
    }

    public Container getMapOverlayContainer() {
        return mapPlaceholder;
    }
}
