package com.belenot.chat;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.belenot.chat.factory.ClientConnectionFactory;

public class Main {
    public static void main(String[] args) {
	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ChatConfig.class);
	Server server = ctx.getBean("server", Server.class);
	ClientConnectionFactory clientConnectionFactory = ctx.getBean("clientConnectionFactory", ClientConnectionFactory.class);
	server.setClientConnectionFactory(clientConnectionFactory);
	server.run();
	ctx.close();
    }

}
