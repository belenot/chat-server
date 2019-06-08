package com.belenot.chat;

import java.util.HashMap;
import java.util.Map;

public class ChatCommand {
    private String command;
    private Map<String, String> parameters = new HashMap<>();

    public ChatCommand(String command) { this.command = command; }
    public void addParameter(String parameter, String value) { parameters.put(parameter, value); }

    public String getCommand() { return command; }
    
    public Map<String, String> getParameters() { return new HashMap<>(parameters); }
    
    
}
