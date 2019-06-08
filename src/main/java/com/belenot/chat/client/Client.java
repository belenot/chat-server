package com.belenot.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Client {
    
    public static void main(String[] args) {
	Socket socket = null;
	String userName = null;
	String password = null;
	try {
	    System.out.print("host:port=");
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String strBuffer = br.readLine();
	    String host = strBuffer.split(":")[0];
	    int port = Integer.parseInt(strBuffer.split(":")[1]);
	    System.out.print("Username: ");
	    userName = br.readLine();
	    System.out.print("Password: ");
	    password = br.readLine();
	    socket = new Socket(host, port);
	    socket.getOutputStream().write((new String(userName + "\n" + password)).getBytes());
	    strBuffer = "";
	    do {
		strBuffer = br.readLine();
		if (strBuffer.equals("read")) {
		    try {
			for (String message : viewInputMessages(socket)) 
			    System.out.println(message);
		    } catch (IOException exc) {
			System.out.println("Can't read input messages");
			exc.printStackTrace();
		    }
		    continue;
		}
		socket.getOutputStream().write(strBuffer.getBytes());
	    } while (!strBuffer.equals("exit"));
	    
	} catch (IOException exc) {
	    exc.printStackTrace();
	}
	finally {
	    try {
		socket.close();
	    } catch (IOException exc) { }
	}
    }

    private static List<String> viewInputMessages(Socket socket) throws IOException {
	InputStream in = socket.getInputStream();
	List<String> messages = new LinkedList<>();
	String buffer = "";
	int c;
	while (!socket.isClosed() && in.available() > 0 && (c = in.read()) >= 0) {
	    buffer += (char) c;
	    if (c == 0) {
		messages.add(buffer);
		buffer = "";
	    }
	}
	return messages;
    }

}
