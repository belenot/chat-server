package com.belenot.chat;

import java.util.HashSet;
import java.util.Iterator;
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

    public boolean removeClientConnection(ClientConnection clientConnection, boolean close) {
	boolean removed = clientConnectionSet.remove(clientConnection);
	if (close && removed) clientConnection.close();
	return removed;
    }

    public Set<ClientConnection> removeClientConnectionSet(Client client, boolean close) {
	Set<ClientConnection> removedClientConnectionSet = new HashSet<>();
	for (ClientConnection clientConnection : clientConnectionSet.stream().filter( (cc) -> (cc.getClient().getId() == client.getId())).collect(Collectors.toSet())) {
	    removedClientConnectionSet.add(clientConnection);
	    if (close) clientConnection.close();
	}
	for (ClientConnection removedClientConnection : removedClientConnectionSet) {
	    clientConnectionSet.remove(removedClientConnection);
	}
	return removedClientConnectionSet;
    }
	    
    public Set<ClientConnection> removeAllClientConnections(boolean close) {
	Set<ClientConnection> removedClientConnectionSet = new HashSet<>();
	for (ClientConnection clientConnection : clientConnectionSet) {
	    removedClientConnectionSet.add(clientConnection);
	    if (close && !clientConnection.isClosed()) {
	    	clientConnection.close();
	    }
	}
	/**
	 * Why I didn't make remove of each connection directly in a cycle above? 
	 * Because of ConcurentModificationException.
	 * I remind: it means, that iterator of that set throws that exception,
	 * when it detects concurent invocation, or, like in that case,
	 * modifieng collection during iteration. 
	 * Maybe, I should to use iterator instead.
	*/
	for (ClientConnection removedClientConnection : removedClientConnectionSet) {
	    clientConnectionSet.remove(removedClientConnection);
	}
	return removedClientConnectionSet;
    }

    public Set<ClientConnection> removeClientConnectionSetByName(String name, boolean close) {
	Set<ClientConnection> removedClientConnectionSet = new HashSet<>();
	for (ClientConnection clientConnection : clientConnectionSet.stream().filter( (cc) -> (cc.getClient().getName().equals(name))).collect(Collectors.toSet())) {
	    removedClientConnectionSet.add(clientConnection);
	    if (close) clientConnection.close();
	}
	for (ClientConnection removedClientConnection : removedClientConnectionSet) {
	    clientConnectionSet.remove(removedClientConnection);
	}
	return removedClientConnectionSet;
    }
	
}
