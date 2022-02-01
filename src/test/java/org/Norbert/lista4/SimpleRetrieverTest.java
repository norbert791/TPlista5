package org.Norbert.lista4;

import org.Norbert.lista4.Database.GameDescriptionRecord;
import org.Norbert.lista4.Database.GameRecord;
import org.Norbert.lista4.Database.PlayerMove;
import org.Norbert.lista4.Database.SimpleRetriever;
import org.Norbert.lista4.Game.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SimpleRetrieverTest {
    @Test
    public void listOfGamesRetrievalTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        SimpleRetriever retriever = (SimpleRetriever) context.getBean("SimpleRetriever");
        GameDescriptionRecord[] temp = retriever.fetchGameList("test1");
        for (GameDescriptionRecord game : temp) {
            System.out.print(game.gameId() +  " " + game.gameType() + " " + game.date() + "\n");
        }
    }

    @Test
    public void gameRetrievalTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        SimpleRetriever retriever = (SimpleRetriever) context.getBean("SimpleRetriever");
        GameRecord temp = retriever.fetchHistory(1);
        assertEquals(temp.boardShape().length, temp.initialBoard().length);
        for (int i = 0; i < temp.boardShape().length; i++) {
            assertEquals(temp.boardShape()[i].length,
                    temp.initialBoard()[i].length);
        }
    /*
        System.out.println("Board shape");
        for (boolean[] booleanAr : temp.boardShape()) {
            for (boolean booleanTemp : booleanAr) {
                System.out.print(booleanTemp ? "1" : "0");
            }
            System.out.print("\n");
        }
        System.out.println("Initial board");
        for (Color[] colAr : temp.initialBoard()) {
            for (Color col : colAr) {
                System.out.print(col == null ? "0" : "1");
            }
            System.out.print("\n");
        }
        System.out.println("PrintMoves");
        for (PlayerMove move : temp.moves()) {
            System.out.print(move.moveType().toString() + " ");
            if(move.moveType() == PlayerMove.MoveType.CHECKER_MOVE) {
                System.out.print(move.checkerMove().oldX() + " "
                        + move.checkerMove().oldY() + " "
                        + move.checkerMove().newX() + " "
                        + move.checkerMove().newY());
            }
            System.out.print("\n");
        }*/
    }

    @Test
    public void reflectionTest() {
        assertDoesNotThrow(() -> {Class tempClass = Class.forName(
                "org.Norbert.lista4.Game.SimpleMaster");});
    }
}
