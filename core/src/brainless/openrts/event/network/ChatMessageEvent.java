package brainless.openrts.event.network;

import com.jme3.network.serializing.Serializable;


@Serializable
class ChatMessageEvent extends NetworkEvent {
	
	private int playerId;
	private String content;

	public ChatMessageEvent(int playerId, String content) {
		super();
		this.playerId = playerId;
		this.content = content;
	}
	
	
	public int getPlayerId() {
		return playerId;
	}

	public String getContent() {
		return content;
	}

}
