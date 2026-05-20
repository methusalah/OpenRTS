package model.campaign;

import geometry.geom2d.Point2D;
import model.battlefield.Battlefield;
import model.battlefield.army.components.Unit;
import model.battlefield.warfare.Faction;

public abstract class TriggerCondition {
    public abstract boolean evaluate(double elapsedTime, Battlefield battlefield);

    public static class TimeTrigger extends TriggerCondition {
        private final double threshold;
        private double timeElapsed = 0;

        public TimeTrigger(double threshold) {
            this.threshold = threshold;
        }

        @Override
        public boolean evaluate(double elapsedTime, Battlefield battlefield) {
            timeElapsed += elapsedTime;
            return timeElapsed >= threshold;
        }

        public double getTimeElapsed() {
            return timeElapsed;
        }
    }

    public static class ZoneTrigger extends TriggerCondition {
        private final Point2D center;
        private final double radius;
        private final int factionIndex;

        public ZoneTrigger(Point2D center, double radius, int factionIndex) {
            this.center = center;
            this.radius = radius;
            this.factionIndex = factionIndex;
        }

        @Override
        public boolean evaluate(double elapsedTime, Battlefield battlefield) {
            if (battlefield.getEngagement().getFactions().size() <= factionIndex) {
                return false;
            }
            Faction f = battlefield.getEngagement().getFactions().get(factionIndex);
            for (Unit u : f.getUnits()) {
                if (!u.destroyed() && u.getCoord().getDistance(center) <= radius) {
                    return true;
                }
            }
            return false;
        }

        public Point2D getCenter() {
            return center;
        }

        public double getRadius() {
            return radius;
        }

        public int getFactionIndex() {
            return factionIndex;
        }
    }

    public static class EliminationTrigger extends TriggerCondition {
        private final int factionIndex;

        public EliminationTrigger(int factionIndex) {
            this.factionIndex = factionIndex;
        }

        @Override
        public boolean evaluate(double elapsedTime, Battlefield battlefield) {
            if (battlefield.getEngagement().getFactions().size() <= factionIndex) {
                return false;
            }
            Faction f = battlefield.getEngagement().getFactions().get(factionIndex);
            int activeUnitsCount = 0;
            for (Unit u : f.getUnits()) {
                if (!u.destroyed()) {
                    activeUnitsCount++;
                }
            }
            return activeUnitsCount == 0;
        }

        public int getFactionIndex() {
            return factionIndex;
        }
    }
}
