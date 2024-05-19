package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;

public class DataVisualizationController {
    private MainApp mainApp;
    private Runner runner;

    @FXML
    private Canvas canvas;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void initialize() {
        // Реализуйте логику визуализации данных
    }
}
