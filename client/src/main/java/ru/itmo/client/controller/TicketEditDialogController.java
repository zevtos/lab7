package ru.itmo.client.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.itmo.general.models.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class TicketEditDialogController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField coordinatesField;
    @FXML
    private DatePicker creationDateField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField discountField;
    @FXML
    private TextField commentField;
    @FXML
    private ComboBox<TicketType> typeComboBox;
    @FXML
    private DatePicker birthdayField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField passportIDField;
    @FXML
    private ComboBox<Color> hairColorComboBox;

    private Stage dialogStage;
    private Ticket ticket;
    private boolean okClicked = false;
    private ResourceBundle bundle;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;

        nameField.setText(ticket.getName());
        coordinatesField.setText(ticket.getCoordinates().toString());
        creationDateField.setValue(ticket.getCreationDate().toLocalDate());
        priceField.setText(Double.toString(ticket.getPrice()));
        discountField.setText(ticket.getDiscount().toString());
        commentField.setText(ticket.getComment());
        typeComboBox.setItems(FXCollections.observableArrayList(TicketType.values()));
        typeComboBox.setValue(ticket.getType());

        if (ticket.getPerson() != null) {
            birthdayField.setValue(ticket.getPerson().birthday().toLocalDate());
            heightField.setText(ticket.getPerson().height().toString());
            passportIDField.setText(ticket.getPerson().passportID());
            hairColorComboBox.setItems(FXCollections.observableArrayList(Color.values()));
            hairColorComboBox.setValue(ticket.getPerson().hairColor());
        }
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            ticket.setName(nameField.getText());
            ticket.setCoordinates(new Coordinates(
                    Double.parseDouble(coordinatesField.getText().split(";")[0]),
                    Float.parseFloat(coordinatesField.getText().split(";")[1])
            ));
            ticket.setCreationDate(ZonedDateTime.of(creationDateField.getValue().atStartOfDay(), ZoneId.systemDefault()));
            ticket.setPrice(Double.parseDouble(priceField.getText()));
            ticket.setDiscount(Long.parseLong(discountField.getText()));
            ticket.setComment(commentField.getText());
            ticket.setType(typeComboBox.getValue());

            LocalDate birthday = birthdayField.getValue();
            Float height = Float.parseFloat(heightField.getText());
            String passportID = passportIDField.getText();
            Color hairColor = hairColorComboBox.getValue();
            Person person = new Person(birthday.atStartOfDay(), height, passportID, hairColor);
            ticket.setPerson(person);

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "No valid name!\n";
        }
        if (coordinatesField.getText() == null || coordinatesField.getText().isEmpty()) {
            errorMessage += "No valid coordinates!\n";
        }
        if (priceField.getText() == null || priceField.getText().isEmpty()) {
            errorMessage += "No valid price!\n";
        }
        if (discountField.getText() == null || discountField.getText().isEmpty()) {
            errorMessage += "No valid discount!\n";
        }
        if (commentField.getText() == null || commentField.getText().isEmpty()) {
            errorMessage += "No valid comment!\n";
        }
        if (typeComboBox.getValue() == null) {
            errorMessage += "No valid type!\n";
        }
        if (birthdayField.getValue() == null) {
            errorMessage += "No valid birthday!\n";
        }
        if (heightField.getText() == null || heightField.getText().isEmpty()) {
            errorMessage += "No valid height!\n";
        }
        if (passportIDField.getText() == null || passportIDField.getText().isEmpty()) {
            errorMessage += "No valid passport ID!\n";
        }
        if (hairColorComboBox.getValue() == null) {
            errorMessage += "No valid hair color!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(errorMessage);
            return false;
        }
    }

    private void showAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Fields");
        alert.setHeaderText("Please correct invalid fields");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
