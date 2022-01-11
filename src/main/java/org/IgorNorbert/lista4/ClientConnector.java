package org.IgorNorbert.lista4;
//TODO: The protocol could improved by queueing server requests, each request would be uniquely identified,
//TODO: and then handling each response independently
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientConnector implements Runnable{
    private Socket socket;
    private volatile Integer[][] arrayBuffer;
    private volatile Integer playerInt = null;
    private volatile Package nextCommand;
    private final ClientReceiver receiver;
    private String errorMessage = null;
    private Object returnValue = null;
    private boolean connected = true;
    private volatile Integer playerMove;
    public ClientConnector(String address, int port,
                           final ClientReceiver receiver) {
        this.receiver = receiver;
        try {
            socket = new Socket(address, port);
           /* InetAddress inetAddress= InetAddress.getByName(address);
            SocketAddress socketAddress=new InetSocketAddress(inetAddress, port);
            socket.connect(socketAddress);*/
        } catch (IOException e) {
            errorMessage = "Connection failed";
            receiver.sendError();
            e.printStackTrace();
            socket = null;
        }
    }

    @Override
    public void run() {
        final ObjectOutputStream outputStream;
        final ObjectInputStream inputStream;
        if(socket == null){
            return;
        }
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            errorMessage = "Connection failed";
            receiver.sendError();
            return;
        }
        while(connected) {
            try {
                if (nextCommand != null) {
                    outputStream.writeObject(nextCommand);
                    nextCommand = null;
                }
                if(inputStream.available() > 0){
                    decodePackage((Package) inputStream.readObject());
                }
            } catch (IOException | ClassNotFoundException |
                    ClassCastException e) {
                e.printStackTrace();
            }
        }
    }
    private synchronized void decodePackage(Package result){
        switch (result) {
            case ERROR -> {
                this.errorMessage = (String) result.getArgument();
                receiver.sendError();
            }
            case CURRENT, MOVE, LOBBIES -> {
                returnValue = result.getArgument();
                receiver.sendInfo();
            }
            case BOARD -> {
                arrayBuffer = (Integer[][]) result.getArgument();
                receiver.sendInfo();
            }
            case PLAYERINT -> {
                playerInt = (Integer) result.getArgument();
                receiver.sendInfo();
            }
        }
    }

    public synchronized void moveChecker(int oldX, int oldY, int newX, int newY){
        Package temp = Package.MOVE;
        temp.setArgument(new int[]{oldX, oldY, newX, newY});
        nextCommand = temp;
    }
    public synchronized void setReady(boolean value){
        Package temp = Package.READY;
        temp.setArgument(value);
        nextCommand = temp;
    }
    public synchronized void leave(){
        nextCommand = Package.LEAVE;
    }
    public synchronized void join(int number){
        Package temp = Package.JOIN;
        temp.setArgument(number);
        nextCommand = temp;
    }

    public Integer getPlayerInt() {
        return playerInt;
    }
    public void fetchPlayerInt(){
        nextCommand = Package.PLAYERINT;
    }
    public Integer[][] getBoard(){
        return arrayBuffer;
    }
    public void fetchBoard(){
        nextCommand = Package.BOARD;
    }
    public void skip(){
        nextCommand = Package.SKIP;
    }
    public void currentPlayer(){
        nextCommand = Package.CURRENT;
    }
    private void sendReturnNotification(){
        receiver.sendInfo();
    }
    private void sendErrorNotification(){
        receiver.sendError();
    }
    public String getError(){
        return errorMessage;
    }
    public Object getReturnValue(){
        return returnValue;
    }
    public void disconnect(){
        nextCommand = Package.DISCONNECT;
        this.connected = false;
    }
}
