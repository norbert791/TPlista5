package org.Norbert.lista5.Server;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
public class UserVerification {
    @Pointcut("execution(NetPackage org.Norbert.lista5.*(..)) " +
            "&& !execution(NetPackage org.Norbert.lista5.Server.Player.register*(..))" +
            "&& !execution(NetPackage org.Norbert.lista5.Server.Player.sign*(..))")
    public void operation() {

    }
    @After("operation()")
    public void verify(JoinPoint jp) {

    }
}
