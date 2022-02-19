package org.Norbert.lista5.ClientUI.MatchHistory;

import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameRecord;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.PlayerMove;

import javax.swing.*;
import java.awt.*;

public class GameReconstructionPanel extends JPanel { //TODO: Merge with gamePanel
    private JButton[][] boardField;
    private PlayerMove[] moves;
    private int moveCounter = 0;

    public GameReconstructionPanel(GameRecord record) {
        super();
        moves = record.moves();
        this.setBackground(Color.black);
        boolean[][] mask = record.boardShape();
        this.setLayout(new GridLayout(mask.length, mask[0].length));
        JButton[][] temp = new JButton[mask.length][mask[0].length];
        for (int i = 0; i < mask.length; i++) {
            for (int j = 0; j < mask[i].length; j++) {
                temp[i][j] = new JButton();
                temp[i][j].setPreferredSize(new Dimension(15, 30));
                if (mask[i][j]) {
                    temp[i][j].setBackground(Color.white);
                    temp[i][j].setEnabled(true);
                    temp[i][j].setVisible(true);
                } else {
                    temp[i][j].setBackground(Color.BLACK);
                    temp[i][j].setEnabled(false);
                    temp[i][j].setVisible(false);
                }
                this.add(temp[i][j]);
                this.boardField = temp;
            }
        }
        org.Norbert.lista5.Game.Color[][] board = record.initialBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null) {
                    boardField[i][j].setBackground(translateColor(board[i][j]));
                }
            }
        }
    }
    public void nextMove() {
        if (moveCounter < moves.length && moves[moveCounter]
                .moveType() == PlayerMove.MoveType.CHECKER_MOVE) {
            PlayerMove.CheckerMove temp = moves[moveCounter].checkerMove();
            boardField[temp.oldY()][temp.oldX()].setBackground(Color.white);
            boardField[temp.newY()][temp.newX()].setBackground(
                    translateColor(moves[moveCounter].playerColor()));
            moveCounter++;
        }
        else if (moveCounter < moves.length ) {
            moveCounter++;
        }
    }

    public void previousMove() {
        if (moveCounter > 0 && moves[moveCounter - 1].
                moveType() == PlayerMove.MoveType.CHECKER_MOVE) {
            moveCounter--;
            PlayerMove.CheckerMove temp = moves[moveCounter].checkerMove();
            boardField[temp.oldY()][temp.oldX()].setBackground(translateColor(
                    moves[moveCounter].playerColor()));
            boardField[temp.newY()][temp.newX()].setBackground(Color.white);
        }
        else if (moveCounter > 0) {
            moveCounter--;
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
}
