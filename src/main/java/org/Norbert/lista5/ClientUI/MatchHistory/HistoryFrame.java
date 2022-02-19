package org.Norbert.lista5.ClientUI.MatchHistory;

import org.Norbert.lista5.ClientUI.MainFrame;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameDescriptionRecord;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameRecord;

import javax.swing.*;
import java.awt.*;

public class HistoryFrame extends JFrame {
    MainFrame mainFrame;
    GameListPanel gameList;
    GameReconstructionPanel panel;
    JPanel mainPanel;
    public HistoryFrame(MainFrame mainFrame, GameDescriptionRecord[] records) {
        super("Match history");
        this.mainFrame = mainFrame;
        this.setBounds(360, 540, 540, 360);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new GridLayout(1,1));
        this.setJMenuBar(new HistoryFrameMenuBar(this));
        mainPanel = new JPanel();
        this.add(mainPanel);
        mainPanel.setLayout(new GridLayout(1,1));
        gameList = new GameListPanel(records, this);
        mainPanel.add(gameList);
        setVisible(true);
    }
    public void fetchGame(int gameId) {
        mainFrame.fetchGame(gameId);
    }
    public void previousMove() {
        panel.previousMove();
        revalidate();
    }
    public void nextMove() {
        panel.nextMove();
        revalidate();
    }

    public void printGame(GameRecord gameRecord) {
        mainPanel.remove(gameList);
        mainPanel.setLayout(new GridLayout(1,2));
        panel = new GameReconstructionPanel(gameRecord);
        mainPanel.add(panel);
        JPanel temp = new JPanel();
        temp.setLayout(new GridLayout(gameRecord.playerMap().size(), 2));
        for (String name : gameRecord.playerMap().keySet()) {
            JLabel label = new JLabel(name);
            temp.add(label);
            label = new JLabel();
            label.setText("temp");
            label.setOpaque(true);
            label.setBackground(
                    translateColor(gameRecord.playerMap().get(name)));
            label.setForeground(
                    translateColor(gameRecord.playerMap().get(name)));
            temp.add(label);
        }
        mainPanel.add(temp);
        revalidate();
    }

    /**
     * translates org.IgorNorbert.lista4.Color to AWT.Color
     * @param playerColor Color to be translated
     * @return Color translation
     */
    private Color translateColor(org.Norbert.lista5.Game.Color playerColor) {
        if (playerColor == null) {
            return Color.WHITE;
        }
        return switch (playerColor) {
            case YELLOW -> Color.yellow;
            case MAGENTA -> Color.magenta;
            case CYAN -> Color.cyan;
            case RED -> Color.red;
            case GREEN -> Color.green;
            case BLUE -> Color.blue;
        };
    }
}
