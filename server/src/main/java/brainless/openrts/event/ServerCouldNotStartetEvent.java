package brainless.openrts.event;

public class ServerCouldNotStartetEvent extends ServerEvent {

		private String message;

		public ServerCouldNotStartetEvent(String message) {
			super();
			this.message = message;
		}
}
