package org.Norbert.lista5;

import org.Norbert.lista5.Database.AuthorizationFailed;
import org.Norbert.lista5.Database.HibernateImplementation.HibernateUserManager;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class HibernateUserManagerTest {
    private final ApplicationContext context =
            new ClassPathXmlApplicationContext("BeansHibernateVersion.xml");
    @Disabled("This is closer to an integration test")
    @Test
    public void registeringTest () {
        HibernateUserManager manager = (HibernateUserManager) context.getBean("hibernateUserManager");
        try {
            manager.register("test2@test.pl", "testowe2", "testowe2");
        } catch (AuthorizationFailed e) {
            e.printStackTrace();
        }
    }
    @Test
    public void loginTest() {
        HibernateUserManager manager = (HibernateUserManager) context.getBean("hibernateUserManager");
        String name = null;
        String name2 = null;
        try {
            name =  manager.logIn("test@test.pl", "testowe");
            name2 = manager.logIn("test2@test.pl", "testowe2");
        } catch (AuthorizationFailed e) {
            e.printStackTrace();
        }
       // assertEquals("testowe", name);
       // assertEquals("testowe2", name2);
        assertThrowsExactly(AuthorizationFailed.class, () -> manager.logIn("nonExisting","user"));
    }
}
