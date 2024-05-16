package com.example.rentitfinalsjava;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseManager {
    private static DatabaseManager instance;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void initializeDB() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection c = SQLConnection.getConnection()) {
                Statement stmt;
                stmt = c.createStatement();

                String createDBQuery = "CREATE DATABASE IF NOT EXISTS dbrentit;";
                stmt.execute(createDBQuery);

                SQLConnection.DBName = "dbrentit";
                c.setCatalog("dbrentit");

                stmt = c.createStatement();

                String createTblUsersQuery = "CREATE TABLE IF NOT EXISTS tblUser (" +
                        "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "firstname VARCHAR (50) NOT NULL," +
                        "lastname VARCHAR (50) NOT NULL," +
                        "gender VARCHAR (50) NOT NULL," +
                        "email VARCHAR (50) NOT NULL," +
                        "username VARCHAR (50) NOT NULL," +
                        "password VARCHAR (50) NOT NULL)";
                stmt.execute(createTblUsersQuery);

                System.out.println("Database with TABLES created successfully.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void insertUser(String firstName, String lastName, String gender, String email, String username, String password) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection c = SQLConnection.getConnection()) {
                String query = "INSERT INTO tblUser (firstname, lastname, gender, email, username, password) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = c.prepareStatement(query)) {
                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, gender);
                    statement.setString(4, email);
                    statement.setString(5, username);
                    statement.setString(6, password);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("User inserted successfully.");
                    } else {
                        System.out.println("Failed to insert user.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private static boolean userIsFound = false;

    public boolean validateUser(String username, String password) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = SQLConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement("SELECT * FROM tblUser WHERE username = ? AND password = ?")) {
                    statement.setString(1, username);
                    statement.setString(2, password);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        userIsFound = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
       return userIsFound;
    }
}
