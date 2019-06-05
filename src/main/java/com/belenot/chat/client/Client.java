package com.belenot.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client implements Runnable {
    private Socket socket;
    private boolean stopped = false;
    private MessageStack msgStack;

    public Client(Socket socket) {
	this.socket = socket;
        msgStack = new MessageStack();
    }
    public void stop() { stopped = true; }
    public MessageStack getMsgStack() { return msgStack; }

    
    public static void main(String[] args) {
	Socket socket = null;
	try {
	    System.out.print("host:port=");
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String strBuffer = br.readLine();
	    String host = strBuffer.split(":")[0];
	    int port = Integer.parseInt(strBuffer.split(":")[1]);
	    socket = new Socket(host, port);
	    Client client = new Client(socket);
	    MessageStack msgStack = client.getMsgStack();
	    (new Thread(client)).start();
	    socket.getOutputStream().write("igor".getBytes());
	    strBuffer = "";
	    while (!strBuffer.equals("exit")) {
		strBuffer = br.readLine();
		if (strBuffer.equals("read")) {
		    msgStack.showRecent();
		    continue;
		}
		socket.getOutputStream().write(strBuffer.getBytes());
	    }
	    
	} catch (IOException exc) {
	    exc.printStackTrace();
	}
	finally {
	    try {
		socket.close();
	    } catch (IOException exc) { }
	}
    }

    @Override
    public void run() {
	try {
	    InputStream in = socket.getInputStream();
	    while ( !socket.isClosed() ) {
		if (in.available() == 0) {
		    Thread.sleep(1000);
		    continue;
		}
		String msg = "";
		int c;
		while(!socket.isClosed() &&  in.available() > 0 && (c = in.read()) > 0 ) {
		    msg += (char) c;
		}
		msgStack.add(msg);
	    }
	} catch (IOException | InterruptedException exc) {
	    System.out.println("Client run exception");
	    exc.printStackTrace();
	}
    }
	
}
