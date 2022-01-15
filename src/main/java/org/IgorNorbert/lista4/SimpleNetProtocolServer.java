package org.IgorNorbert.lista4;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SimpleNetProtocolServer implements NetProtocolServer{
    private Socket socket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    private NetPackage packageCache;
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
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        inputStream = new ObjectInputStream(this.socket.getInputStream());
    }

    @Override
    public void sendPackage(NetPackage netPackage) throws IOException{
        if(this.inputStream != null){
            outputStream.writeObject(netPackage);
            outputStream.flush();
  //          System.out.println("Sending package");
        }
    }

    @Override
    public NetPackage retrievePackage() {
        return this.packageCache;
    }

    @Override
    public boolean waitForPackage() throws IOException {
        boolean result = false;
            try {
                packageCache = (NetPackage) inputStream.readObject();
                result = true;
     //           System.out.println("Refreshing package");
            } catch (ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
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
