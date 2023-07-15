package com.nate.elemental.utils.storage.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.nate.elemental.Factions;

public class DatabaseConnection {
    private static final String DB_URL = Factions.getConnectionURL();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
