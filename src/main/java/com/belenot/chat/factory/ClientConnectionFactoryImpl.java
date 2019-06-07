package com.belenot.chat.factory;

import java.net.Socket;
import java.util.logging.Logger;

import com.belenot.chat.ClientConnection;
import com.belenot.chat.Publisher;
import com.belenot.chat.domain.Client;

public class ClientConnectionFactoryImpl implements ClientConnectionFactory {
    private Publisher publisher;
    private Logger logger;

    public void setPublisher(Publisher publisher) { this.publisher = publisher; }
    public void setLogger(Logger logger) { this.logger = logger; }
    
    public ClientConnection newClientConnection(Client client, Socket socket) {
	//try { socket.setSoTimeout(soTimeout); } catch (Exception exc) { }
	ClientConnection clientConnection = new ClientConnection();
	clientConnection.setClient(client);
	clientConnection.setSocket(socket);
	clientConnection.setLogger(logger);
	publisher.addClientConnection(clientConnection);
	clientConnection.setPublisher(publisher);
	return clientConnection;
    }

    
}
