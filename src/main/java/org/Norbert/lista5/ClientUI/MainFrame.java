package org.Norbert.lista5.ClientUI;

import org.Norbert.lista5.ClientLogic.ClientLogic;
import org.Norbert.lista5.ClientUI.MatchHistory.HistoryFrame;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameDescriptionRecord;
import org.Norbert.lista5.Database.jdbcTemplateImplementation.GameRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.util.Map;

/**
 * Main window if the gui.
 */
public class MainFrame extends JFrame implements UserInterface {
    private MenuBar menuBar;
    private ClientLogic clientLogic;
    private JPanel gamePanel;
    private GamePanel boardPanel;
    private PlayerListPanel playerListPanel;
    private boolean isMovePrepared = false;
    private boolean[][] boardMask;
    private int[] coordinates = new int[4];
    private JScrollPane lobbies;
    private org.Norbert.lista5.Game.Color playerColor;
    private org.Norbert.lista5.Game.Color currentPlayerColor;
    private HistoryFrame historyFrame;

    /**
     * Constructor.
     * @param clientLogic reference to clientLogic
     */
    public MainFrame(final ClientLogic clientLogic) {
        super("Chinese checkers");
        this.clientLogic = clientLogic;
        //prepareBoard();
        this.setLayout(new GridLayout(1, 1));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(360, 540, 540, 360);
        this.menuBar = new MenuBar(this);
        this.setJMenuBar(menuBar);
        this.setVisible(true);
    }
    /**
     * Prints the board onto the gui.
     * @param board Color array representing current state of the game
     */
    @Override
    public void printBoard(final org.Norbert.lista5.Game.Color[][] board) {
        if (lobbies != null) {
            this.remove(lobbies);
            lobbies = null;
        }
        if (this.gamePanel == null) {
            gamePanel = new JPanel();
            gamePanel.setLayout(new GridLayout(1, 2));
            boardPanel = new GamePanel(this, boardMask);
            playerListPanel = new PlayerListPanel(this);
            gamePanel.add(boardPanel);
            gamePanel.add(playerListPanel);
            this.add(gamePanel);
        }
        this.boardPanel.updateButtons(board);
        revalidate();
    }

    /**
     * Sets the color icon of the current player.
     * @param color color representing player currently making their move
     */
    @Override
    public void setCurrentPlayer(final org.Norbert.lista5.Game.Color color) {
        this.currentPlayerColor = color;
        this.menuBar.setCurrentColor(translateColor(color));
    }

    /**
     * Sets the color icon of the player.
     * @param color color representing the client's color
     */
    @Override
    public void setPlayerColor(final org.Norbert.lista5.Game.Color color) {
        this.playerColor = color;
        this.menuBar.setPlayerColor(translateColor(color));
    }

    /**
     * Prints error popUp.
     * @param errorMessage Message that is to be displayed to client
     */
    @Override
    public void printError(final String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }

    /**
     * Does nothing in this implementation.
     * @param nextMove insert true iff client may make another move
     */
    @Override
    public void nextMove(final boolean nextMove) {

    }

    /**
     * Prints the list of lobbies onto the gui.
     * @param lobbies Array of lobbies where index is lobby number
     *                and value is number of players in lobby.
     */
    @Override
    public void printLobbyList(final int[] lobbies) {
        if (this.gamePanel != null) {
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

    /**
     * Prints player onto the gui.
     * @param playerMap map containing list of players and their names
     */
    @Override
    public void printPlayers(
            final Map<String, org.Norbert.lista5.Game.Color> playerMap) {
        if (playerMap == null) {
            return;
        }
        if (this.playerListPanel != null) {
            playerListPanel.updatePlayerList(playerMap);
        }
        revalidate();
    }

    /**
     * Prints marks players that finished the game.
     * @param order order in which the players have one
     */
    @Override
    public void updateVictors(final String[] order) {
        if (order == null) {
            return;
        }
        if (this.playerListPanel != null) {
            this.playerListPanel.updateWinOrder(order);
        }
        revalidate();
    }

    @Override
    public void printLobby() {
        if (this.lobbies != null) {
            this.remove(lobbies);
        }
        if (this.gamePanel == null) {
            this.gamePanel = new JPanel();
            gamePanel = new JPanel();
            gamePanel.setLayout(new GridLayout(1, 2));
            boardPanel = new GamePanel(this, boardMask);
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
        if (this.gamePanel != null) {
            remove(gamePanel);
            gamePanel = null;
        }
        if (this.boardPanel != null) {
            remove(boardPanel);
            boardPanel = null;
        }
        if (this.lobbies != null) {
            remove(lobbies);
            lobbies = null;
        }
        revalidate();
    }

    @Override
    public void setMask(final boolean[][] boardMask) {
        this.boardMask = boardMask;
    }

    /**
     * Prints list of all matches that user has played.
     *
     * @param records array of records with data about the games
     */
    @Override
    public void printHistory(GameDescriptionRecord[] records) {
        historyFrame = new HistoryFrame(this, records);
    }

    /**
     * Print gameRecord
     *
     * @param record gameRecord
     */
    @Override
    public void printGameRecord(GameRecord record) {
        if (historyFrame != null) {
            historyFrame.printGame(record);
        }
    }

    /**
     * Get color of the player currently making their move.
     * @return Color of the current player
     */
    public org.Norbert.lista5.Game.Color getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    /**
     * Get color of the client's player.
     * @return Color of the Client player
     */
    public org.Norbert.lista5.Game.Color getPlayerColor() {
        return playerColor;
    }

    /**
     * Connect to server using ClientLogic.
     * @param address address of the server
     */
    public void connect(String address) {

        clientLogic.connect(address);
    }

    /**
     * Join lobby of the server.
     * @param lobby index of lobby
     */
    public void join(int lobby) {
        clientLogic.join(lobby);
    }

    /**
     * leave the lobby.
     */
    public void leave() {

        clientLogic.leave();
    }

    /**
     * Skip your turn.
     */
    public void skipTurn() {

        clientLogic.skip();
    }

    /**
     * Send move request to server.
     * @param coordinates array of representing: oldX, oldY, newX, newY
     */
    protected void makeMove(int[] coordinates) {
        clientLogic.moveChecker(coordinates[0],
                coordinates[1], coordinates[2], coordinates[3]);
    }

    /**
     * Disconnect from server.
     */
    public void disconnect() {
        clientLogic.disconnect();

    }

    /**
     * Send ready request to server.
     * @param value true iff you are ready
     */
    public void setReady(boolean value) {

        clientLogic.setReady(value);
    }

    /**
     * Translates org.IgorNorbert.lista4.Color playerColor to AWT.Color.
     * @param playerColor color to translate
     * @return translated color
     */
    protected Color translateColor(org.Norbert.lista5.Game.Color playerColor) {
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

    public void showHistory() {
        clientLogic.fetchHistory();
    }

    public void fetchGame(int gameId) {
        clientLogic.fetchGame(gameId);
    }
}
