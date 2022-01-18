package org.IgorNorbert.lista4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Implementation of NetProtocolServer.
 */
public class SimpleNetProtocolServer implements NetProtocolServer {
    /**
     * Socket used for communication with client.
     */
    private Socket socket = null;
    /**
     * input stream for NetPackages.
     */
    private ObjectInputStream inputStream = null;
    /**
     * OutputStream for NetPackages.
     */
    private ObjectOutputStream outputStream = null;
    /**
     * next netPackage.
     */
    private NetPackage packageCache;

    /**
     * Safely sets socket.
     * @param socket socket for connection with client
     * @throws IOException thrown when some IO issues occurs
     */
    @Override
    public void setSocket(final Socket socket) throws IOException {
        if (this.socket != null) {
            this.socket.close();
        }
        if (inputStream != null) {
            inputStream.close();
            outputStream = null;
        }
        if (outputStream != null) {
            outputStream.close();
            outputStream = null;
        }
        this.socket = socket;
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        inputStream = new ObjectInputStream(this.socket.getInputStream());
    }

    /**
     * Sends netPackage.
     * @param netPackage package that is to be sent to the client.
     * @throws IOException thrown when some IO issues occurs
     */
    @Override
    public void sendPackage(final NetPackage netPackage) throws IOException {
        if (this.inputStream != null) {
            outputStream.writeObject(netPackage);
            outputStream.flush();
        }
    }

    /**
     * Retrieves the netPackage from cache.
     * @return the package from cache
     */
    @Override
    public NetPackage retrievePackage() {
        return this.packageCache;
    }

    /**
     * Locks on inputStream until Package is received.
     * @return true iff package was properly received
     * @throws IOException thrown iff some IO issues occurred
     */
    @Override
    public boolean waitForPackage() throws IOException {
        boolean result = false;
            try {
                packageCache = (NetPackage) inputStream.readObject();
                result = true;
            } catch (ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
            }
        return result;
    }

    /**
     * Safely close both streams.
     * @throws IOException iff some issue occurred
     */
    @Override
    public void close() throws IOException {
        if (this.socket != null) {
            socket.close();
        }
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
        if (outputStream != null) {
            outputStream.close();
            outputStream = null;
        }
    }
}
