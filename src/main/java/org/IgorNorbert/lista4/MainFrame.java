package org.IgorNorbert.lista4;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class MainFrame extends JFrame implements UserInterface, ActionListener {
    private MenuBar menuBar;
    private LayoutManager layout;
    private ClientLogic clientLogic;
    private JPanel gamePanel;
    private GamePanel boardPanel;
    private PlayerListPanel playerListPanel;
    private boolean isMovePrepared = false;
 //   private int boardHeight = 17;
 //   private int boardLength = 13;
    private int[] coordinates = new int[4];
    private JScrollPane lobbies;
    private org.IgorNorbert.lista4.Color playerColor;
    private org.IgorNorbert.lista4.Color currentPlayerColor;
//    private CheckerButton[][] buttonArray = new CheckerButton[boardHeight][boardLength];

    public MainFrame(ClientLogic clientLogic) {
        super("Chinese checkers");
        this.clientLogic = clientLogic;
        //prepareBoard();
        this.setLayout(new GridLayout(1,1));
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
            else {
                coordinates[2] = temp.x;
                coordinates[3] = temp.y;
                if(temp.x == coordinates[0] && temp.y == coordinates[1]){
                    isMovePrepared = false;
                    return;
                }
                makeMove(coordinates);
                isMovePrepared = false;
            }
        }
    }

    @Override
    public void printBoard(org.IgorNorbert.lista4.Color[][] board) {
        if (lobbies != null){
            this.remove(lobbies);
            lobbies = null;
        }
        if(this.gamePanel == null) {
            gamePanel = new JPanel();
            gamePanel.setLayout(new GridLayout(1,2));
            boardPanel = new GamePanel(this);
            playerListPanel = new PlayerListPanel(this);
            gamePanel.add(boardPanel);
            gamePanel.add(playerListPanel);
            this.add(gamePanel);
        }
        this.boardPanel.updateButtons(board);
        revalidate();
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
        if(this.gamePanel != null) {
            remove(this.gamePanel);
            this.gamePanel = null;
            this.boardPanel = null;
            this.playerListPanel = null;
        }
        if (this.lobbies == null) {
            this.lobbies = new JScrollPane(new LobbyPanel(this, lobbies));
            this.add(this.lobbies);
        }
        revalidate();
    }

    @Override
    public void printPlayers(Map<String, org.IgorNorbert.lista4.Color> playerMap) {
        if(playerMap == null){
            return;
        }
        if (this.playerListPanel != null) {
            playerListPanel.updatePlayerList(playerMap);
        }
        revalidate();
    }

    @Override
    public void updateVictors(String[] order) {
        if(order == null){
            return;
        }
        if(this.playerListPanel != null) {
            this.playerListPanel.updateWinOrder(order);
        }
        revalidate();
    }

    @Override
    public void printLobby() {
        if(this.lobbies != null) {
            this.remove(lobbies);
        }
        if(this.gamePanel == null) {
            this.gamePanel = new JPanel();
            gamePanel = new JPanel();
            gamePanel.setLayout(new GridLayout(1,2));
            boardPanel = new GamePanel(this);
            playerListPanel = new PlayerListPanel(this);
            gamePanel.add(boardPanel);
            gamePanel.add(playerListPanel);
            this.add(gamePanel);
        }
        revalidate();
    }

    @Override
    public void printStartScreen() {
        removeAll();
        this.add(menuBar);
        this.gamePanel = null;
        this.boardPanel = null;
        revalidate();
    }

    private class CheckerButton extends JButton{
        public int x;
        public int y;
    }

    public org.IgorNorbert.lista4.Color getCurrentPlayerColor(){
        return currentPlayerColor;
    }
    public org.IgorNorbert.lista4.Color getPlayerColor(){
        return playerColor;
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
    protected void makeMove(int[] coordinates){
        clientLogic.moveChecker(coordinates[0],coordinates[1],coordinates[2],coordinates[3]);
    }
    public void disconnect(){
        clientLogic.disconnect();
    }
    public void setReady(boolean value){
        clientLogic.setReady(value);
    }
    protected Color translateColor(org.IgorNorbert.lista4.Color playerColor) {
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
