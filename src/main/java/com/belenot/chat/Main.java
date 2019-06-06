package com.belenot.chat;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ChatConfig.class);
	Server server = ctx.getBean("server", Server.class);
	server.run();
	ctx.close();
    }

}
