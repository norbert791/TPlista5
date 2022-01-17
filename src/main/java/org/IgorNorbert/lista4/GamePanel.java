package org.IgorNorbert.lista4;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {
    private CheckerButton[][] checkerButtons;
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public GamePanel() {
        super();
        final int boardHeight = 17;
        final int boardLength = 13;
        final int midHeight = boardHeight / 2;
        final int midLength = boardLength / 2;
        final int starSize = 4;
        this.setBackground(Color.black);
        this.setLayout(new GridLayout(boardHeight, boardLength * 2));
        CheckerButton[][] temp = new CheckerButton[boardHeight][boardLength * 2];
        for( int i = 0; i < boardHeight; i++){
            for (int j = 0; j < boardLength * 2 - 1; j++){
                temp[i][j] = new CheckerButton();
                temp[i][j].x = j;
                temp[i][j].y = i;
                temp[i][j].setPreferredSize(new Dimension(15,30));
                temp[i][j].setBackground(java.awt.Color.BLACK);
                temp[i][j].addActionListener(this);
                temp[i][j].setEnabled(false);
                temp[i][j].setVisible(false);
                this.add(temp[i][j]);
                this.checkerButtons = temp;
            }
        }
        for (int i = 0; i < starSize; i++) {
            for (int j = 0; j <= i * 2; j += 2) {
                temp[i][boardLength - 1 - i + j].setBackground(java.awt.Color.white);
                temp[i][boardLength - 1 - i + j].setEnabled(true);
                temp[i][boardLength - 1 - i + j].setVisible(true);
            }
        }
        for (int i = 4; i <= midHeight; i++) {
            for (int j = i - starSize;
                 j < boardLength * 2 - 1 - (i - starSize); j += 2) {
                temp[i][j].setBackground(java.awt.Color.white);
                temp[i][j].setEnabled(true);
                temp[i][j].setVisible(true);
            }
        }
        for (int i = midHeight + 1; i < boardLength; i++) {
            for (int j = starSize - 1 - (i -  midHeight - 1);
                 j < boardLength * 2 - midLength + 1 + (i - midHeight + 1); j += 2){
                temp[i][j].setBackground(java.awt.Color.white);
                temp[i][j].setEnabled(true);
                temp[i][j].setVisible(true);
            }
        }
        for (int i = boardLength; i < boardHeight; i++) {
            for (int j = midHeight + 1 + (i - boardLength);
                 j < boardHeight - 1 - (i - boardLength); j += 2) {
                temp[i][j].setBackground(Color.white);
                temp[i][j].setEnabled(true);
                temp[i][j].setVisible(true);
            }
        }
    }

    public void updateButtons(){

    }

    private class CheckerButton extends JButton{
        public int x;
        public int y;
    }
}
