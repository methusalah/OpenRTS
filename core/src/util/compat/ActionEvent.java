package util.compat;

/**
 * Drop-in replacement for java.awt.event.ActionEvent that is TeaVM-compatible.
 */
public class ActionEvent {
    private final Object source;
    private final int id;
    private final String command;

    public ActionEvent(Object source, int id, String command) {
        this.source = source;
        this.id = id;
        this.command = command;
    }

    public Object getSource()    { return source; }
    public int    getId()        { return id; }
    public String getActionCommand() { return command; }
}
