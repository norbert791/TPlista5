package org.IgorNorbert.lista4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SimpleNetProtocol implements NetProtocol {
    private Socket socket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    private NetPackage packageCache;
    @Override
    public void setSocket(Socket socket) throws IOException {
        System.out.println("setSocket1");
        if(this.socket != null){
          this.socket.close();
        }
        System.out.println("setSocket2");
        if(inputStream != null){
            inputStream.close();
            outputStream = null;
        }
        System.out.println("setSocket3");
        if(outputStream != null){
            outputStream.close();
            outputStream = null;
        }
        System.out.println("setSocket4");
        this.socket = socket;
        System.out.println("setSocket5");
        inputStream = new ObjectInputStream(this.socket.getInputStream());
        System.out.println("setSocket6");
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        System.out.println("setSocket7");
    }

    @Override
    public void sendPackage(NetPackage netPackage) throws IOException{
        if(this.inputStream != null){
            outputStream.writeObject(netPackage);
            System.out.println("Sending package");
        }
    }

    @Override
    public NetPackage retrievePackage() {
        return this.packageCache;
    }

    @Override
    public boolean refresh() throws IOException{
        boolean result = false;
        if(this.inputStream.available() > 0) {
            try {
                packageCache = (NetPackage) inputStream.readObject();
                result = true;
                System.out.println("Refreshing package");
            } catch (ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
            }
        }
        return result;
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
