package com.belenot.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import com.belenot.chat.dao.ClientDao;
import com.belenot.chat.domain.Client;
import com.belenot.chat.factory.ClientConnectionFactory;

public class Server implements Runnable, AutoCloseable {
    private ServerSocket serverSocket;
    /////
    private boolean stopped = false;
    
    private int serverSocketPort;
    private ClientDao clientDao;
    private ClientConnectionFactory clientConnectionFactory;
    ////
    private Logger logger;

    public void setServerPort(int serverSocketPort) { this.serverSocketPort = serverSocketPort; }
    public void setClientDao(ClientDao clientDao) { this.clientDao = clientDao; }
    public void setClientConnectionFactory(ClientConnectionFactory clientConnectionFactory) { this.clientConnectionFactory = clientConnectionFactory; }
    public void setLogger(Logger logger) { this.logger = logger; }
    

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
	    try {
		clientSocket = serverSocket.accept();
		clientSocket.getInputStream().read(buffer);
	    } catch (IOException exc) {
		logger.severe("Error while  proccessing client socket");
		exc.printStackTrace();
	    }
	    String name = new String(buffer);
	    Client client = clientDao.getClient(name);
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
	try {
	    if(serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
	} catch (IOException exc) {
	    logger.severe("Error while closing server socket");
	    exc.printStackTrace();
	}
    }
    ///
    public void stop() {
	stopped = true;
    }
    ///
    public void start() {
	stopped = false;
    }
    
    public static void main(String[] args) {
	System.out.println("Server");
    }

    ////
    private void runThread(Runnable runnable) {
	(new Thread()).start();
    }
}
