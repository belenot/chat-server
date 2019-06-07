package com.belenot.chat;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.belenot.chat.dao.MessageDao;
import com.belenot.chat.domain.Client;
import com.belenot.chat.domain.Message;

public class Publisher {
    private MessageDao messageDao;
    private Set<ClientConnection> clientConnectionSet = new HashSet<>();

    public void setMessageDao(MessageDao messageDao) { this.messageDao = messageDao; }

    public void publish(Client client, String text) {
	Message message = messageDao.addMessage(client, text);
	for (ClientConnection clientConnection : clientConnectionSet) {
	    if (clientConnection.isClosed()) {
		clientConnectionSet.remove(clientConnection);
		continue;
	    }
	    clientConnection.receive(message);
	}
	
    }

    public void addClientConnection(ClientConnection clientConnection) { clientConnectionSet.add(clientConnection); }

    public void closeAll() {
	for (ClientConnection clientConnection : clientConnectionSet.stream().filter( (c) -> (!c.isClosed())).collect(Collectors.toSet())) {
	    clientConnection.close();
	}
    }
}
