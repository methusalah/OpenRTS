package model.campaign;

import event.DialogueEvent;
import event.EventManager;
import event.ObjectiveEvent;
import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import model.battlefield.Battlefield;
import model.battlefield.army.components.Unit;
import model.battlefield.warfare.Faction;

public abstract class ScriptAction {
    public abstract void execute(Battlefield battlefield);

    public static class SpawnWaveAction extends ScriptAction {
        private final String unitBuilderID;
        private final Point2D spawnPoint;
        private final int count;
        private final int factionIndex;
        private final Point2D targetPoint;

        public SpawnWaveAction(String unitBuilderID, Point2D spawnPoint, int count, int factionIndex, Point2D targetPoint) {
            this.unitBuilderID = unitBuilderID;
            this.spawnPoint = spawnPoint;
            this.count = count;
            this.factionIndex = factionIndex;
            this.targetPoint = targetPoint;
        }

        @Override
        public void execute(Battlefield battlefield) {
            if (battlefield.getEngagement().getFactions().size() <= factionIndex) return;
            Faction faction = battlefield.getEngagement().getFactions().get(factionIndex);
            for (int i = 0; i < count; i++) {
                double offsetX = (Math.random() - 0.5) * 1.5;
                double offsetY = (Math.random() - 0.5) * 1.5;
                Point3D pos = new Point3D(spawnPoint.x + offsetX, spawnPoint.y + offsetY, 0.5);
                
                Unit u = model.builders.entity.definitions.BuilderManager.getUnitBuilder(unitBuilderID)
                        .build(faction, pos, 0.0);
                u.drawOnBattlefield();
                
                if (targetPoint != null) {
                    model.battlefield.army.motion.pathfinding.FlowField ff = 
                            new model.battlefield.army.motion.pathfinding.FlowField(battlefield.getMap(), targetPoint);
                    u.getMover().setDestination(ff);
                    u.ai.orderMoveAttack();
                }
            }
        }

        public String getUnitBuilderID() {
            return unitBuilderID;
        }

        public Point2D getSpawnPoint() {
            return spawnPoint;
        }

        public int getCount() {
            return count;
        }

        public int getFactionIndex() {
            return factionIndex;
        }

        public Point2D getTargetPoint() {
            return targetPoint;
        }
    }

    public static class ShowDialogueAction extends ScriptAction {
        private final String speaker;
        private final String text;

        public ShowDialogueAction(String speaker, String text) {
            this.speaker = speaker;
            this.text = text;
        }

        @Override
        public void execute(Battlefield battlefield) {
            EventManager.post(new DialogueEvent(speaker, text));
        }

        public String getSpeaker() {
            return speaker;
        }

        public String getText() {
            return text;
        }
    }

    public static class AddObjectiveAction extends ScriptAction {
        private final String objective;

        public AddObjectiveAction(String objective) {
            this.objective = objective;
        }

        @Override
        public void execute(Battlefield battlefield) {
            EventManager.post(new ObjectiveEvent(objective, true, false));
        }

        public String getObjective() {
            return objective;
        }
    }

    public static class CompleteObjectiveAction extends ScriptAction {
        private final String objective;

        public CompleteObjectiveAction(String objective) {
            this.objective = objective;
        }

        @Override
        public void execute(Battlefield battlefield) {
            EventManager.post(new ObjectiveEvent(objective, true, true));
        }

        public String getObjective() {
            return objective;
        }
    }
}
