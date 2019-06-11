package com.belenot.chat.aspect;

import java.net.Socket;
import java.util.Date;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.belenot.chat.domain.Client;


@Aspect
public class LoggingAspect {
    private Logger logger;

    public void setLogger(Logger logger) { this.logger = logger; }
    
    @Before( "execution(* com.belenot.chat.factory.ClientConnectionFactory.newClientConnection(com.belenot.chat.domain.Client, java.net.Socket))" )
    public void beforeNewClientConnection(JoinPoint jp) {
	Client client = (Client) jp.getArgs()[0];
	Socket socket = (Socket) jp.getArgs()[1];
	try {
	    String name = client.getName();
	    int id = client.getId();
	    String hostaddress = socket.getInetAddress().getHostAddress();
	    int port = socket.getPort();
	    Date date = new Date();
	    String message = String.format("[%s] Creating connection for %s with id %d on %s:%d\n",date.toString() ,name, id, hostaddress, port);
	    logger.info(message);
	} catch (NullPointerException exc) {
	    //Rewrite with severe, change to around and terminate joinpoint's execution
	    logger.warning("client or socket is null while executing newClientConnection");
	}
    }
}
