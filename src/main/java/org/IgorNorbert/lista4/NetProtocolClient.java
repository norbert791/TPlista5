package org.IgorNorbert.lista4;

import java.io.IOException;
import java.net.Socket;

public interface NetProtocolClient {
    /**
     * sets socket for this session and opens the connection.
     * @param socket socket for connection with client
     */
    void setSocket(Socket socket) throws IOException;

    /**
     * sends Package
     * @param netPackage package that is to be sent to the client.
     */
    void sendPackage(NetPackage netPackage) throws IOException;

    /**
     * retrieves latest Package.
     */
    NetPackage retrievePackage();

    /**
     * Reads stream from socket if possible.
     * @return true iff new package was received
     */
    boolean refresh() throws IOException;

    /**
     * Closes the connection.
     * @throws IOException
     */
    void close() throws IOException;
}
