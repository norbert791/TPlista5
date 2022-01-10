package org.IgorNorbert.lista4.Client;

import javax.swing.*;
import java.awt.*;

public class ChoseColor extends JPanel {

    ChoseColor(){


        JButton red = new JButton("red");
        red.setPreferredSize(new Dimension(120,30));
        red.setFocusable(false);
        JButton green = new JButton("green");
        green.setPreferredSize(new Dimension(120,30));
        green.setFocusable(false);
        JButton blue = new JButton("blue");
        blue.setPreferredSize(new Dimension(120,30));
        blue.setFocusable(false);
        JButton cyan = new JButton("cyan");
        cyan.setPreferredSize(new Dimension(120,30));
        cyan.setFocusable(false);
        JButton magenta = new JButton("magenta");
        magenta.setPreferredSize(new Dimension(120,30));
        magenta.setFocusable(false);
        JButton yellow = new JButton("yellow");
        yellow.setPreferredSize(new Dimension(120,30));
        yellow.setFocusable(false);




        this.setBackground(Color.cyan);
        this.setBounds(760, 350, 800, 300);
        this.add(red);
        this.add(green);
        this.add(blue);
        this.add(cyan);
        this.add(magenta);
        this.add(yellow);
    }
}
