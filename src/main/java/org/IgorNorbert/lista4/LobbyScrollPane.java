package org.IgorNorbert.lista4;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

public class LobbyScrollPane extends JScrollPane implements ActionListener {
    private final MainFrame frame;
    private final Collection<LobbyButton> buttons = new ArrayList<>();
    public LobbyScrollPane (MainFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object object = e.getSource();
        if (object.getClass() == LobbyButton.class) {
            frame.join(((LobbyButton)object).lobbyNumber);
        }
    }

    private class LobbyButton extends JButton{
        public int lobbyNumber;
        public int numberOfPlayers;
        public LobbyButton(int lobbyNumber, int numberOfPlayers) {
            super("Lobby no. " + lobbyNumber + "(" +  numberOfPlayers + "/6)");
            this.lobbyNumber = lobbyNumber;
            this.numberOfPlayers = numberOfPlayers;
        }
    }
    public void updateList(int[] lobbies) {
        for (LobbyButton button : buttons) {
            this.remove(button);
        }
        buttons.clear();
        for (int i = 0; i < lobbies.length; i++) {
            LobbyButton temp = new LobbyButton(i, lobbies[i]);
            buttons.add(temp);
            this.add(temp);
        }
    }
}
