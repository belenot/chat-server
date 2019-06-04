package com.belenot.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
	Socket socket = null;
	try {
	    System.out.print("host:port=");
	    String strBuffer = (new BufferedReader(new InputStreamReader(System.in))).readLine();
	    String host = strBuffer.split(":")[0];
	    int port = Integer.parseInt(strBuffer.split(":")[1]);
	    socket = new Socket(host, port);
	    socket.getOutputStream().write("igor".getBytes());
	} catch (IOException exc) {
	    exc.printStackTrace();
	}
	finally {
	    try {
		socket.close();
	    } catch (IOException exc) { }
	}
    }
}
