package app.example;
import model.ModelManager;
import network.server.OpenRTSServer;
import app.OpenRTSApplication;

import com.jme3.system.JmeContext;

public class GameMutliplayerAlien extends Game {

	protected static String mapfilename = "assets/maps/test.btf";

	public static void main(String[] args) {

		if (args.length > 0) {
			mapfilename = args[0];
		}

		OpenRTSServer server = new OpenRTSServer();
		server.start(JmeContext.Type.Headless);

		GameMutliplayerAlien client = new GameMutliplayerAlien();

		OpenRTSApplication.main(client);
		client.startClient();
	}

	@Override
	public void simpleInitApp() {
		super.simpleInitApp();
		if (!mapfilename.isEmpty()) {
			ModelManager.loadBattlefield(mapfilename);
			faction = ModelManager.getBattlefield().getEngagement().getFactions().get(1);
		}

	}
}
