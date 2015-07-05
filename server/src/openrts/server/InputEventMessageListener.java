package openrts.server;

import event.InputEvent;
import geometry.geom2d.Point2D;

import java.util.logging.Logger;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;

import controller.CommandManager;
import controller.game.MultiplayerGameInputInterpreter;

public class InputEventMessageListener implements com.jme3.network.MessageListener<HostedConnection> {

	private static final Logger logger = Logger.getLogger(InputEventMessageListener.class.getName());
	protected final static int DOUBLE_CLIC_DELAY = 200;// milliseconds
	protected final static int DOUBLE_CLIC_MAX_OFFSET = 5;// in pixels on screen

	private boolean multipleSelection = false;
	private double dblclicTimer = 0;
	private Point2D dblclicCoord;
	private GameController ctrl;

	public InputEventMessageListener(GameController ctl) {
		this.ctrl = ctl;
	}

	@Override
	public void messageReceived(HostedConnection source, Message message) {
		if (message instanceof InputEvent) {
			// do something with the message
			InputEvent inputEvent = (InputEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + inputEvent.getCommand() + "'");

			if (!inputEvent.getIsPressed()) {
				switch (inputEvent.getCommand()) {
					case MultiplayerGameInputInterpreter.MULTIPLE_SELECTION:
						CommandManager.setMultipleSelection(false);
						break;
					case MultiplayerGameInputInterpreter.SELECT:
						if(System.currentTimeMillis()-dblclicTimer < DOUBLE_CLIC_DELAY &&
								dblclicCoord.getDistance(new Point2D(inputEvent.getX(), inputEvent.getY())) < DOUBLE_CLIC_MAX_OFFSET){
							// double clic
							CommandManager.selectUnityInContext(ctrl.getSpatialSelector().getEntityId());
						} else {
							if (!ctrl.isDrawingZone()) {
								CommandManager.select(ctrl.getSpatialSelector().getEntityId(), new Point2D(inputEvent.getX(), inputEvent.getY()));
							}
						}
						ctrl.endSelectionZone();
						dblclicTimer = System.currentTimeMillis();
						dblclicCoord = new Point2D(inputEvent.getX(), inputEvent.getY());
						break;
					case MultiplayerGameInputInterpreter.ACTION:
						CommandManager.act(ctrl.getSpatialSelector().getEntityId(), new Point2D(inputEvent.getX(), inputEvent.getY()));
						break;
					case MultiplayerGameInputInterpreter.MOVE_ATTACK:
						CommandManager.setMoveAttack();
						break;
					case MultiplayerGameInputInterpreter.HOLD:
						CommandManager.orderHold();
						break;
					case MultiplayerGameInputInterpreter.PAUSE:
						ctrl.togglePause();
						break;
				}
			} else {
				// input pressed
				switch(inputEvent.getCommand()){
					case MultiplayerGameInputInterpreter.MULTIPLE_SELECTION:
						CommandManager.setMultipleSelection(true);
						break;
					case MultiplayerGameInputInterpreter.SELECT:
						ctrl.startSelectionZone(inputEvent.getX(), inputEvent.getY());
						break;
				}
			}
		}

		source.getServer().broadcast(message);
	}
}