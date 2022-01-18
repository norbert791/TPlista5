package org.IgorNorbert.lista4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

public class LobbyPanel extends JPanel implements ActionListener {
    private final MainFrame frame;
    private final Collection<LobbyButton> buttons = new ArrayList<>();
    public LobbyPanel(MainFrame frame, int[] numbers) {
        super();
        this.frame = frame;
        this.setLayout(new GridLayout(numbers.length,1));
        for (int i = 0; i < numbers.length; i++) {
            LobbyButton temp = new LobbyButton(i, numbers[i]);
            temp.addActionListener(this);
            buttons.add(temp);
            this.add(temp);
        }
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
            super("Lobby no. " + lobbyNumber + " (" +  numberOfPlayers + "/6)");
            this.lobbyNumber = lobbyNumber;
            this.numberOfPlayers = numberOfPlayers;
        }
    }
    public void setPanel(int[] numbers) {
        for (int i = 0; i < numbers.length; i++) {
            LobbyButton temp = new LobbyButton(i, numbers[i]);
            temp.addActionListener(this);
            buttons.add(temp);
            this.add(temp);
        }
    }
}
