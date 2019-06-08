package com.belenot.chat;

import java.util.regex.Pattern;

import org.springframework.context.ApplicationEventPublisher;

import com.belenot.chat.event.ChatEvent;

public class AdminConnection extends ClientConnection {
    ApplicationEventPublisher applicationPublisher;

    //Warning: Very herovo
    public AdminConnection(ApplicationEventPublisher applicationPublisher) {
	super();
	this.applicationPublisher = applicationPublisher;
    }

    @Override
    protected void proceedInputText(String text) {
	Pattern p = null;
	p = Pattern.compile("^close$");
	if (p.matcher(text).find()) {
	    applicationPublisher.publishEvent(new ChatEvent("close"));
	    return;
	}
	p = Pattern.compile("^write:");
	if (p.matcher(text).find()) {
	    super.proceedInputText(text);
	    return;
	}
	super.proceedInputText(text);
    }
	    
	

    
}
