package org.Norbert.lista5.Database.HibernateImplementation;

import org.Norbert.lista5.Database.GameLogger;
import org.Norbert.lista5.Game.Color;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class MoveTracker {
    private final GameLogger logger = new HibernateLogger();
    @AfterReturning(
        pointcut = "execution(* org.Norbert.lista5.Game.*Master.moveChecker(..))")
    public void storeMove(JoinPoint jp) {
        Object[] temp = jp.getArgs();
        logger.insertCheckerMove((int) temp[0], (int) temp[1], (int) temp[2], (int) temp[3], (Color) temp[4]);
    }
    @AfterReturning(
            pointcut = "execution(* org.Norbert.lista5.Game.*Master.skipTurn(..))")
    public void storeSkip(JoinPoint jp) {
            logger.insertSkip((Color) jp.getArgs()[0]);
    }
    @AfterReturning(
            pointcut = "execution(* org.Norbert.lista5.Game.*Master.forfeit(..))")
    public void storeForfeit(JoinPoint jp) {
            logger.insertForfeit((Color) jp.getArgs()[0] );
    }

}
