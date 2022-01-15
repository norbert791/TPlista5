package org.IgorNorbert.lista4;

public interface NetProtocolFactory {
    NetProtocolServer getServerSide();
    NetProtocolClient getClientSide();
}
