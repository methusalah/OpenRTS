package event;

public class ObjectiveEvent extends Event {
    public final String objective;
    public final boolean active;
    public final boolean completed;

    public ObjectiveEvent(String objective, boolean active, boolean completed) {
        this.objective = objective;
        this.active = active;
        this.completed = completed;
    }
}
