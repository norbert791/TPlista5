package org.Norbert.lista5.Protocol;

import java.io.IOException;
import java.net.Socket;

/**
 * Interface for protocol managing communication with server.
 */
public interface NetProtocolClient {
    /**
     * sets socket for this session and opens the connection.
     * @param socket socket for connection with client
     */
    void setSocket(Socket socket) throws IOException;

    /**
     * sends Package.
     * @param netPackage package that is to be sent to the client.
     */
    void sendPackage(NetPackage netPackage) throws IOException;

    /**
     * retrieves latest Package.
     * @return NetPackage containing response from the server
     */
    NetPackage retrievePackage();

    /**
     * Reads stream from socket if possible.
     * @return true iff new package was received
     */
    boolean isReady() throws IOException;

    /**
     * Closes the connection.
     * @throws IOException
     */
    void close() throws IOException;
}
