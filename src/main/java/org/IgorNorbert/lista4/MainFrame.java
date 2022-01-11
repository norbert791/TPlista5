package org.IgorNorbert.lista4;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFrame extends JFrame implements ClientReceiver, ActionListener{
    private LayoutManager layout;
    private ClientConnector connector;
    private JPanel boardPanel;
    private boolean playerTurn = false;
    private boolean isMovePrepared = false;
    private int boardHeight = 17;
    private int boardLength = 13;
    private int[] coordinates = new int[4];
    private Integer playerInteger= null;
    private final ExecutorService pool = Executors.newFixedThreadPool(2);
    private CheckerButton[][] buttonArray = new CheckerButton[boardHeight][boardLength];

    public MainFrame(){
        super("Chinese checkers");
        //this.setLayout(new GridLayout(1,1));
        prepareBoard();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(360, 540, 540, 360 );
        this.setJMenuBar(new MenuBar(this));
        this.setVisible(true);
    }

    @Override
    public void sendInfo() {
        playerInteger = connector.getPlayerInt();
        Integer[][] temp = connector.getBoard();
        if(temp == null){
            return;
        }
        for(int i = 0; i < boardHeight; i++){
            for (int j = 0; j < boardLength; j++){
                if(buttonArray[i][j].getBackground() != Color.BLACK){
                    buttonArray[i][j].setBackground(translateColor(org.IgorNorbert.lista4.Color.toColor(temp[i][j])));
                }
            }
        }
        repaint();
    }

    @Override
    public void sendError() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof CheckerButton && playerTurn){
            CheckerButton temp = (CheckerButton) e.getSource();
            if(!isMovePrepared){
                coordinates[0] = temp.x;
                coordinates[1] = temp.y;
                isMovePrepared = true;
            }
            else if(isMovePrepared){
                coordinates[2] = temp.x;
                coordinates[3] = temp.y;
                makeMove();
                isMovePrepared = false;
            }
        }
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
        boardPanel.setLayout(new GridLayout(boardHeight, boardLength * 2));
        CheckerButton[][] temp = new CheckerButton[boardHeight][boardLength * 2];
        for( int i = 0; i < boardHeight; i++){
            for (int j = 0; j < boardLength * 2 - 1; j++){
                temp[i][j] = new CheckerButton();
                temp[i][j].x = j;
                temp[i][j].y = i;
                temp[i][j].setPreferredSize(new Dimension(15,30));
                temp[i][j].setBackground(java.awt.Color.BLACK);
                boardPanel.add(temp[i][j]);
            }
        }
        for (int i = 0; i < starSize; i++) {
            for (int j = 0; j <= i * 2; j += 2) {
                temp[i][boardLength - 1 - i + j].setBackground(java.awt.Color.white);
            }
        }
        for (int i = 4; i <= midHeight; i++) {
            for (int j = i - starSize;
                 j < boardLength * 2 - 1 - (i - starSize); j += 2) {
                temp[i][j].setBackground(java.awt.Color.white);
            }
        }
        for (int i = midHeight + 1; i < boardLength; i++) {
            for (int j = starSize - 1 - (i -  midHeight - 1);
                 j < boardLength * 2 - midLength + 1 + (i - midHeight + 1); j += 2){
                temp[i][j].setBackground(java.awt.Color.white);
            }
        }
        for (int i = boardLength; i < boardHeight; i++) {
            for (int j = midHeight + 1 + (i - boardLength);
                 j < boardHeight - 1 - (i - boardLength); j += 2) {
                temp[i][j].setBackground(Color.white);
            }
        }
    }
    public void connect(String address){
        connector = new ClientConnector(address, 7777, this);
        pool.execute(connector);
    }
    public static void main(String[] args){
        MainFrame frame = new MainFrame();
    }
    public void join(int lobby) {
        connector.join(lobby);
    }
    public void leave(){
        connector.leave();
    }
    public void skipTurn(){
        connector.skip();
    }
    private void makeMove(){
        connector.moveChecker(coordinates[0],coordinates[1],coordinates[2],coordinates[3]);
    }
    public void disconnect(){
        connector.disconnect();
    }
    public void setReady(boolean value){
        connector.setReady(value);
    }
    private Color translateColor(org.IgorNorbert.lista4.Color playerColor) {
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
