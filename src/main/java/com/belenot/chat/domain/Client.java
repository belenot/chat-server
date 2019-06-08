package com.belenot.chat.domain;

public class Client {
    private int id;
    private String name;
    private boolean admin;
    
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isAdmin() { return admin; }

}
