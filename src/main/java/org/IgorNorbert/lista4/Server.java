package org.IgorNorbert.lista4;

import java.util.ArrayList;
import java.util.Collection;

public class Server {
    private Collection<Lobby> lobbyList = new ArrayList<>();
    public Lobby[] getLobbyList(){
        return lobbyList.toArray(new Lobby[0]);
    }
}
