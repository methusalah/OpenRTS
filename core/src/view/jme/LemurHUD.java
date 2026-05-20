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

    // Campaign UI elements
    private Container objectivesContainer;
    private Container dialogueContainer;
    private Label dialogueSpeaker;
    private Label dialogueText;
    private Container endScreenOverlay;

    private java.util.Map<String, Boolean> objectives = new java.util.LinkedHashMap<>();

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

        // 3. Campaign Objectives Container (Top-Left)
        objectivesContainer = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        objectivesContainer.setPreferredSize(new Vector3f(300, 150, 0));
        objectivesContainer.setLocalTranslation(10, screenHeight - 10, 0);
        
        Label objTitle = objectivesContainer.addChild(new Label("OBJECTIVES", new ElementId("title")));
        objTitle.setColor(new ColorRGBA(0.24f, 0.97f, 1.0f, 1f));

        // 4. Narrative Dialogue Panel (Bottom-Middle, above bottom bar)
        dialogueContainer = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        dialogueContainer.setPreferredSize(new Vector3f(screenWidth - 100, 120, 0));
        dialogueContainer.setLocalTranslation(50, bottomHeight + 140, 0);

        dialogueSpeaker = dialogueContainer.addChild(new Label("", new ElementId("title")));
        dialogueSpeaker.setColor(new ColorRGBA(0.24f, 0.97f, 1.0f, 1f));
        dialogueText = dialogueContainer.addChild(new Label(""));
        dialogueText.setColor(ColorRGBA.White);

        Button dismissBtn = dialogueContainer.addChild(createStyledButton("ACKNOWLEDGE"));
        dismissBtn.addClickCommands(cmd -> hideDialogue());

        // Initially hide dialogue
        dialogueContainer.setCullHint(com.jme3.scene.Spatial.CullHint.Always);
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
        guiNode.attachChild(objectivesContainer);
        guiNode.attachChild(dialogueContainer);
    }

    public void hide() {
        bottomBar.removeFromParent();
        topRightBar.removeFromParent();
        objectivesContainer.removeFromParent();
        dialogueContainer.removeFromParent();
        if (endScreenOverlay != null) {
            endScreenOverlay.removeFromParent();
            endScreenOverlay = null;
        }
    }

    public void showDialogue(String speaker, String text) {
        dialogueSpeaker.setText(speaker.toUpperCase());
        dialogueText.setText(text);
        dialogueContainer.setCullHint(com.jme3.scene.Spatial.CullHint.Inherit);
    }

    public void hideDialogue() {
        dialogueContainer.setCullHint(com.jme3.scene.Spatial.CullHint.Always);
    }

    public void updateObjective(String objective, boolean active, boolean completed) {
        if (!active) {
            objectives.remove(objective);
        } else {
            objectives.put(objective, completed);
        }
        refreshObjectivesUI();
    }

    private void refreshObjectivesUI() {
        objectivesContainer.clearChildren();
        Label objTitle = objectivesContainer.addChild(new Label("OBJECTIVES", new ElementId("title")));
        objTitle.setColor(new ColorRGBA(0.24f, 0.97f, 1.0f, 1f));

        for (java.util.Map.Entry<String, Boolean> entry : objectives.entrySet()) {
            String checkMark = entry.getValue() ? "[x] " : "[ ] ";
            Label objLabel = objectivesContainer.addChild(new Label(checkMark + entry.getKey()));
            if (entry.getValue()) {
                objLabel.setColor(new ColorRGBA(0.4f, 1.0f, 0.4f, 1f));
            } else {
                objLabel.setColor(ColorRGBA.White);
            }
        }
    }

    public void showEndScreen(boolean victory) {
        float screenWidth = app.getCamera().getWidth();
        float screenHeight = app.getCamera().getHeight();

        if (endScreenOverlay != null) {
            endScreenOverlay.removeFromParent();
        }

        endScreenOverlay = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even));
        endScreenOverlay.setPreferredSize(new Vector3f(400, 200, 0));
        endScreenOverlay.setLocalTranslation(screenWidth / 2f - 200f, screenHeight / 2f + 100f, 10f);

        Label title = endScreenOverlay.addChild(new Label(victory ? "VICTORY" : "MISSION FAILED", new ElementId("title")));
        title.setColor(victory ? new ColorRGBA(0.24f, 1.0f, 0.4f, 1f) : new ColorRGBA(1.0f, 0.24f, 0.24f, 1f));
        title.setTextHAlignment(HAlignment.Center);

        if (victory) {
            Button nextBtn = endScreenOverlay.addChild(createStyledButton("NEXT MISSION"));
            nextBtn.addClickCommands(cmd -> {
                endScreenOverlay.removeFromParent();
                endScreenOverlay = null;
                controller.nextCampaignMission();
            });
        } else {
            Button retryBtn = endScreenOverlay.addChild(createStyledButton("RETRY MISSION"));
            retryBtn.addClickCommands(cmd -> {
                endScreenOverlay.removeFromParent();
                endScreenOverlay = null;
                controller.restartCampaignMission();
            });
        }

        guiNode.attachChild(endScreenOverlay);
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
