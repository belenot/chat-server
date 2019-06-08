package com.belenot.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.belenot.chat.dao.ClientDao;
import com.belenot.chat.domain.Client;
import com.belenot.chat.event.ChatEvent;
import com.belenot.chat.factory.ClientConnectionFactory;

public class Server implements Runnable, AutoCloseable, ApplicationListener<ChatEvent>  {
    private ServerSocket serverSocket;
    private boolean stopped = false;
    private boolean closed = false;
    
    private int serverSocketPort;
    private ClientDao clientDao;
    private ClientConnectionFactory clientConnectionFactory;
    private Publisher publisher;
    private Logger logger;

    public void setServerSocketPort(int serverSocketPort) { this.serverSocketPort = serverSocketPort; }
    public void setClientDao(ClientDao clientDao) { this.clientDao = clientDao; }
    public void setClientConnectionFactory(ClientConnectionFactory clientConnectionFactory) { this.clientConnectionFactory = clientConnectionFactory; }
    public void setLogger(Logger logger) { this.logger = logger; }
    public void setPublisher(Publisher publisher) { this.publisher = publisher; }
    

    public void init() {
	try {
	    serverSocket = new ServerSocket(serverSocketPort);
	} catch (IOException exc) {
	    String msg = String.format("Can't create server socket with port %d\n%s\n", serverSocketPort, exc);
	    logger.severe(msg);
	}
    }

    @Override
    public void run() {
	while (!stopped && !closed) {
	    Socket clientSocket = null;
	    byte[] buffer = new byte[1024];
	    Arrays.fill(buffer, 0, buffer.length, (byte)0);
	    logger.info(String.format("Listen for connections on %d\n", serverSocketPort));
	    try {
		//Bring exception when shutdowned by admin. It worth to add timeout?
		clientSocket = serverSocket.accept();
		clientSocket.getInputStream().read(buffer);
	    } catch (IOException exc) {
		if (!serverSocket.isClosed()) {
		    logger.severe("Error while  proccessing client socket");
		    exc.printStackTrace();
		    continue;
		} else {
		    break;
		}
	    }	    
	    String name = null;
	    String password = null;
	    try {
		name = retrieveName(buffer);
		password = retrievePassword(buffer);
	    } catch (Exception exc) {
		logger.severe("Illegal data format from socket");
	    }
	    logger.info(String.format("Connection from client: %s", name));
	    Client client = clientDao.getClient(name, password);
	    if (client == null) {
		String msg = String.format("Can't gain client by name %s\n", name);
		logger.severe(msg);
		try {
		    if (!clientSocket.isClosed()) clientSocket.close();
		} catch (IOException exc) { exc.printStackTrace(); }
		continue;
	    }
	    ClientConnection clientConnection = clientConnectionFactory.newClientConnection(client, clientSocket);
	    runThread(clientConnection);
	}
    }

    @Override
    public void close() {
	closed = true;
	try {
	    publisher.closeAll();
	    if(serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
	} catch (IOException exc) {
	    logger.severe("Error while closing server socket");
	    exc.printStackTrace();
	}
    }

    @Override
    public void onApplicationEvent(ChatEvent event) {
	if (!(event.getSource() instanceof ChatCommand)) return;
	ChatCommand chatCommand = (ChatCommand) event.getSource();
	String command = chatCommand.getCommand();
	switch (command) {
	case "close": close(); break;
	case "create":
	    String name = chatCommand.getParameters().get("name");
	    String password = chatCommand.getParameters().get("password");
	    clientDao.addClient(name, password);
	    break;
	default:
	    logger.warning(String.format("Unrecognized chat command: %s", command));
	    break;
	}

    }
    
    public static void main(String[] args) {
	System.out.println("Server");
    }

    private void runThread(Runnable runnable) {
	(new Thread(runnable)).start();
    }

    //template username\npassword
    private String retrieveName(byte[] buffer) throws Exception {
	String strBuffer = (new String(buffer)).trim();
	String name = strBuffer.split("\n")[0];
	return name;
    }

    private String retrievePassword(byte[] buffer) throws Exception {
	String strBuffer = (new String(buffer)).trim();
	String password = strBuffer.split("\n")[1];
	return password;
    }
}
