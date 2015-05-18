package app.example;
import model.ModelManager;
import app.OpenRTSApplication;

public class GameMutliplayerAlien extends Game {

	protected static String mapfilename = "/Users/wuendsch/git/OpenRTS/core/assets/maps/map02.btf";

	public static void main(String[] args) {

		if (args.length > 0) {
			mapfilename = args[0];
		}

		GameMutliplayerAlien app = new GameMutliplayerAlien();
		OpenRTSApplication.main(app);
		app.startClient();
	}

	@Override
	public void simpleInitApp() {
		super.simpleInitApp();

		if (!mapfilename.isEmpty()) {
			ModelManager.loadBattlefield(mapfilename);
		}
	}
}
