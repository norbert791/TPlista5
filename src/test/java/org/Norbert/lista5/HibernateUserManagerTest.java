package org.Norbert.lista5;

import org.Norbert.lista5.Database.AuthorizationFailed;
import org.Norbert.lista5.Database.HibernateImplementation.HibernateUserManager;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateUserManagerTest {
    private final ApplicationContext context =
            new ClassPathXmlApplicationContext("BeansHibernateVersion.xml");
    @Disabled("This is closer to an integration test")
    @Test
    public void registeringTest () {
        HibernateUserManager manager = (HibernateUserManager) context.getBean("userManager");
        assertTrue(manager.register("test2@test.pl", "testowe2", "testowe2"));
    }
    @Test
    public void loginTest() {
        HibernateUserManager manager = (HibernateUserManager) context.getBean("userManager");
        boolean name = false;
        boolean name2 = false;
        assertFalse(manager.logIn("testBlad@test.pl", "testowe2"));
        assertThrowsExactly(AuthorizationFailed.class, manager::getName);
        assertTrue(manager.logIn("test@test.pl", "testowe"));
    }
}
