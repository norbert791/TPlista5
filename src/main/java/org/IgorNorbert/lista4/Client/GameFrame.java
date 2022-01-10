package org.IgorNorbert.lista4.Client;


import javax.swing.*;
import java.awt.*;

public class GameFrame {

    JFrame frame = new JFrame();
    JLabel label = new JLabel("Milk!!");
    JPanel button = new JPanel();


    GameFrame(){

        button.setPreferredSize(new Dimension(150,150));
        button.setBackground(Color.red);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(button,BorderLayout.NORTH);
        //label.setBounds(0,0,100,50);
        panel.setBackground(Color.cyan);
        panel.setBounds(0,0,420,420);
        BorderLayout layout = new BorderLayout();
        layout.setHgap(10);
        layout.setVgap(10);


        panel.add(label,BorderLayout.SOUTH);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420,420);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}
