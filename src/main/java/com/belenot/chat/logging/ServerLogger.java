package com.belenot.chat.logging;

import java.util.logging.Logger;

public class ServerLogger extends Logger {
    public ServerLogger() {
	super("ServerLogger", null);
    }

    public void severe(String msg) {
	System.out.println(msg);
    }

    public void info(String msg) {
	System.out.println(msg);
    }
    
}
