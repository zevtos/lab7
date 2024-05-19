package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;

import java.util.ResourceBundle;

public class RegisterController {
    private MainApp mainApp;
    private Runner runner;
    private ResourceBundle bundle;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button registerButton;
    @FXML
    private TextArea messageOutput;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleRegister());
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            messageOutput.appendText(bundle.getString("register.password.mismatch") + "\n");
            return;
        }

        Runner.ExitCode result = runner.executeRegister(username, password);
        if (result == Runner.ExitCode.OK) {
            mainApp.showLoginScreen(bundle);
        } else {
            messageOutput.appendText(bundle.getString("register.failed") + "\n");
        }
    }

    @FXML
    private void handleBackToLogin() {
        mainApp.showLoginScreen(bundle);
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
