package org.Norbert.lista4;
import org.Norbert.lista4.Database.PlayerMove;
import org.Norbert.lista4.Database.SimpleLogger;
import org.Norbert.lista4.Game.*;
import org.Norbert.lista4.Game.Exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SimpleLoggerTest {
    @Test
    public void commitTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        SimpleLogger logger = (SimpleLogger) context.getBean("SimpleLogger");
        logger.addPlayer("test1", Color.BLUE);
        logger.addPlayer("test2", Color.CYAN);
        logger.insertCheckerMove(0,0,1,1,Color.BLUE);
        logger.insertCheckerMove(17,17,16,16, Color.CYAN);
        logger.insertSkip(Color.BLUE);
        logger.insertForfeit(Color.CYAN);
        logger.commitGame();
    }
}
