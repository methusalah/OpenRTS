package model.campaign;

import geometry.geom2d.Point2D;
import model.battlefield.Battlefield;
import model.campaign.TriggerCondition.TimeTrigger;
import model.campaign.TriggerCondition.ZoneTrigger;
import model.campaign.ScriptAction.AddObjectiveAction;
import model.campaign.ScriptAction.CompleteObjectiveAction;
import model.campaign.ScriptAction.ShowDialogueAction;
import model.campaign.ScriptAction.SpawnWaveAction;

public class DemoMissionScript implements MissionScript {
    private TimeTrigger triggerStart = new TimeTrigger(0.5);
    private TimeTrigger triggerBeaconHint = new TimeTrigger(5.0);
    private TimeTrigger triggerWave1 = new TimeTrigger(30.0);
    private TimeTrigger triggerWave2 = new TimeTrigger(60.0);
    private TimeTrigger triggerTimeVictory = new TimeTrigger(90.0);
    
    private ZoneTrigger triggerBeaconReached = new ZoneTrigger(new Point2D(20, 20), 4.0, 0);

    private boolean startFired = false;
    private boolean beaconHintFired = false;
    private boolean wave1Fired = false;
    private boolean wave2Fired = false;
    private boolean beaconReachedFired = false;
    private boolean victoryFired = false;
    private boolean defeatFired = false;

    private String primaryObjective = "Survive the onslaught (90s)";
    private String secondaryObjective = "Locate rescue beacon";

    @Override
    public void initialize(Battlefield battlefield) {
        // Initial clean state
    }

    @Override
    public void update(double elapsedTime, Battlefield battlefield) {
        // 1. Initial Dialog
        if (!startFired && triggerStart.evaluate(elapsedTime, battlefield)) {
            startFired = true;
            new AddObjectiveAction(primaryObjective).execute(battlefield);
            new ShowDialogueAction("Advisor", "Commander! Hostile alien signatures detected heading towards our outpost! Hold the line at all costs!").execute(battlefield);
        }

        // 2. Secondary Objective Hint
        if (!beaconHintFired && triggerBeaconHint.evaluate(elapsedTime, battlefield)) {
            beaconHintFired = true;
            new AddObjectiveAction(secondaryObjective).execute(battlefield);
            new ShowDialogueAction("Advisor", "We've lost contact with our scouts. Move a unit to coordinates (20, 20) to check for survivors.").execute(battlefield);
        }

        // 3. Enemy Wave 1
        if (!wave1Fired && triggerWave1.evaluate(elapsedTime, battlefield)) {
            wave1Fired = true;
            new ShowDialogueAction("Advisor", "WARNING: First hostile alien wave spawning in the north! Prep defenses!").execute(battlefield);
            new SpawnWaveAction("SmallAlien", new Point2D(10, 10), 3, 1, new Point2D(25, 20)).execute(battlefield);
        }

        // 4. Enemy Wave 2
        if (!wave2Fired && triggerWave2.evaluate(elapsedTime, battlefield)) {
            wave2Fired = true;
            new ShowDialogueAction("Advisor", "WARNING: Mass heavy alien forces detected approaching from the north-west!").execute(battlefield);
            new SpawnWaveAction("BigAlien", new Point2D(5, 30), 2, 1, new Point2D(25, 20)).execute(battlefield);
        }

        // 5. Zone Trigger: Rescue Beacon
        if (!beaconReachedFired && triggerBeaconReached.evaluate(elapsedTime, battlefield)) {
            beaconReachedFired = true;
            new CompleteObjectiveAction(secondaryObjective).execute(battlefield);
            new ShowDialogueAction("Marine Captain", "Thank goodness! Reinforcements have arrived. Let's push these creatures back!").execute(battlefield);
            new SpawnWaveAction("Marine", new Point2D(20, 20), 2, 0, new Point2D(25, 20)).execute(battlefield);
        }

        // 6. Defeat check
        if (!defeatFired && startFired) {
            if (battlefield.getEngagement().getFactions().size() > 0) {
                model.battlefield.warfare.Faction playerFaction = battlefield.getEngagement().getFactions().get(0);
                int activeCount = 0;
                for (model.battlefield.army.components.Unit u : playerFaction.getUnits()) {
                    if (!u.destroyed()) {
                        activeCount++;
                    }
                }
                if (activeCount == 0) {
                    defeatFired = true;
                    new ShowDialogueAction("System", "MISSION FAILED: Your base has been overrun.").execute(battlefield);
                }
            }
        }

        // 7. Victory check
        if (!victoryFired && !defeatFired && wave2Fired) {
            if (triggerTimeVictory.evaluate(elapsedTime, battlefield)) {
                if (battlefield.getEngagement().getFactions().size() > 1) {
                    model.battlefield.warfare.Faction enemyFaction = battlefield.getEngagement().getFactions().get(1);
                    int enemyCount = 0;
                    for (model.battlefield.army.components.Unit u : enemyFaction.getUnits()) {
                        if (!u.destroyed()) {
                            enemyCount++;
                        }
                    }
                    if (enemyCount == 0) {
                        victoryFired = true;
                        new CompleteObjectiveAction(primaryObjective).execute(battlefield);
                        new ShowDialogueAction("System", "MISSION SUCCESS: Sector secured! Prepare for the next drop.").execute(battlefield);
                    }
                }
            }
        }
    }

    @Override
    public boolean isVictoryAchieved() {
        return victoryFired;
    }

    @Override
    public boolean isDefeatTriggered() {
        return defeatFired;
    }
}
