package com.belenot.chat.event;

import org.springframework.context.ApplicationEvent;

public class ChatEvent extends ApplicationEvent {
    private String command;
    public ChatEvent(String command) {
	super(command);
	this.command = command;
    }
    
    @Override
    public String getSource() {
	return command;
    }
}
