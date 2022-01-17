package org.IgorNorbert.lista4;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements UserInterface, ActionListener{
    private MenuBar menuBar;
    private LayoutManager layout;
    private ClientLogic clientLogic;
    private JPanel boardPanel = null;
    private boolean playerTurn = false;
    private boolean isMovePrepared = false;
    private int boardHeight = 17;
    private int boardLength = 13;
    private int[] coordinates = new int[4];
    private Integer playerInteger= null;
    private org.IgorNorbert.lista4.Color playerColor;
    private org.IgorNorbert.lista4.Color currentPlayerColor;
    private CheckerButton[][] buttonArray = new CheckerButton[boardHeight][boardLength];

    public MainFrame(ClientLogic clientLogic){
        super("Chinese checkers");
        this.clientLogic = clientLogic;
        prepareBoard();
        //this.setLayout(new GridLayout(1,1));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(360, 540, 540, 360 );
        this.menuBar = new MenuBar(this);
        this.setJMenuBar(menuBar);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof CheckerButton && currentPlayerColor == playerColor) {
            CheckerButton temp = (CheckerButton) e.getSource();
            if(temp.getBackground() == Color.BLACK){
                this.isMovePrepared = false;
            }
            else if(!isMovePrepared){
                coordinates[0] = temp.x;
                coordinates[1] = temp.y;
                isMovePrepared = true;
            }
            else if(isMovePrepared){
                coordinates[2] = temp.x;
                coordinates[3] = temp.y;
                if(temp.x == coordinates[0] && temp.y == coordinates[1]){
                    isMovePrepared = false;
                    return;
                }
                System.out.println("New pos selected");
                makeMove();
                isMovePrepared = false;
            }
        }
    }

    @Override
    public void printBoard(org.IgorNorbert.lista4.Color[][] board) {
        if (board != null && boardPanel != null) {
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardLength * 2 - 1; j++) {
                    if (buttonArray[i][j].getBackground() != Color.BLACK) {
                        buttonArray[i][j].setBackground(translateColor(board[i][j]));
                    }
                }
            }
        }
        repaint();
    }
    @Override
    public void setCurrentPlayer(org.IgorNorbert.lista4.Color color) {
        this.currentPlayerColor = color;
        this.menuBar.setCurrentColor(translateColor(color));
    }

    @Override
    public void setPlayerColor(org.IgorNorbert.lista4.Color color) {
        this.playerColor = color;
        this.menuBar.setPlayerColor(translateColor(color));
    }

    @Override
    public void printError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }

    @Override
    public void nextMove(boolean nextMove) {

    }

    @Override
    public void printLobbyList(int[] lobbies) {

    }

    private class CheckerButton extends JButton{
        public int x;
        public int y;
    }
    private void prepareBoard() {
        boardPanel = new JPanel();
        final int midHeight = boardHeight / 2;
        final int midLength = boardLength / 2;
        final int starSize = 4;
        this.add(boardPanel);
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(boardHeight, boardLength * 2));
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
                boardPanel.add(temp[i][j]);
                this.buttonArray = temp;
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
    public void connect(String address){
        clientLogic.connect(address);
    }
    public void join(int lobby) {
        clientLogic.join(lobby);
    }
    public void leave(){
        clientLogic.leave();
    }
    public void skipTurn(){
        clientLogic.skip();
    }
    private void makeMove(){
        clientLogic.moveChecker(coordinates[0],coordinates[1],coordinates[2],coordinates[3]);
    }
    public void disconnect(){
        clientLogic.disconnect();
    }
    public void setReady(boolean value){
        clientLogic.setReady(value);
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
}
