package com.belenot.chat.dao;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.belenot.chat.domain.Client;

public class ClientDao {
    private Set<Client> clientSet = new HashSet<>();
    private int currentId = 0;

    public Client getClient(int id) {
	Client client = clientSet.stream().filter( (c) -> (c.getId() == id)).findFirst().get();
	return client;
    }

    public Set<Client> getClients(String name) {
        Set<Client> clients = clientSet.stream().filter( (c) -> (c.getName().equals(name))).collect(Collectors.toSet());
	return clients;
    }

    public Client getClient(String name) {
	for (Client client : getClients(name)) { return client; }
	return null;
    }

    public Client addClient(String name) {
	Client client = new Client();
	client.setId(++currentId);
	client.setName(name);
	clientSet.add(client);
	return client;
    }
	
}
