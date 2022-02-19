package org.Norbert.lista5.ClientUI.MatchHistory;

import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameDescriptionRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameListPanel extends JPanel implements ActionListener {
    private final HistoryFrame frame;
    public GameListPanel(GameDescriptionRecord[] records, HistoryFrame frame) {
        super();
        this.frame = frame;
        this.setLayout(new GridLayout(records.length,1));
        for (GameDescriptionRecord record : records) {
            GameButton temp = new GameButton(record.gameType(), record.date(), record.gameId());
            temp.addActionListener(this);
            this.add(temp);
        }
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        frame.fetchGame( ((GameButton) e.getSource()).gameId);
    }

    private class GameButton extends JButton {
        private int gameId;
        public GameButton(String gameType, String gameTime, int gameId) {
            super(gameType + " " + gameTime);
            this.gameId = gameId;
        }
    }
}
