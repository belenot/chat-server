package com.belenot.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import com.belenot.chat.dao.ClientDao;
import com.belenot.chat.domain.Client;
import com.belenot.chat.event.ChatEvent;
import com.belenot.chat.exception.ServerException;
import com.belenot.chat.exception.ServerExceptionCode;
import com.belenot.chat.factory.ClientConnectionFactory;

public class Server implements Runnable, AutoCloseable, ApplicationListener<ChatEvent>  {
    private ServerSocket serverSocket;
    private boolean stopped = false;
    private boolean closed = false;

    @Autowired
    private Environment env;
    
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
	    ClientConnection clientConnection = null;
	    try {
	        clientConnection = recieveClientConnection();
	    } catch (ServerException exc) {
		logger.severe(exc.toString());
		continue;
	    }
	    runClientConnection(clientConnection);
	}
	close();
    }

    @Override
    public void close() {
	closed = true;
	try {
	    publisher.removeAllClientConnections(true);
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
	    clientDao.addClient(chatCommand.getParameters().get("name"), chatCommand.getParameters().get("password"));
	    break;
	case "ban":
	    publisher.removeClientConnectionSetByName(chatCommand.getParameters().get("name"), true);
	    break;
	default:
	    logger.warning(String.format("Unrecognized chat command: %s", command));
	    break;
	}

    }

    protected ClientConnection recieveClientConnection() throws ServerException {
	ClientConnection clientConnection = null;
	Socket clientSocket = null;
	byte[] buffer = new byte[1024];
	Arrays.fill(buffer, 0, buffer.length, (byte)0);
	logger.info(String.format("Listen for connections on %d\n", serverSocketPort));
	try {
	    clientSocket = serverSocket.accept();
	    clientSocket.getInputStream().read(buffer);
	} catch (IOException exc) {
	    if (!serverSocket.isClosed()) {
		ServerException serverException = new ServerException("Error while accepting client socket", exc, ServerExceptionCode.RECIEVE);
		throw serverException;
	    } else {
		return null;
	    }
	}	    
	String name = null;
	String password = null;
	try {
	    name = retrieveName(buffer);
	    password = retrievePassword(buffer);
	} catch (IllegalArgumentException exc) {
	    throw new ServerException("Illegal data format from socket", exc, ServerExceptionCode.RECIEVE);
	}
	logger.info(String.format("Authorize attempt from client: %s", name));
	Client client = clientDao.getClient(name, password);
	if (client == null) {
	    try {
		if (!clientSocket.isClosed()) clientSocket.close();
	    } catch (IOException exc) {
		exc.printStackTrace();
	    }
	    throw new ServerException(String.format("Can't gain client by name %s\n", name), ServerExceptionCode.RECIEVE);
	}
	
	clientConnection = clientConnectionFactory.newClientConnection(client, clientSocket);
	if (clientConnection == null) throw new ServerException("Can't create clientConnection for client: " + name, ServerExceptionCode.RECIEVE);
	return clientConnection;
    }

    protected void runClientConnection(ClientConnection clientConnection) {
	(new Thread(clientConnection)).start();
    }
    
    public static void main(String[] args) {
	System.out.println("Server");
    }

    //template username\npassword
    private String retrieveName(byte[] buffer) throws IllegalArgumentException {
	String strBuffer = (new String(buffer)).trim();
	String name = strBuffer.split("\n")[0];
	return name;
    }

    private String retrievePassword(byte[] buffer) throws IllegalArgumentException {
	String strBuffer = (new String(buffer)).trim();
	String password = strBuffer.split("\n")[1];
	return password;
    }
}
