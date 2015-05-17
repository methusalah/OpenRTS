package app.example;
import java.io.IOException;

import model.ModelManager;
import app.OpenRTSApplication;

public class GameMutliplayerHuman extends Game {

	protected static String mapfilename = "map02.btf";

	public static void main(String[] args) {

		if (args.length > 0) {
			mapfilename = args[0];
		}

		GameMutliplayerHuman app = new GameMutliplayerHuman();
		OpenRTSApplication.main(app);
		try {
			app.startClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void simpleInitApp() {
		super.simpleInitApp();

		if (!mapfilename.isEmpty()) {
			ModelManager.loadBattlefield(mapfilename);
		}
	}
}
