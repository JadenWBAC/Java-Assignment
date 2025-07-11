package com.javafxtutorial.olaseven;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage mainStage;

    // Scene dimensions
    private static final double LOGIN_WIDTH = 320;
    private static final double LOGIN_HEIGHT = 240;
    private static final double WELCOME_WIDTH = 620;
    private static final double WELCOME_HEIGHT = 240;
    private static final double BOOKSTORE_WIDTH = 900;
    private static final double BOOKSTORE_HEIGHT = 700;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        loadLoginScreen();
    }

    private void loadLoginScreen() throws IOException {
        FXMLLoader fxmlLoader = loadFXML("login_view.fxml");
        Scene scene = new Scene(fxmlLoader.load(), LOGIN_WIDTH, LOGIN_HEIGHT);

        configureStage("Bookworm Haven - Login", scene);
        mainStage.show();
    }

    public static void loadWelcomeScreen(String message) throws IOException {
        FXMLLoader fxmlLoader = loadFXML("welcome_view.fxml");
        Scene scene = new Scene(fxmlLoader.load(), WELCOME_WIDTH, WELCOME_HEIGHT);

        WelcomeController controller = fxmlLoader.getController();
        controller.setWelcomeText(message);

        configureStage("Bookworm Haven - Welcome", scene);
    }

    public static void loadBookstoreSystem() throws IOException {
        FXMLLoader fxmlLoader = loadFXML("bookstore_view.fxml");
        Scene scene = new Scene(fxmlLoader.load(), BOOKSTORE_WIDTH, BOOKSTORE_HEIGHT);

        configureStage("Bookworm Haven - Management System", scene);
    }

    private static FXMLLoader loadFXML(String fxmlFile) throws IOException {
        FXMLLoader fxmlLoader = null;

        // Try multiple loading strategies
        String[] paths = {
                "/com/javafxtutorial/olaseven/" + fxmlFile,
                fxmlFile,
                "/" + fxmlFile
        };

        for (String path : paths) {
            try {
                fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(path));
                if (fxmlLoader.getLocation() != null) {
                    break;
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }

        // Final fallback using ClassLoader
        if (fxmlLoader == null || fxmlLoader.getLocation() == null) {
            fxmlLoader = new FXMLLoader(HelloApplication.class.getClassLoader().getResource(fxmlFile));
        }

        if (fxmlLoader == null || fxmlLoader.getLocation() == null) {
            throw new IOException("Could not find FXML file: " + fxmlFile +
                    "\nMake sure the FXML file is in the correct location:\n" +
                    "- src/main/resources/com/javafxtutorial/olaseven/" + fxmlFile + "\n" +
                    "- or in the same directory as your Java files");
        }

        return fxmlLoader;
    }

    private static void configureStage(String title, Scene scene) {
        mainStage.setTitle(title);
        mainStage.setScene(scene);

        // Center the stage on screen
        mainStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}