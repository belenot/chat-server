package com.belenot.chat.dao;

import java.util.Set;
import java.util.stream.Collectors;

import com.belenot.chat.domain.Client;

public class ClientDao {
    private Set<Client> clientSet;

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
}
