package com.nate.elemental.utils.storage.h2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.nate.elemental.Factions;

public class TableUtils {
    private static final String DB_URL = Factions.getConnectionURL();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static boolean tableExists(String tableName) {
        try (Connection connection = getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName, null);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

public List<String> getTableNames() {
    List<String> tableNames = new ArrayList<>();
    try (Connection connection = getConnection()) {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});
        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            tableNames.add(tableName);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return tableNames;
}

public List<String> getTableData(String tableName) {
    List<String> tableData = new ArrayList<>();
    try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            StringBuilder row = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                row.append(metaData.getColumnName(i)).append(": ").append(resultSet.getString(i)).append(" ");
            }
            tableData.add(row.toString());
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return tableData;
}
}



