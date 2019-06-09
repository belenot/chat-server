package com.belenot.chat;

import java.util.regex.Pattern;

import org.springframework.context.ApplicationEventPublisher;

import com.belenot.chat.event.ChatEvent;

public class AdminConnection extends ClientConnection {
    ApplicationEventPublisher applicationPublisher;

    //Warning: Very bad
    public AdminConnection(ApplicationEventPublisher applicationPublisher) {
	super();
	this.applicationPublisher = applicationPublisher;
    }

    @Override
    protected void proceedInputText(String text) {
	Pattern p = null;
	p = Pattern.compile("^close$");
	if (p.matcher(text).find()) {
	    ChatCommand chatCommand = new ChatCommand("close");
	    applicationPublisher.publishEvent(new ChatEvent(chatCommand));
	    return;
	}
	p = Pattern.compile("^write:");
	if (p.matcher(text).find()) {
	    super.proceedInputText(text);
	    return;
	}
	p = Pattern.compile("^create:[a-zA-Z][a-zA-Z0-9]*:[a-zA-Z0-9]{1,}$");
	if (p.matcher(text).find()) {
	    int firstDoubleDotIndex = text.indexOf(":");
	    int secondDoubleDotIndex = text.indexOf(":", firstDoubleDotIndex + 1);
	    String name = text.substring(firstDoubleDotIndex + 1, secondDoubleDotIndex);
	    String password = text.substring(secondDoubleDotIndex + 1, text.length());
	    ChatCommand chatCommand = new ChatCommand("create");
	    chatCommand.addParameter("name", name);
	    chatCommand.addParameter("password", password);
	    applicationPublisher.publishEvent(new ChatEvent(chatCommand));
	    return;
	}
	    
	super.proceedInputText(text);
    }
	    
	

    
}
