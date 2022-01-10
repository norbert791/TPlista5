package org.IgorNorbert.lista4.Client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MenuFrame extends JFrame implements ActionListener {

    //ChoseColor choseColor = new ChoseColor();
    JLabel background = new JLabel();
    JLabel title = new JLabel();
    JButton newButton = new JButton("New Game");
    JLayeredPane layeredPane = new JLayeredPane();
    JPanel menuP = new JPanel();
    ImageIcon image = new ImageIcon("src/main/java/org/IgorNorbert/lista4/Graphics/MenuGraphics.jpg");
    ImageIcon image2 = new ImageIcon("src/main/java/org/IgorNorbert/lista4/Graphics/title.png");

    MenuFrame(){

        title.setIcon(image2);
        title.setBounds(0,0,1920,275);

        newButton.setBounds(100, 100, 30, 100);
        newButton.setFocusable(false);
        newButton.addActionListener(this);


        menuP.setBackground(Color.darkGray);
        menuP.setBounds(910,400,300,600);
        menuP.add(newButton);

            background.setIcon(image);
            background.setBounds(0,0,1920,1080);

            layeredPane.setBounds(0, 0, 1920, 1080);
            layeredPane.add(menuP);
            layeredPane.add(title);
            layeredPane.add(background);

            this.add(layeredPane);
            //this.add(color);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(1920,1080);
            this.setLayout(new BorderLayout());
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==newButton){
            newButton.setVisible(false);
            ChoseColor choseColor = new ChoseColor();
            layeredPane.add(choseColor);
        }
    }
}
