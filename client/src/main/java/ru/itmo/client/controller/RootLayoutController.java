package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;

import java.util.Locale;
import java.util.ResourceBundle;

public class RootLayoutController {
    private MainApp mainApp;
    private Runner runner;
    private ResourceBundle bundle;
    private Locale currentLocale;

    @FXML
    private MenuItem menuItemEnglish;
    @FXML
    private MenuItem menuItemRussian;
    @FXML
    private MenuItem menuItemFinnish;
    @FXML
    private MenuItem menuItemSpanish;
    @FXML
    private MenuItem menuItemSwedish;

    public void setMainApp(MainApp mainApp, ResourceBundle bundle) {
        this.mainApp = mainApp;
        this.bundle = bundle;
    }

    @FXML
    private void initialize() {
        menuItemEnglish.setOnAction(event -> mainApp.changeLocale(new Locale("en", "US")));
        menuItemRussian.setOnAction(event -> mainApp.changeLocale(new Locale("ru", "RU")));
        menuItemFinnish.setOnAction(event -> mainApp.changeLocale(new Locale("fi", "FI")));
        menuItemSpanish.setOnAction(event -> mainApp.changeLocale(new Locale("es", "CR")));
        menuItemSwedish.setOnAction(event -> mainApp.changeLocale(new Locale("sv", "SE")));
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
        if (runner.getCurrentUsername() == null) {
            MainApp.showAlert(bundle.getString("user.not.registered.title"), null, bundle.getString("user.not.registered.message"));
            return;
        }

        Object[] infoData = runner.getInfo(); // Предполагаем, что runner.getInfoData() возвращает массив Object[], содержащий тип коллекции, размер коллекции и время последнего сохранения.
        String collectionType = (String) infoData[0];
        int collectionSize = (int) infoData[1];
        String lastSaveTime = (String) infoData[2];

        String infoMessage = String.format(bundle.getString("collection.info.message"),
                collectionType, collectionSize, lastSaveTime);

        MainApp.showAlert(bundle.getString("collection.info.title"), null, infoMessage);
    }



}
