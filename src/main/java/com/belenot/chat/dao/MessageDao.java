package com.belenot.chat.dao;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.belenot.chat.domain.Client;
import com.belenot.chat.domain.Message;

public class MessageDao {
    private List<Message> messageList = new LinkedList<>();

    public Message addMessage(Client client, String text) {
	Message message = new Message();
	int id = messageList.size();
	message.setId(id);
	message.setDate(new Date());
	message.setClient(client);
	message.setText(text + "\0");
	messageList.add(message);
	return message;
    }

    public Message getMessage(int id) {
	return messageList.stream().filter( (m) -> m.getId() == id).collect(Collectors.toList()).get(0);
    }

    public Message getLatestMessage() {
	return messageList.get(messageList.size() - 1);
    }
}
