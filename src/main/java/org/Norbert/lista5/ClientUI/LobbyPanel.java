package org.Norbert.lista5.ClientUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Panel for storing lobby buttons.
 */
public class LobbyPanel extends JPanel implements ActionListener {
    private final MainFrame frame;
    private final Collection<LobbyButton> buttons = new ArrayList<>();
    public LobbyPanel(final MainFrame frame, int[] numbers) {
        super();
        this.frame = frame;
        this.setLayout(new GridLayout(numbers.length, 1));
        for (int i = 0; i < numbers.length; i++) {
            LobbyButton temp = new LobbyButton(i, numbers[i]);
            temp.addActionListener(this);
            buttons.add(temp);
            this.add(temp);
        }
    }

    /**
     * Used for lobby selection.
     * @param e event
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        Object object = e.getSource();
        if (object.getClass() == LobbyButton.class) {
            frame.join(((LobbyButton) object).lobbyNumber);
        }
    }

    /**
     * Used for representing lobby.
     */
    private class LobbyButton extends JButton {
        public int lobbyNumber;
        public int numberOfPlayers;
        public LobbyButton(int lobbyNumber, int numberOfPlayers) {
            super("Lobby no. " + lobbyNumber + " (" +  numberOfPlayers + "/6)");
            this.lobbyNumber = lobbyNumber;
            this.numberOfPlayers = numberOfPlayers;
        }
    }
}
