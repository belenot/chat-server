package com.belenot.chat.event;

import org.springframework.context.ApplicationEvent;

import com.belenot.chat.ChatCommand;

public class ChatEvent extends ApplicationEvent {
    public ChatEvent(ChatCommand chatCommand) {
	super(chatCommand);
    }
}
