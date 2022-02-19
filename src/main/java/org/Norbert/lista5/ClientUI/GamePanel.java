package org.Norbert.lista5.ClientUI;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel containing the game board
 */
public class GamePanel extends JPanel implements ActionListener {
    private CheckerButton[][] checkerButtons;
    private final int[] coordinates = new int[4];
    private final MainFrame frame;
    private boolean isMovePrepared = false;
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof CheckerButton && frame.getCurrentPlayerColor() == frame.getPlayerColor()) {
            CheckerButton temp = (CheckerButton) e.getSource();
            if (temp.getBackground() == Color.BLACK) {
                this.isMovePrepared = false;
            } else if (!isMovePrepared) {
                coordinates[0] = temp.x;
                coordinates[1] = temp.y;
                isMovePrepared = true;
            } else {
                coordinates[2] = temp.x;
                coordinates[3] = temp.y;
                if (temp.x == coordinates[0] && temp.y == coordinates[1]) {
                    isMovePrepared = false;
                    return;
                }
                frame.makeMove(coordinates);
                isMovePrepared = false;
            }
        }
    }

    /**
     * Constructor.
     * @param frame reference to the MainFrame
     * @param mask boolean array for generating board
     */
    public GamePanel(MainFrame frame, boolean[][] mask) {
        super();
        this.setBackground(Color.black);
        this.setLayout(new GridLayout(mask.length, mask[0].length));
        this.frame = frame;
        CheckerButton[][] temp = new CheckerButton[mask.length][mask[0].length];
        for (int i = 0; i < mask.length; i++) {
            for (int j = 0; j < mask[i].length; j++) {
                temp[i][j] = new CheckerButton();
                temp[i][j].x = j;
                temp[i][j].y = i;
                temp[i][j].setPreferredSize(new Dimension(15,30));
                if (mask[i][j]) {
                    temp[i][j].setBackground(Color.white);
                    temp[i][j].setEnabled(true);
                    temp[i][j].setVisible(true);
                    temp[i][j].addActionListener(this);
                } else {
                    temp[i][j].setBackground(Color.BLACK);
                    temp[i][j].setEnabled(false);
                    temp[i][j].setVisible(false);
                }
                this.add(temp[i][j]);
                this.checkerButtons = temp;
            }
        }
    }

    /**
     * Updates the board
     * @param board new board
     */
    public void updateButtons(org.Norbert.lista5.Game.Color[][] board) {
        if (board != null && checkerButtons != null) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (checkerButtons[i][j].getBackground() != Color.BLACK) {
                        checkerButtons[i][j].setBackground(translateColor(board[i][j]));
                    }
                }
            }
        }
    }

    /**
     * translates org.IgorNorbert.lista4.Color to AWT.Color
     * @param playerColor Color to be translated
     * @return Color translation
     */
    private Color translateColor(org.Norbert.lista5.Game.Color playerColor) {
        if (playerColor == null) {
            return Color.WHITE;
        }
        return switch (playerColor) {
            case YELLOW -> Color.yellow;
            case MAGENTA -> Color.magenta;
            case CYAN -> Color.cyan;
            case RED -> Color.red;
            case GREEN -> Color.green;
            case BLUE -> Color.blue;
        };
    }

    /**
     * Used for storing button's coordinates.
     */
    private class CheckerButton extends JButton {
        public int x;
        public int y;
    }
}
