package com.belenot.chat;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.belenot.chat.domain.Client;
import com.belenot.chat.domain.Message;

public class ClientConnection implements Runnable, Closeable {
    private boolean started = false;
    private boolean closed = false;
    
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
    
    public boolean isStarted() { return started; }
    public boolean isClosed() { return closed; }


    @Override
    public void run() {
	started = true;
	try {
	    InputStream in = socket.getInputStream();
	    long timeout = 5000;
	    long start = System.currentTimeMillis();
	    int b;
	    String textBuffer = "";
	    while (!closed && socket != null && !socket.isClosed() && in != null) {
		while ( in.available() > 0 && (b = in.read()) > 0) {
		    textBuffer += (char)b;
		}
		if (textBuffer.length() > 0) {
		    if (textBuffer.equals("close")) {
			break;
		    }
		    publisher.publish(client, textBuffer);
		    textBuffer = "";
		    start = System.currentTimeMillis();
		}
		else if ( (System.currentTimeMillis() - start) > timeout) {
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException exc) {
			logger.severe("Can't sleep thread in client connection with " + client.getName());
		    }
		}
	    }
	} catch (IOException exc) {
	    logger.severe("Error during connection with client with name " + client.getName());
	}
	finally {
	    close();
	}
    }

    @Override
    public void close() {
	logger.info(String.format("Close connection with client %s", client.getName()));
	try {
	    if (socket != null && !socket.isClosed()) socket.close();
	} catch (IOException exc) { }
	closed = true;
    }
    
}
