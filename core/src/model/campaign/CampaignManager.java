package model.campaign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.ModelManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class CampaignManager {
    private static final Logger logger = Logger.getLogger(CampaignManager.class.getName());
    private static final String CAMPAIGN_JSON_PATH = "assets/data/campaign.json";

    private static List<CampaignMission> missions = new ArrayList<>();
    private static int currentMissionIndex = -1;
    private static boolean campaignActive = false;
    private static MissionScript activeScript = null;

    public static class CampaignMission {
        public String name;
        public String mapFile;
        public String description;
    }

    public static void loadCampaign() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(CAMPAIGN_JSON_PATH);
            if (file.exists()) {
                missions = mapper.readValue(file, new TypeReference<List<CampaignMission>>() {});
                logger.info("Loaded campaign with " + missions.size() + " missions.");
            } else {
                logger.warning("campaign.json not found, using default configuration");
                // Default fallback configuration
                missions.clear();
                CampaignMission mission1 = new CampaignMission();
                mission1.name = "Mission 1: The Outpost";
                mission1.mapFile = "assets/maps/test.btf";
                mission1.description = "Establish a base and hold off initial alien incursions.";
                missions.add(mission1);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load campaign JSON", e);
        }
    }

    public static void startCampaign() {
        loadCampaign();
        if (missions.isEmpty()) {
            logger.warning("No missions available to start.");
            return;
        }
        campaignActive = true;
        currentMissionIndex = 0;
        loadMission(currentMissionIndex);
    }

    public static void nextMission() {
        if (!campaignActive) return;
        currentMissionIndex++;
        if (currentMissionIndex < missions.size()) {
            loadMission(currentMissionIndex);
        } else {
            logger.info("Campaign completed successfully!");
            campaignActive = false;
            activeScript = null;
        }
    }

    public static void restartMission() {
        if (currentMissionIndex >= 0 && currentMissionIndex < missions.size()) {
            loadMission(currentMissionIndex);
        }
    }

    private static void loadMission(int index) {
        CampaignMission mission = missions.get(index);
        logger.info("Loading Campaign Mission " + (index + 1) + ": " + mission.name);
        ModelManager.loadBattlefield(mission.mapFile);
        
        // Instantiate the mission script based on mission index
        if (index == 0) {
            activeScript = new DemoMissionScript();
        } else {
            activeScript = null;
        }

        if (activeScript != null) {
            activeScript.initialize(ModelManager.getBattlefield());
        }
    }

    public static boolean isCampaignActive() {
        return campaignActive;
    }

    public static void setCampaignActive(boolean active) {
        campaignActive = active;
    }

    public static MissionScript getActiveScript() {
        return activeScript;
    }

    public static CampaignMission getCurrentMission() {
        if (currentMissionIndex >= 0 && currentMissionIndex < missions.size()) {
            return missions.get(currentMissionIndex);
        }
        return null;
    }

    public static int getCurrentMissionIndex() {
        return currentMissionIndex;
    }

    public static List<CampaignMission> getMissions() {
        return missions;
    }
}
