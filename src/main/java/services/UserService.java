package services;

import entities.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    // Method to authenticate user
    public User authenticate(String username, String password) throws SQLException {
        // Implement your database query to authenticate the user
        // For simplicity, let's assume we have a method to get all users
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // Method to get all users (for demonstration purposes)
    public List<User> getAllUsers() throws SQLException {
        // Implement your database query to get all users
        // For simplicity, let's return a hardcoded list
        return List.of(
                new User(1, "admin", "admin123", "admin"),
                new User(2, "user", "user123", "user")
        );
    }
}