package com.belenot.chat.client;

import java.util.LinkedList;
import java.util.List;

public class MessageStack {
    private List<String> messageList = new LinkedList<>();

    private int cursor = 0;

    public void showRecent() {
	System.out.printf("Recent(count: %d)\n:", messageList.size());
	for (; cursor < messageList.size(); cursor++) {
	    System.out.println(messageList.get(cursor));
	}
    }

    public void add(String msg) {
	messageList.add(msg);
    }

    
}
