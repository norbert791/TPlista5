package org.Norbert.lista4.ClientUI;

import org.Norbert.lista4.ClientUI.MatchHistory.HistoryFrame;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBar extends JMenuBar implements ActionListener { //TODO: Refactor it
    private MainFrame frame;
    private JMenu playerColor;
    private JMenu currentColor;
    public MenuBar(MainFrame frame) {
        this.frame = frame;
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
        this.add(currentColor);
    }
    //TODO: Make it enum
    private class Join extends JMenu{
        Join(){
            super("Join");
        }
    }
    private class Game extends JMenu{
        Game(){
            super("Game");
        }
    }
    private class Connect extends JMenuItem{
        Connect(){
            super("Connect");
        }
    }
    private class Disconnect extends JMenuItem{
        Disconnect(){
            super("Disconnect");
        }
    }
    private class Leave extends JMenuItem{
        Leave(){
            super("Leave");
        }
    }
    private class SkipTurn extends  JMenuItem{
        SkipTurn(){
            super("SkipTurn");
        }
    }
    private class Ready extends JMenuItem{
        Ready(){
            super("SetReady");
        }
    }
    private class History extends JMenuItem {
        History() { super("History");}
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
        if(e.getSource() instanceof Connect){
            String address = JOptionPane.showInputDialog(
                    frame,
                    "Insert address"
            );
            if(address != null && !address.equals("")) {
                frame.connect(address);
            }
        }
        else if(e.getSource() instanceof Leave ) {
            frame.leave();
        }
        else if(e.getSource() instanceof SkipTurn) {
            frame.skipTurn();
        }
        else if(e.getSource() instanceof Disconnect) {
            frame.disconnect();
        }
        else if(e.getSource() instanceof Ready){
            frame.setReady(true);
        }
        else if(e.getSource() instanceof History) {
            frame.showHistory();
        }

    }
}
