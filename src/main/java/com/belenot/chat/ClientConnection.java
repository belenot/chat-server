package com.belenot.chat;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.belenot.chat.domain.Client;
import com.belenot.chat.domain.Message;

public class ClientConnection implements Runnable, Closeable {
    private boolean isStarted = false;
    private boolean isClosed = false;
    
    private Client client;
    private Socket socket;
    private Publisher publisher;
    private Logger logger;

    public void setClient(Client client) { this.client = client; }
    public void setSocket(Socket socket) { this.socket = socket; }
    public void setPublisher(Publisher publisher) { this.publisher = publisher; }
    public void setLogger(Logger logger) { this.logger = logger; }

    public void receive(Message message) {
	String formattedMessage = String.format("[%s]%s: %s", message.getDate().toString(), message.getClient().getName(), message.getText());
	try {
	    OutputStream out = socket.getOutputStream();
	    out.write(formattedMessage.getBytes());
	} catch (IOException exc) {
	    String msg = String.format("Can't write to client with name %s message \"%s\"", message.getClient().getName(), message.getText());
	    logger.severe(msg);
	}
	
    }
    
    public boolean isStarted() { return isStarted; }
    public boolean isClosed() { return isClosed; }


    @Override
    public void run() {
	isStarted = true;
	// Handle client requests
    }

    @Override
    public void close() {
	try {
	    if (socket != null && !socket.isClosed()) socket.close();
	} catch (IOException exc) { }
	isClosed = true;
    }
    
}
