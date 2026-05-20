package util.compat;

/**
 * Drop-in replacement for java.awt.event.ActionListener that is TeaVM-compatible.
 */
public interface ActionListener {
    void actionPerformed(ActionEvent e);
}
