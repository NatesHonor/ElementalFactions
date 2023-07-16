package com.nate.elemental.commands.data.h2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.nate.elemental.utils.storage.h2.Database;
import com.nate.elemental.utils.storage.h2.TableUtils;

public class Debug implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    if (args.length == 0) {
	        sender.sendMessage("Usage: /debug-h2 <table>");
	        return true;
	    }

	    String tableName = args[0];

	    if (tableName.equals("user")) {
	        getTableData(tableName);
	    }

	    TableUtils tableUtils = new TableUtils();
	    List<String> tableData = tableUtils.getTableData(tableName);

	    sender.sendMessage("Table: " + tableName);
	    sender.sendMessage("Data:");

	    for (String row : tableData) {
	        sender.sendMessage(formatRow(row));
	    }

	    return true;
	}


	public List<String> getTableData(String tableName) {
	    List<String> tableData = new ArrayList<>();
	    try (Connection connection = Database.getConnection(); Statement statement = connection.createStatement()) {
	        String query = "SELECT * FROM `" + tableName + "`";
	        ResultSet resultSet = statement.executeQuery(query);
	        ResultSetMetaData metaData = resultSet.getMetaData();
	        int columnCount = metaData.getColumnCount();
	        while (resultSet.next()) {
	            StringBuilder row = new StringBuilder();
	            for (int i = 1; i <= columnCount; i++) {
	                String columnName = metaData.getColumnName(i);
	                String columnValue = resultSet.getString(i);
	                row.append(columnName).append(": ").append(columnValue).append(" | ");
	            }
	            tableData.add(row.toString());
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return tableData;
	}


    private String formatRow(String row) {
        String[] columns = row.split("\\s+");
        if (columns.length % 2 != 0) {
            return row;
        }
        StringBuilder formattedRow = new StringBuilder();
        for (int i = 0; i < columns.length; i += 2) {
            if (i > 0) {
                formattedRow.append("  ");
            }
            String columnName = columns[i].replace(":", "");
            String columnValue = columns[i + 1];
            formattedRow.append(columnName).append(": ").append(columnValue);
        }
        return formattedRow.toString();
    }
}
