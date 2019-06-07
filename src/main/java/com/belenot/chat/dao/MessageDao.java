package com.belenot.chat.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.belenot.chat.domain.Client;
import com.belenot.chat.domain.Message;

public class MessageDao {
    private List<Message> messageList = new LinkedList<>();

    private String host;
    private int port;
    private String user;
    private String password;
    private Logger logger;
    
    private Connection conn;

    public void setHost(String host) { this.host = host; }
    public void setPort(int port) { this.port = port; }
    public void setUser(String user) { this.user = user; }
    public void setPassword(String password) { this.password = password; }
    public void setLogger(Logger logger) { this.logger = logger; }
    
    public void init() {
	try {
	    String connectionAddress = String.format("jdbc:postgresql://%s:%d/chat", host, port);
	    conn = DriverManager.getConnection(connectionAddress, user, password);
	} catch (SQLException exc) {
	    logger.severe(exc.getSQLState());
	}
    }

    public Message addMessage(Client client, String text) {
	try {
	    long timestamp = (new Date()).getTime();
	    String sql_query = String.format("INSERT INTO message (text_msg, client, date_create) VALUES ('%s', %d, to_timestamp(%d))", text, client.getId(), timestamp);
	    PreparedStatement st = conn.prepareStatement(sql_query);
	    st.executeUpdate();
	    int id = -1;
	    sql_query = String.format("SELECT id FROM message order by date_create desc LIMIT 1;");
	    st = conn.prepareStatement(sql_query);
	    ResultSet rs = st.executeQuery();
	    if (rs.next()) {
		id = rs.getInt(1);
	    } else {
		throw new SQLException();
	    }
	    Message message = new Message();
	    message.setId(id);
	    message.setDate(new Date(timestamp));
	    message.setClient(client);
	    message.setText(text + "\0");
	    return message;
	} catch (SQLException exc) {
	    logger.severe(String.format("Error while adding message from %s(id=%d) with content:\"%s\"", client.getName(), client.getId(), text));
	    return null;
	}
    }

}
