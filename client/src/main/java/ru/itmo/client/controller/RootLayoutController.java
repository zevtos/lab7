package ru.itmo.client.controller;

import javafx.fxml.FXML;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;

import java.util.ResourceBundle;

public class RootLayoutController {
    private MainApp mainApp;
    private Runner runner;
    private ResourceBundle bundle;

    public void setMainApp(MainApp mainApp, ResourceBundle bundle) {
        this.mainApp = mainApp;
        this.bundle = bundle;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleAbout() {
        String infoMessage = mainApp.getRunner().getInfo();
        MainApp.showAlert("Информация о коллекции", null, infoMessage);
    }
}
