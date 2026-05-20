package model.campaign;

import model.battlefield.Battlefield;

public interface MissionScript {
    void initialize(Battlefield battlefield);
    void update(double elapsedTime, Battlefield battlefield);
    boolean isVictoryAchieved();
    boolean isDefeatTriggered();
}
