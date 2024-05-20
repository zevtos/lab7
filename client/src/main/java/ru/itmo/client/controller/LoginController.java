package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;

import java.util.ResourceBundle;

public class LoginController {
    private MainApp mainApp;
    private Runner runner;
    private ResourceBundle bundle;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private TextArea messageOutput;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        System.out.println(username);
        String password = passwordField.getText();
        System.out.println(password);
        Runner.ExitCode result = runner.executeLogin(username, password);
        if (result == Runner.ExitCode.OK) {
            mainApp.showMainScreen(bundle);
        } else {
            messageOutput.appendText(bundle.getString("login.failed") + "\n");
        }
    }

    @FXML
    private void handleRegister() {
        mainApp.showRegisterScreen(bundle);
    }
}
