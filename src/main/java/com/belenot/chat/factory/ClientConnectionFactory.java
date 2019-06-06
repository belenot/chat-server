package com.belenot.chat.factory;

import java.net.Socket;

import com.belenot.chat.ClientConnection;
import com.belenot.chat.domain.Client;

public interface ClientConnectionFactory {
    public ClientConnection newClientConnection(Client client, Socket socket);
}
