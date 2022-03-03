package org.Norbert.lista5.ClientUI;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBar extends JMenuBar implements ActionListener { //TODO: Refactor it
    private MainFrame frame;
    private JMenu playerColor;
    private JMenu currentColor;
    public MenuBar(MainFrame frame) {
        JMenu temp = new JMenu("Join");
        temp.add(new MyItem(ItemType.CONNECT));
        temp.add(new MyItem(ItemType.DISCONNECT));
        temp.add(new MyItem(ItemType.HISTORY));
        this.add(temp);
        temp = new JMenu("Game");
        temp.add(new MyItem(ItemType.READY));
        temp.add(new MyItem(ItemType.SKIP_TURN));
        temp.add(new MyItem(ItemType.LEAVE));
        this.add(temp);
        JMenu temp3 = new JMenu("Your color");
        temp3.setEnabled(false);
        this.add(temp3);
        playerColor = new JMenu();
        playerColor.setEnabled(false);
        playerColor.setBackground(Color.black);
        playerColor.setOpaque(true);
        this.add(playerColor);
        temp3 = new JMenu("Current color");
        temp3.setEnabled(false);
        this.add(temp3);
        currentColor = new JMenu();
        currentColor.setEnabled(false);
        currentColor.setBackground(Color.black);
        currentColor.setOpaque(true);
        this.add(currentColor);
        this.frame = frame;
        /*
        Join temp = new Join();
        JMenuItem tempItem = new Connect();
        tempItem.addActionListener(this);
        temp.add(tempItem);
        tempItem = new Disconnect();
        tempItem.addActionListener(this);
        temp.add(tempItem);
        this.add(temp);
        tempItem = new History();
        tempItem.addActionListener(this);
        temp.add(tempItem);
        Game temp2 = new Game();
        tempItem = new Leave();
        tempItem.addActionListener(this);
        temp2.add(tempItem);
        tempItem = new Ready();
        tempItem.addActionListener(this);
        temp2.add(tempItem);
        temp2.add(tempItem);
        tempItem = new SkipTurn();
        tempItem.addActionListener(this);
        temp2.add(tempItem);
        this.add(temp2);
        JMenu temp3 = new JMenu("Your color");
        temp3.setEnabled(false);
        this.add(temp3);
        playerColor = new JMenu();
        playerColor.setEnabled(false);
        playerColor.setBackground(Color.black);
        playerColor.setOpaque(true);
        this.add(playerColor);
        temp3 = new JMenu("Current color");
        temp3.setEnabled(false);
        this.add(temp3);
        currentColor = new JMenu();
        currentColor.setEnabled(false);
        currentColor.setBackground(Color.black);
        currentColor.setOpaque(true);
        this.add(currentColor);*/
    }
    public void setPlayerColor(Color color){
        this.playerColor.setBackground(color);
        repaint();
    }
    public void setCurrentColor(Color color){
        this.currentColor.setBackground(color);
        repaint();
    }
    @Override
    public void actionPerformed(ActionEvent e){
        ItemType temp = ((MyItem) e.getSource()).type;
        switch (temp) {
            case CONNECT -> {
                String address = JOptionPane.showInputDialog(
                        frame,
                        "Insert address"
                );
                if(address != null && !address.equals("")) {
                    frame.connect(address);
                }
            }
            case LEAVE -> frame.leave();
            case SKIP_TURN -> frame.skipTurn();
            case READY -> frame.setReady(true);
            case DISCONNECT -> frame.disconnect();
            case HISTORY -> frame.showHistory();
        }
    }
    private class MyItem extends JMenuItem {
        public ItemType type;
        public MyItem(ItemType type) {
            this.type = type;
            this.setText(switch (type) {
                case CONNECT -> "connect";
                case DISCONNECT -> "disconnect";
                case LEAVE -> "leave";
                case SKIP_TURN -> "skip";
                case READY -> "ready";
                case HISTORY -> "history";
            });
            addActionListener(MenuBar.this);
        }
    }
    private enum ItemType {
        CONNECT,
        DISCONNECT,
        LEAVE,
        SKIP_TURN,
        READY,
        HISTORY,
    }
}
