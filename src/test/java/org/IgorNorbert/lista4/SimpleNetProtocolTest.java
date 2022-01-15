package org.IgorNorbert.lista4;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SimpleNetProtocolTest {
    @Test
    public void creationTest() {
        new Thread(() -> {
            try {
                ServerSocket tempSocket = new ServerSocket(3333);
                Socket serverSocket = tempSocket.accept();
                SimpleNetProtocolServer server = new SimpleNetProtocolServer();
                server.setSocket(serverSocket);
                NetPackage temp1 = NetPackage.READY;
                temp1.setArgument(5);
                server.sendPackage(temp1);
                server.refresh();
                temp1 = server.retrievePackage();
                Assertions.assertEquals((String) temp1.getArgument(), "5");
            } catch (IOException e) {
                e.printStackTrace();
                fail("Thread 1 fail");
            }
            System.out.println("Exited normally");
        }).start();
        new Thread(() -> {
            try {
                Socket clientSocket = new Socket("localhost", 3333);
                SimpleNetProtocolClient client = new SimpleNetProtocolClient();
                client.setSocket(clientSocket);
                NetPackage temp2;
                client.refresh();
                temp2 = client.retrievePackage();
                Assertions.assertEquals((int) temp2.getArgument(), 5);
                temp2 = NetPackage.MOVE;
                temp2.setArgument("5");
                client.sendPackage(temp2);
            } catch (IOException e) {
                e.printStackTrace();
                fail("Thread 2 fail");
            }
            System.out.println("exited normally");
        }).start();
    }
}
