package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {



    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/foot", "postgres", "root");
        return conn;
    }
}
