package com.belenot.chat.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.belenot.chat.domain.Client;

public class ClientDao {

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
	
    
    public Client getClient(String name, String password) {
	try {
	    String sql_select = "SELECT client.id, client.admin FROM client join password on client.id = password.client WHERE client.name = ? AND password.password = ?;";
	    PreparedStatement st = conn.prepareStatement(sql_select);
	    st.setString(1, name);
	    st.setString(2, password);
	    ResultSet resultSet = st.executeQuery();
	    int id = -1;
	    boolean admin = false;
	    if (resultSet.next()) {
		id  = resultSet.getInt("id");
		admin = resultSet.getBoolean("admin");
	    } else {
		throw new SQLException("result set is null length");
	    }
	    Client client = new Client();
	    client.setId(id);
	    client.setName(name);
	    client.setAdmin(admin);
	    return client;
	} catch (SQLException exc) {
	    logger.severe(exc.getSQLState());
	}
	
	return null;
    }

    public Client addClient(String name) {
	return addClient(name, name);
    }
	

    //NEED TRANSACTION!!!
    public Client addClient(String name, String password) {
	try {
	    String sql_query = "INSERT INTO client (name) values (?);";
	    PreparedStatement st = conn.prepareStatement(sql_query);
	    st.setString(1, name);
	    st.executeUpdate();
	    sql_query = String.format("SELECT currval('client_id_seq');");
	    st = conn.prepareStatement(sql_query);
	    ResultSet rs = st.executeQuery();
	    int id = -1;
	    if (rs.next()) {
		id = rs.getInt(1);
	    } else {
		throw new SQLException();
	    }
	    sql_query = "INSERT INTO password (client, password) VALUES (?, ?);";
	    st = conn.prepareStatement(sql_query);
	    st.setInt(1, id);
	    st.setString(2, password);
	    st.executeUpdate();
	    Client client = new Client();
	    client.setId(id);
	    client.setName(name);
	    return client;
	} catch (SQLException exc) {
	    logger.severe(exc.getSQLState());
	    return null;
	}
    }

    public Client addAdmin(String name, String password) {
	return null;
    }
	
}
