package org.Norbert.lista5.ClientUI.MatchHistory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class HistoryFrameMenuBar extends JMenuBar implements ActionListener {
    private final HistoryFrame frame;
    public HistoryFrameMenuBar(HistoryFrame frame) {
        super();
        this.frame = frame;
        JMenu temp = new JMenu("Move");
        JMenuItem temp2 = new JMenuItem("Next");
        temp2.addActionListener(this);
        temp.add(temp2);
        temp2 = new JMenuItem("Previous");
        temp2.addActionListener(this);
        temp.add(temp2);
        this.add(temp);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (Objects.equals(((JMenuItem) e.getSource()).getText(), "Next")) {
            frame.nextMove();
        }
        else if (Objects.equals(((JMenuItem) e.getSource()).getText(), "Previous")) {
            frame.previousMove();
        }
    }
}
