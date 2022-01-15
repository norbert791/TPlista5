package org.IgorNorbert.lista4;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleNetProtocolClient implements NetProtocolClient {
    private Socket socket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    private Queue<NetPackage> packageCache = new LinkedList<>();
    @Override
    public void setSocket(Socket socket) throws IOException {
        if(this.socket != null){
          this.socket.close();
        }
        if(inputStream != null){
            inputStream.close();
            outputStream = null;
        }
        if(outputStream != null){
            outputStream.close();
            outputStream = null;
        }
        this.socket = socket;
        inputStream = new ObjectInputStream(this.socket.getInputStream());
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
    }

    @Override
    public void sendPackage(NetPackage netPackage) throws IOException{
        if(this.inputStream != null){
          //  System.out.println("Sending package");
            outputStream.writeObject(netPackage);
            outputStream.flush();
       //     System.out.println("Waiting for response");
            try {
                packageCache.add((NetPackage) inputStream.readObject());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public NetPackage retrievePackage() {
        return this.packageCache.poll();
    }

    @Override
    public boolean isReady() throws IOException{
        return !packageCache.isEmpty();
    }

    @Override
    public void close() throws IOException {
        if(this.socket != null){
            socket.close();
        }
        if(inputStream != null){
            inputStream.close();
            inputStream = null;
        }
        if(outputStream != null){
            outputStream.close();
            outputStream = null;
        }
    }
}
