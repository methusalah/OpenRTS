# OpenRTS 3D Game & Engine

OpenRTS is a modern, modular, and high-performance **3D Real-Time Strategy (RTS) Game Engine** built in Java using **jMonkeyEngine 3** and **Simsilica Lemur**. 

Designed to support versatile RTS design paradigms, OpenRTS combines a robust programmatic UI, secondary mini-map rendering viewports, fully scripted campaign engines, modernized joint skeletal animation performers, save/load state serialization, and custom physics controls.

---

## ✨ Key Modernized Features

### 1. Transparent Slate HUD (Glassmorphism)
*   **Simsilica Lemur GUI**: Complete migration from legacy XML Nifty GUI layouts to a programmatic, hardware-accelerated **Lemur** interface.
*   **Dynamic Telemetry**: Features real-time multi-selection breakdown indicators (e.g. `3 Marines, 1 Tank`) and cumulative health bars showing selection ratios (e.g. `240 / 300 (80%)`).
*   **Command Card (2x2)**: Interactive, neon action controls featuring **ATTACK (A)**, **HOLD (H)**, **STOP**, and **BOOST (B)** with smooth hover effects.

### 2. Auto-Scaling Mini-Map Viewport
*   **Parallel Projection**: Embeds a dedicated, orthographic top-down camera centered over the terrain's midpoint, mapping directly to a secondary `minimapViewPort`.
*   **Screen-Space Tracking**: Automatically tracks the Lemur screen boundaries to update viewport boundaries dynamically on window resizes.

### 3. Fully Scripted Campaign & Trigger-Action Engine
*   **Sequential Progression**: Campaign runs load sequentially from `campaign.json`, managed by the progression tracking state machine.
*   **Modular Trigger Conditions**: Run dynamic tick evaluations:
    *   `TimeTrigger`: Timed event occurrences.
    *   `ZoneTrigger`: Coordinate boundary area check (checks unit intersection in a 2D radius).
    *   `EliminationTrigger`: Evaluates complete destruction of target faction forces.
*   **Declarative Script Actions**: Execute complex events:
    *   `SpawnWaveAction`: Spawns units procedurally snapping ground contours and ordering movement.
    *   `ShowDialogueAction`: Publishes cinematic speech dialogues.
    *   `AddObjectiveAction`/`CompleteObjectiveAction`: Manages HUD checklists.
*   **Cinematic Dialog & Objectives UI**: A sleek glassmorphic narrative speech container (cyan tag headers, white body text, skip skip skips) and checklist widget tracking primary and bonus objectives.
*   **Transition Screens**: Pop up gorgeous, fullscreen glassmorphic **VICTORY** and **MISSION FAILED** screens linking to next level or map retries.

### 4. Advanced AnimComposer & Armature Skinning
*   **AnimComposer Upgrades**: Integrates JME3 `AnimComposer` controls with fallback support for legacy `AnimControl` channels.
*   **Armature Skinning**: Procedural bone orientation (e.g. turret tracking) queries modern `SkinningControl` armature matrices and local `Joint` transformations.

### 5. Unit Speed Boost Ability
*   **Interactive Multipliers**: Activate a **1.8x** speed increase for **6 seconds** using the **B** key or Lemur Command button.

### 6. Modernized Java 21 Toolchain & Testing
*   **JDK 21 & Gradle Toolchains**: Compiles dynamically under Java 21 using the Foojay toolchain resolver.
*   **100% Test Coverage**: Validates game math and campaign script evaluations using **JUnit 5 (Jupiter)** and **Mockito** mocks.

---

## 🎮 Keyboard Controls & Mappings

Explore game modes and control battlefield forces instantly:

*   **F1**: Switch to Battlefield Game Mode (Isometric View)
*   **F2**: Switch to Terrain Editor Mode
*   **F3**: Switch to First-Person/Ground Editor Mode
*   **Space**: Pause / Resume Gameplay
*   **C**: **Start Campaign Mode** (Instantly loads *Mission 1: The Outpost* and starts the scenario script!)
*   **A**: Command selected forces to Move-Attack
*   **H**: Command selected forces to Hold Position
*   **B**: Activate Speed Boost Ability (1.8x speed multiplier for 6s)

---

## 🚀 Getting Started

Ensure you have a modern JDK installed (JDK 21 is automatically resolved and downloaded by the build tool).

### 1. Compile the Project
```bash
./gradlew compileJava
```

### 2. Run the Unit Tests
```bash
./gradlew test
```

### 3. Launch the Application
```bash
./gradlew run
```

---

## 📂 Project Architecture

```
OpenRTS
├── assets/                  # Root game assets (JSON campaigns, models)
├── core/
│   ├── assets/              # Core textures, shaders, nifty XMLs, map data (.btf)
│   ├── src/
│   │   ├── app/             # Application entry points (MainRTS, Example launchers)
│   │   ├── controller/      # User inputs, camera controllers, Lemur UI binders
│   │   ├── event/           # Guava decoupled event buses
│   │   ├── model/           # Battlefield logic, grid data, and Campaign scripts
│   │   └── view/            # JME3 rendering pipes, Skeletal perform, Lemur overlays
│   └── test/java/           # JUnit 5 & Mockito automated verification suite
├── example/                 # Example module runs
└── build.gradle             # Master build configuration & dependencies
```

---

## 📜 Licensing

OpenRTS is released under the [MIT License](LICENSE), which applies to all components and subsequent contributions.
