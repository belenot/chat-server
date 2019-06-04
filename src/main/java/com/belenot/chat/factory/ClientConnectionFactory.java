package com.belenot.chat.factory;

import java.net.Socket;

import com.belenot.chat.ClientConnection;
import com.belenot.chat.Publisher;
import com.belenot.chat.domain.Client;

public class ClientConnectionFactory {
    private Publisher publisher;

    public void setPublisher(Publisher publisher) { this.publisher = publisher; }
    
    public ClientConnection newClientConnection(Client client, Socket socket) {
	ClientConnection clientConnection = newClientConnection(client, socket);
	publisher.addClientConnection(clientConnection);
	clientConnection.setPublisher(publisher);
	return clientConnection;
    }
}
