package com.belenot.chat;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.belenot.chat.aspect.LoggingAspect;
import com.belenot.chat.dao.ClientDao;
import com.belenot.chat.dao.MessageDao;
import com.belenot.chat.factory.ClientConnectionFactory;
import com.belenot.chat.factory.ClientConnectionFactoryImpl;
import com.belenot.chat.logging.ServerLogger;

@Configurable
@EnableAspectJAutoProxy( exposeProxy=true )
public class ChatConfig {

    @Bean
    public Logger logger() {
	Logger logger = new ServerLogger();
	return logger;
    }

    @Bean( initMethod = "init" )
    public ClientDao clientDao() {
	ClientDao clientDao = new ClientDao();
	//clientDao.addClient("ivan");
	//clientDao.addClient("vasya");
	//clientDao.addClient("igor");
	clientDao.setHost("localhost");
	clientDao.setPort(8832);
	clientDao.setUser("chat_db");
	clientDao.setPassword("chat_db");
	clientDao.setLogger(logger());
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
	ClientConnectionFactoryImpl clientConnectionFactory = new ClientConnectionFactoryImpl();
	clientConnectionFactory.setPublisher(publisher());
	clientConnectionFactory.setLogger(logger());
	return clientConnectionFactory;
    }

    @Bean( initMethod = "init" , autowire = Autowire.BY_TYPE )
    public Server server() {
	Server server = new Server();
	server.setServerSocketPort(5678);
	server.setClientDao(clientDao());
	server.setClientConnectionFactory(clientConnectionFactory());
	server.setLogger(logger());
	return server;
    }

    @Bean
    public LoggingAspect loggingAspect() {
	LoggingAspect loggingAspect = new LoggingAspect();
	loggingAspect.setLogger(logger());
	return loggingAspect;
    }
    
	
    
}
