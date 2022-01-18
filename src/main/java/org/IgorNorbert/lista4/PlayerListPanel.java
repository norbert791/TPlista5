package org.IgorNorbert.lista4;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class PlayerListPanel extends JPanel {
    private Collection<NameLabel> players = new ArrayList<>();
    private MainFrame frame;
    /**
     * Creates instance with reference to frame
     * @param frame frame to whom the instance will refer
     */
    public PlayerListPanel(MainFrame frame) {
        super();
        this.frame = frame;
        setLayout(new GridLayout(6,2));
    }

    private class NameLabel extends JLabel {
        public NameLabel(String name) {
            super(name);
            this.name = name;
        }
        public String name;
    }

    public void updatePlayerList(Map<String, Color> map) {
        this.removeAll();
        this.setLayout(new GridLayout(map.keySet().size(), 2));
        players.clear();
        for (String string : map.keySet()) {
            NameLabel temp = new NameLabel(string);
            players.add(temp);
            this.add(temp);
            temp = new NameLabel("");
            temp.setBackground(frame.translateColor(map.get(string)));
        }
    }

    public void updateWinOrder(String[] order) {
        for (int i = 0; i < order.length; i++) {
            for (NameLabel label : players) {
                if(Objects.equals(label.name, order[i])) {
                    label.name = label.name + " (" + i + ".)";
                }
            }
        }
    }
}
