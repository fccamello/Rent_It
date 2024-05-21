package com.example.rentitfinalsjava;
import com.example.rentitfinalsjava.Current_User;
//import com.example.rentitfinalsjava.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseManager(){
        initializeDB();
    }
    public static DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
        }

        return instance;
    }
    public void initializeDB(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try (Connection c = SQLConnection.getConnection()){
                Statement stmt;
                stmt = c.createStatement();

                String createDBQuery = "CREATE DATABASE IF NOT EXISTS dbrentit;";
                stmt.execute(createDBQuery);

                SQLConnection.DBName = "dbrentit";
                c.setCatalog("dbrentit");

                c.setAutoCommit(false);
                stmt = c.createStatement();

                String createTblUserQuery = "CREATE TABLE IF NOT EXISTS tblUser (" +
                        "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "firstname VARCHAR (50) NOT NULL," +
                        "lastname VARCHAR (50) NOT NULL," +
                        "gender VARCHAR (50) NOT NULL," +
                        "address VARCHAR (100) NOT NULL," +
                        "contact_number VARCHAR(11) NOT NULL," +
                        "email VARCHAR (50) NOT NULL," +
                        "username VARCHAR (50) NOT NULL," +
                        "password VARCHAR (50) NOT NULL," +
                        "isOwner INT (1) NOT NULL DEFAULT 0)";
                stmt.execute(createTblUserQuery);

                String createTblItemQuery = "CREATE TABLE IF NOT EXISTS tblItem(" +
                        "item_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "user_id INT NOT NULL," +
                        "title VARCHAR (50) NOT NULL," +
                        "description VARCHAR (100) NOT NULL," +
                        "image VARCHAR (255) NOT NULL," +
                        "category VARCHAR (25) NOT NULL," +
                        "price DOUBLE NOT NULL," +
                        "isAvailable INT NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES tbluser (user_id) ON DELETE CASCADE)";
                stmt.execute(createTblItemQuery);

                String createtblRentRequest = "CREATE TABLE IF NOT EXISTS tblRentRequest(" +
                        "rent_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "item_id INT NOT NULL," +
                        "user_id INT NOT NULL," +
                        "requestDate DATE NOT NULL," +
                        "durationCategory VARCHAR (20) NOT NULL," +
                        "duration INT NOT NULL," +
                        "endRentDate DATE NOT NULL," +
                        "totalAmount DOUBLE NOT NULL," +
                        "modeOfDelivery VARCHAR (50) NOT NULL," +
                        "isApproved INT NOT NULL DEFAULT -1," +
                        "isReturned INT NOT NULL DEFAULT 0," +
                        "FOREIGN KEY (item_id) REFERENCES tblItem (item_id)," +
                        "FOREIGN KEY (user_id) REFERENCES tblUser (user_id) ON DELETE CASCADE)";
                stmt.execute(createtblRentRequest);

                c.commit();
                System.out.println(SQLConnection.DBName);
                System.out.println("Database with TABLES created successfully.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private final Current_User current_user = Current_User.getInstance();
    public boolean insertUser(String firstName, String lastName, String gender, String email, String address, String username, String userType, String password) {
        final boolean[] result = {true};
        String contact_number = "09123456789";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection c = SQLConnection.getConnection()) {
                String query = "INSERT INTO tblUser (firstname, lastname, gender, address, contact_number, email, username, password, isOwner) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = c.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    int isOwner = (userType.equals("Owner")) ? 1 : 0; // if userType == 0, then 1 else 0

                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, gender);
                    statement.setString(4, address);
                    statement.setString(5, contact_number); // contact_number
                    statement.setString(6, email);
                    statement.setString(7, username);
                    statement.setString(8, password);
                    statement.setInt(9, isOwner);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("User inserted successfully.");
                        ResultSet res = statement.getGeneratedKeys();
                        res.next();
                        int user_id = res.getInt(1);
                        current_user.setCurrent_User(user_id, username, firstName, lastName, email, address, gender, (isOwner == 1));
                        System.out.println(current_user);
                    } else {
                        System.out.println("Failed to insert user.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                result[0] = false;
            }
        });

        return result[0];
    }

    private boolean userIsFound = false;

    public boolean validateUser(String username, String password) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection connection = SQLConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM tblUser WHERE username = ? AND password = ?")) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    userIsFound = true;

                    resultSet.next();
                    current_user.setCurrent_User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("username"),
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getString("email"),
                            resultSet.getString("address"),
                            resultSet.getString("gender"),
                            (resultSet.getInt("isOwner") == 1)
                    );

                    System.out.println(current_user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return userIsFound;
    }
//
//    public List<Item> getItems (){
//        final boolean [] retrievedItems = {true};
//        final List<Item> models = new ArrayList<>();
//        System.out.println("current db: " + SQLConnection.DBName);
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                try (Connection conn = SQLConnection.getConnection();
//                     Statement stmt = conn.createStatement()){
//
//                    String query = "SELECT item_id, user_id, title, description, image, category, price " +
//                            "FROM tblitem WHERE isAvailable = 1";
//                    ResultSet items = stmt.executeQuery(query);
//
//                    while (items.next()){
//                        System.out.println("item_id: " + items.getInt("item_id"));
//                        models.add(new Item(
//                                items.getInt("item_id"),
//                                items.getInt("user_id"),
//                                items.getString("title"),
//                                items.getString("image"),
//                                items.getString("description"),
//                                items.getString("category"),
//                                items.getDouble("price")
//                        ));
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    retrievedItems[0] = false;
//                }
//            }
//        });
//
//        return models;
//    }
}