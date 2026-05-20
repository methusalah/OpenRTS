package model.campaign;

import geometry.geom2d.Point2D;
import model.battlefield.Battlefield;
import model.battlefield.army.Engagement;
import model.battlefield.army.components.Unit;
import model.battlefield.warfare.Faction;
import model.campaign.TriggerCondition.EliminationTrigger;
import model.campaign.TriggerCondition.TimeTrigger;
import model.campaign.TriggerCondition.ZoneTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class CampaignScriptTest {

    private Battlefield mockBattlefield;
    private Engagement mockEngagement;
    private List<Faction> factions;

    @BeforeEach
    public void setUp() {
        mockBattlefield = Mockito.mock(Battlefield.class);
        mockEngagement = Mockito.mock(Engagement.class);
        factions = new ArrayList<>();

        when(mockBattlefield.getEngagement()).thenReturn(mockEngagement);
        when(mockEngagement.getFactions()).thenReturn(factions);
    }

    @Test
    public void testTimeTriggerAccumulation() {
        TimeTrigger trigger = new TimeTrigger(5.0);

        // First tick
        boolean fired1 = trigger.evaluate(3.0, mockBattlefield);
        assertThat(fired1).isFalse();
        assertThat(trigger.getTimeElapsed()).isEqualTo(3.0);

        // Second tick that breaches the threshold
        boolean fired2 = trigger.evaluate(2.5, mockBattlefield);
        assertThat(fired2).isTrue();
        assertThat(trigger.getTimeElapsed()).isEqualTo(5.5);
    }

    @Test
    public void testZoneTriggerUnitWithinRadius() {
        Faction mockFaction = Mockito.mock(Faction.class);
        factions.add(mockFaction);

        Point2D center = new Point2D(10, 10);
        ZoneTrigger trigger = new ZoneTrigger(center, 5.0, 0);

        Unit mockUnit = Mockito.mock(Unit.class);
        when(mockUnit.destroyed()).thenReturn(false);
        // Distance from (10, 10) to (13, 14) is 5.0
        when(mockUnit.getCoord()).thenReturn(new Point2D(13, 14));

        when(mockFaction.getUnits()).thenReturn(Collections.singletonList(mockUnit));

        boolean triggered = trigger.evaluate(1.0, mockBattlefield);
        assertThat(triggered).isTrue();
    }

    @Test
    public void testZoneTriggerUnitOutsideRadius() {
        Faction mockFaction = Mockito.mock(Faction.class);
        factions.add(mockFaction);

        Point2D center = new Point2D(10, 10);
        ZoneTrigger trigger = new ZoneTrigger(center, 5.0, 0);

        Unit mockUnit = Mockito.mock(Unit.class);
        when(mockUnit.destroyed()).thenReturn(false);
        // Distance is greater than 5.0
        when(mockUnit.getCoord()).thenReturn(new Point2D(20, 20));

        when(mockFaction.getUnits()).thenReturn(Collections.singletonList(mockUnit));

        boolean triggered = trigger.evaluate(1.0, mockBattlefield);
        assertThat(triggered).isFalse();
    }

    @Test
    public void testZoneTriggerUnitDestroyed() {
        Faction mockFaction = Mockito.mock(Faction.class);
        factions.add(mockFaction);

        Point2D center = new Point2D(10, 10);
        ZoneTrigger trigger = new ZoneTrigger(center, 5.0, 0);

        Unit mockUnit = Mockito.mock(Unit.class);
        when(mockUnit.destroyed()).thenReturn(true);
        when(mockUnit.getCoord()).thenReturn(new Point2D(11, 11)); // inside radius but destroyed

        when(mockFaction.getUnits()).thenReturn(Collections.singletonList(mockUnit));

        boolean triggered = trigger.evaluate(1.0, mockBattlefield);
        assertThat(triggered).isFalse();
    }

    @Test
    public void testZoneTriggerFactionIndexOutOfBounds() {
        Point2D center = new Point2D(10, 10);
        ZoneTrigger trigger = new ZoneTrigger(center, 5.0, 0); // No factions in the list

        boolean triggered = trigger.evaluate(1.0, mockBattlefield);
        assertThat(triggered).isFalse();
    }

    @Test
    public void testEliminationTriggerAllDestroyed() {
        Faction mockFaction = Mockito.mock(Faction.class);
        factions.add(mockFaction);

        EliminationTrigger trigger = new EliminationTrigger(0);

        Unit mockUnit1 = Mockito.mock(Unit.class);
        Unit mockUnit2 = Mockito.mock(Unit.class);
        when(mockUnit1.destroyed()).thenReturn(true);
        when(mockUnit2.destroyed()).thenReturn(true);

        List<Unit> units = new ArrayList<>();
        units.add(mockUnit1);
        units.add(mockUnit2);
        when(mockFaction.getUnits()).thenReturn(units);

        boolean triggered = trigger.evaluate(1.0, mockBattlefield);
        assertThat(triggered).isTrue();
    }

    @Test
    public void testEliminationTriggerActiveUnitsExist() {
        Faction mockFaction = Mockito.mock(Faction.class);
        factions.add(mockFaction);

        EliminationTrigger trigger = new EliminationTrigger(0);

        Unit mockUnit1 = Mockito.mock(Unit.class);
        Unit mockUnit2 = Mockito.mock(Unit.class);
        when(mockUnit1.destroyed()).thenReturn(false); // still active
        when(mockUnit2.destroyed()).thenReturn(true);

        List<Unit> units = new ArrayList<>();
        units.add(mockUnit1);
        units.add(mockUnit2);
        when(mockFaction.getUnits()).thenReturn(units);

        boolean triggered = trigger.evaluate(1.0, mockBattlefield);
        assertThat(triggered).isFalse();
    }

    @Test
    public void testEliminationTriggerFactionIndexOutOfBounds() {
        EliminationTrigger trigger = new EliminationTrigger(0); // No factions in the list

        boolean triggered = trigger.evaluate(1.0, mockBattlefield);
        assertThat(triggered).isFalse();
    }

    @Test
    public void testCampaignJsonParsing() {
        CampaignManager.loadCampaign();
        assertThat(CampaignManager.getMissions()).isNotEmpty();
        assertThat(CampaignManager.getMissions().size()).isEqualTo(2);
        assertThat(CampaignManager.getMissions().get(0).name).isEqualTo("Mission 1: The Outpost");
        assertThat(CampaignManager.getMissions().get(1).name).isEqualTo("Mission 2: Counter-Attack");
    }
}

