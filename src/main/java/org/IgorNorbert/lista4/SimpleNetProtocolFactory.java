package org.IgorNorbert.lista4;

/**
 * Implementation used for managing SimpleProtocol.
 */
public class SimpleNetProtocolFactory implements NetProtocolFactory {
    /**
     * returns SimplenessProtocolServer.
     * @return SimpleNetProtocolServer instance
     */
    @Override
    public NetProtocolServer getServerSide() {
        return new SimpleNetProtocolServer();
    }
    /**
     * returns SimplenessProtocolClient.
     * @return SimpleNetProtocolClient instance
     */
    @Override
    public NetProtocolClient getClientSide() {
        return new SimpleNetProtocolClient();
    }
}
