package org.Norbert.lista4.Database;

public interface HistoryRetriever {
    /**
     * Get list of games
     * @param nick_name nick of the player whose games should be fetched
     * @return Array of games available for retrieval
     */
    GameDescriptionRecord[] fetchGameList(String nick_name);

    /**
     * Fetched moves of selected game
     * @param game_id id of games whose moves should be retrieved
     * @return Array of Player moves for reconstructing game
     */
    GameRecord fetchHistory(int game_id);
}
