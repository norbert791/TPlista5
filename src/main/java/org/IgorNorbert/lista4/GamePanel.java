package org.IgorNorbert.lista4;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {
    private CheckerButton[][] checkerButtons;
    private final int boardHeight = 17;
    private final int boardLength = 13;
    private final int[] coordinates = new int[4];
    private final MainFrame frame;
    private boolean isMovePrepared = false;
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof CheckerButton && frame.getCurrentPlayerColor() == frame.getPlayerColor()) {
            CheckerButton temp = (CheckerButton) e.getSource();
            if(temp.getBackground() == Color.BLACK){
                this.isMovePrepared = false;
            }
            else if(!isMovePrepared){
                coordinates[0] = temp.x;
                coordinates[1] = temp.y;
                isMovePrepared = true;
            }
            else {
                coordinates[2] = temp.x;
                coordinates[3] = temp.y;
                if(temp.x == coordinates[0] && temp.y == coordinates[1]){
                    isMovePrepared = false;
                    return;
                }
                System.out.println("New pos selected");
                frame.makeMove(coordinates);
                isMovePrepared = false;
            }
        }
    }

    public GamePanel(MainFrame frame) {
        super();
        this.frame = frame;
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

    public void updateButtons(org.IgorNorbert.lista4.Color[][] board) {
        if (board != null && checkerButtons != null) {
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardLength * 2 - 1; j++) {
                    if (checkerButtons[i][j].getBackground() != Color.BLACK) {
                        checkerButtons[i][j].setBackground(translateColor(board[i][j]));
                    }
                }
            }
        }
    }

    private Color translateColor(org.IgorNorbert.lista4.Color playerColor) {
        if(playerColor == null){
            return Color.WHITE;
        }
        return switch (playerColor){
            case YELLOW -> Color.yellow;
            case MAGENTA -> Color.magenta;
            case CYAN -> Color.cyan;
            case RED -> Color.red;
            case GREEN -> Color.green;
            case BLUE -> Color.blue;
        };
    }

    private class CheckerButton extends JButton{
        public int x;
        public int y;
    }
}
