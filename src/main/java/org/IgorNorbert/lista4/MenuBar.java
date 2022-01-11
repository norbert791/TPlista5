package org.IgorNorbert.lista4;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBar extends JMenuBar implements ActionListener {
    private MainFrame frame;
    public MenuBar(MainFrame frame){
        this.frame = frame;
        Join temp = new Join();
        JMenuItem tempItem = new Connect();
        tempItem.addActionListener(this);
        temp.add(tempItem);
        tempItem = new Enter();
        tempItem.addActionListener(this);
        temp.add(tempItem);
        tempItem = new Disconnect();
        tempItem.addActionListener(this);
        temp.add(tempItem);
        this.add(temp);
        Game temp2 = new Game();
        tempItem = new Leave();
        tempItem.addActionListener(this);
        temp2.add(tempItem);
        tempItem = new Ready();
        temp2.add(tempItem);
        temp2.add(tempItem);
        tempItem = new SkipTurn();
        tempItem.addActionListener(this);
        temp2.add(tempItem);
        this.add(temp2);
    }
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
    private class Enter extends JMenuItem{
        Enter(){
            super("Enter lobby");
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
        else if(e.getSource() instanceof Enter) {
            String room = JOptionPane.showInputDialog(
                    frame,
                    "Insert room number"
                    );
            try {
                int result = Integer.parseInt(room);
                frame.join(result);
            }catch (NumberFormatException ex){

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

    }
}