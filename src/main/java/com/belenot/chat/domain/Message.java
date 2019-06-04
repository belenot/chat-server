package com.belenot.chat.domain;

import java.util.Date;

public class Message {
    private int id;
    private String text;
    private Client client;
    private Date date;

    public void setId(int id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setClient(Client client) { this.client = client; }
    public void setDate(Date date) { this.date = date; }

    public int getId() { return id; }
    public String getText() { return text; }
    public Date getDate() { return date; }
    public Client getClient() { return client; }
    
}
