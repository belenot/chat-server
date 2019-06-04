package com.belenot.chat;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import com.belenot.chat.dao.ClientDao;
import com.belenot.chat.dao.MessageDao;
import com.belenot.chat.factory.ClientConnectionFactory;
import com.belenot.chat.logging.ServerLogger;

@Configurable
public class ChatConfig {

    @Bean
    public Logger logger() {
	Logger logger = new ServerLogger();
	return logger;
    }

    @Bean
    public ClientDao clientDao() {
	ClientDao clientDao = new ClientDao();
	clientDao.addClient("ivan");
	clientDao.addClient("vasya");
	clientDao.addClient("igor");
	return clientDao;
    }

    @Bean
    public MessageDao messageDao() {
	MessageDao messageDao = new MessageDao();
	return messageDao;
    }

    @Bean
    public Publisher publisher() {
	Publisher publisher = new Publisher();
	publisher.setMessageDao(messageDao());
	return publisher;
    }

    @Bean
    public ClientConnectionFactory clientConnectionFactory() {
	ClientConnectionFactory clientConnectionFactory = new ClientConnectionFactory();
	clientConnectionFactory.setPublisher(publisher());
	clientConnectionFactory.setSoTimeout(1000);
	clientConnectionFactory.setLogger(logger());
	return clientConnectionFactory;
    }

    @Bean( initMethod = "init" )
    public Server server() {
	Server server = new Server();
	server.setServerSocketPort(5678);
	server.setClientDao(clientDao());
	server.setClientConnectionFactory(clientConnectionFactory());
	server.setLogger(logger());
	return server;
    }

    
    
	
    
}
