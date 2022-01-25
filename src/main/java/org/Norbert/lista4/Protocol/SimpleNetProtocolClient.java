package org.Norbert.lista4.Protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleNetProtocolClient implements NetProtocolClient {
    /**
     * Socket used for communication with server.
     */
    private Socket socket = null;
    /**
     * InputStream for communication.
     */
    private ObjectInputStream inputStream = null;
    /**
     * OutputStream for communication.
     */
    private ObjectOutputStream outputStream = null;
    /**
     * Cache for received files.
     */
    private Queue<NetPackage> packageCache = new LinkedList<>();

    /**
     * Sets socket for protocol.
     * @param socket socket for connection with client
     * @throws IOException iff IO exception occured
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
        inputStream = new ObjectInputStream(this.socket.getInputStream());
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
    }

    /**
     * Sends package to server.
     * @param netPackage package that is to be sent to the client.
     * @throws IOException thrown if Io exception occured
     */
    @Override
    public void sendPackage(final NetPackage netPackage) throws IOException {
        if (this.inputStream != null) {
            outputStream.writeObject(netPackage);
            outputStream.flush();
            try {
                packageCache.add((NetPackage) inputStream.readObject());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get NetPackage from cache.
     * @return NetPackage received from stream
     */
    @Override
    public NetPackage retrievePackage() {
        return this.packageCache.poll();
    }

    /**
     * Checks if package cache is empty.
     * @return true iff a NetPackage can be read
     * @throws IOException not thrown
     */
    @Override
    public boolean isReady() throws IOException {
        return !packageCache.isEmpty();
    }

    /**
     * Closes session.
     * @throws IOException of problems occurred during fileStreams' closing
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
