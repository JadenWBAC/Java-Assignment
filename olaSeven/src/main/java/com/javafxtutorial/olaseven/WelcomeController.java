package com.javafxtutorial.olaseven;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class WelcomeController {

    @FXML
    Label welcomeText;

    public void setWelcomeText(String message) {
        welcomeText.setText(message);
    }

    @FXML
    protected void onProceedButtonClick() throws IOException {
        com.javafxtutorial.olaseven.HelloApplication.loadBookstoreSystem();
    }
}