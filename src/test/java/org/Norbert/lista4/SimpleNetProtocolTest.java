package org.Norbert.lista4;

import org.Norbert.lista4.Protocol.NetPackage;
import org.Norbert.lista4.Protocol.SimpleNetProtocolClient;
import org.Norbert.lista4.Protocol.SimpleNetProtocolServer;
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
                NetPackage temp1;
                server.waitForPackage();
                temp1 = server.retrievePackage();
                Assertions.assertEquals((String) temp1.getArgument(), "5");
                temp1.type = NetPackage.Type.JOIN;
                temp1.setArgument(5);
                server.sendPackage(temp1);
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
                temp2 = new NetPackage();
                temp2.type = NetPackage.Type.ERROR;
                temp2.setArgument("5");
                client.sendPackage(temp2);
                client.isReady();
                temp2 = client.retrievePackage();
                Assertions.assertEquals((int) temp2.getArgument(), 5);

            } catch (IOException e) {
                e.printStackTrace();
                fail("Thread 2 fail");
            }
            System.out.println("exited normally");
        }).start();
    }
}
