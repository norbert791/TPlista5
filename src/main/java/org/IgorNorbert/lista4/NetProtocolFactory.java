package org.IgorNorbert.lista4;

/**
 * Abstract factory used for creating matching
 * communication interface implementations.
 */
public interface NetProtocolFactory {
    /**
     * Get interface used for communication with client.
     * @return interface for communication with client
     */
    NetProtocolServer getServerSide();

    /**
     * Get interface used for communication with server.
     * @return interface for communication with client
     */
    NetProtocolClient getClientSide();
}
