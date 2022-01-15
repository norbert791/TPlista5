package org.IgorNorbert.lista4;

public class SimpleNetProtocolFactory implements NetProtocolFactory{

    @Override
    public NetProtocolServer getServerSide() {
        return new SimpleNetProtocolServer();
    }

    @Override
    public NetProtocolClient getClientSide() {
        return new SimpleNetProtocolClient();
    }
}
