package event;

public class DialogueEvent extends Event {
    public final String speaker;
    public final String text;

    public DialogueEvent(String speaker, String text) {
        this.speaker = speaker;
        this.text = text;
    }
}
