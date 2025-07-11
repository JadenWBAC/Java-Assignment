package com.javafxtutorial.olaseven;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    @FXML private Label welcomeText;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private static final String USERS_FILE = "Users";

    @FXML
    protected void onLoginButtonClick() throws IOException {
        String username = getFieldText(usernameField);
        String password = getFieldText(passwordField);

        if (!validateInput(username, password)) {
            setWelcomeMessage("Please enter both username and password");
            return;
        }

        User matchedUser = authenticateUser(username, password);

        if (matchedUser != null) {
            String welcomeMessage = String.format("Welcome to our application %s %s",
                    matchedUser.getFirstname(), matchedUser.getLastname());
            HelloApplication.loadWelcomeScreen(welcomeMessage);
        } else {
            setWelcomeMessage("Invalid username or password, you may want to go home");
        }
    }

    private boolean validateInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    private String getFieldText(TextField field) {
        return field.getText().trim();
    }

    private void setWelcomeMessage(String message) {
        welcomeText.setText(message);
    }

    private User authenticateUser(String username, String password) {
        List<User> users = loadUsers();
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    private List<User> loadUsers() {
        List<User> users = new ArrayList<>();

        try (InputStream inputStream = getInputStream()) {
            if (inputStream != null) {
                loadUsersFromStream(inputStream, users);
            }
        } catch (IOException e) {
            System.err.println("Error loading users file: " + e.getMessage());
            loadDefaultUsers(users);
        }

        return users;
    }

    private InputStream getInputStream() throws IOException {
        // Try to load from resources first
        URL resourceUrl = getClass().getResource("/" + USERS_FILE);
        if (resourceUrl != null) {
            return resourceUrl.openStream();
        }

        // Fall back to file system
        try {
            return new FileInputStream(USERS_FILE);
        } catch (FileNotFoundException e) {
            System.err.println("Users file not found in resources or file system");
            return null;
        }
    }

    private void loadUsersFromStream(InputStream inputStream, List<User> users) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                User user = parseUserFromLine(line);
                if (user != null) {
                    users.add(user);
                }
            }
        }
    }

    private User parseUserFromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 4) {
            return new User(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[3].trim()
            );
        }
        return null;
    }

    private void loadDefaultUsers(List<User> users) {
        // Default users for testing when file is not available
        String[][] defaultUsers = {
                {"John", "Doe", "johndoe", "password123"},
                {"Alice", "Smith", "alicesmith", "pass456"},
                {"Bob", "Johnson", "bobjohnson", "abc123"},
                {"Emily", "Jones", "emilyjones", "testpass"},
                {"Michael", "Brown", "michaelbrown", "securepwd"}
        };

        for (String[] userData : defaultUsers) {
            users.add(new User(userData[0], userData[1], userData[2], userData[3]));
        }

        System.out.println("Using default users for testing");
    }
}