package com.belenot.chat.dao;

import java.util.HashMap;
import java.util.Map;

import com.belenot.chat.domain.Client;

public class ClientDao {
    private Map<Client, String> clients = new HashMap<>();
    private int currentId = 0;

    public Client getClient(String name, String password) {
	for (Map.Entry<Client, String> entry : clients.entrySet()) {
	    if (entry.getKey().getName().equals(name) && entry.getValue().equals(password))
		return entry.getKey();
	}
	return null;
    }

    public Client addClient(String name) {
	return addClient(name, name);
    }
	
    
    public Client addClient(String name, String password) {
	Client client = new Client();
	client.setId(++currentId);
	client.setName(name);
	clients.put(client, password);
	return client;
    }
	
}
