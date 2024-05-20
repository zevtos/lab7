package ru.itmo.client.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.itmo.client.MainApp;
import ru.itmo.general.models.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @FXML
    private void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList(TicketType.values()));
        hairColorComboBox.setItems(FXCollections.observableArrayList(Color.values()));
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;

        nameField.setText(ticket.getName());
        coordinatesField.setText(ticket.getCoordinates().toString());
        creationDateField.setValue(ticket.getCreationDate().toLocalDate());
        priceField.setText(Double.toString(ticket.getPrice()));
        discountField.setText(Long.toString(ticket.getDiscount()));
        commentField.setText(ticket.getComment());
        typeComboBox.setItems(FXCollections.observableArrayList(TicketType.values()));
        typeComboBox.setValue(ticket.getType());

        if (ticket.getPerson() != null) {
            birthdayField.setValue(ticket.getPerson().birthday().toLocalDate());
            heightField.setText(Float.toString(ticket.getPerson().height()));
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

            System.out.println("Ticket before sending: " + ticket); // Debug message

            okClicked = true;
            dialogStage.close();
        } else {
            MainApp.showAlert(bundle.getString("ticket.edit.invalid.title"),
                    bundle.getString("ticket.edit.invalid.header"),
                    bundle.getString("ticket.edit.invalid.content"));
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += bundle.getString("ticket.edit.invalid.name") + "\n";
        }
        if (coordinatesField.getText() == null || coordinatesField.getText().length() == 0) {
            errorMessage += bundle.getString("ticket.edit.invalid.coordinates") + "\n";
        } else {
            try {
                String[] coords = coordinatesField.getText().split(";");
                Double x = Double.parseDouble(coords[0]);
                Float y = Float.parseFloat(coords[1]);
                if (x == null || y == null) {
                    errorMessage += bundle.getString("ticket.edit.invalid.coordinates.format") + "\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += bundle.getString("ticket.edit.invalid.coordinates.format") + "\n";
            }
        }
        if (creationDateField.getValue() == null) {
            errorMessage += bundle.getString("ticket.edit.invalid.creationDate") + "\n";
        }
        if (priceField.getText() == null || priceField.getText().length() == 0) {
            errorMessage += bundle.getString("ticket.edit.invalid.price") + "\n";
        } else {
            try {
                double price = Double.parseDouble(priceField.getText());
                if (price <= 0) {
                    errorMessage += bundle.getString("ticket.edit.invalid.price.format") + "\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += bundle.getString("ticket.edit.invalid.price.format") + "\n";
            }
        }
        if (discountField.getText() != null && discountField.getText().length() != 0) {
            try {
                long discount = Long.parseLong(discountField.getText());
                if (discount <= 0 || discount > 100) {
                    errorMessage += bundle.getString("ticket.edit.invalid.discount.format") + "\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += bundle.getString("ticket.edit.invalid.discount.format") + "\n";
            }
        }
        if (commentField.getText() == null || commentField.getText().length() == 0) {
            errorMessage += bundle.getString("ticket.edit.invalid.comment") + "\n";
        }
        if (typeComboBox.getValue() == null) {
            errorMessage += bundle.getString("ticket.edit.invalid.type") + "\n";
        }
        if (birthdayField.getValue() == null) {
            errorMessage += bundle.getString("ticket.edit.invalid.birthday") + "\n";
        }
        if (heightField.getText() == null || heightField.getText().length() == 0) {
            errorMessage += bundle.getString("ticket.edit.invalid.height") + "\n";
        } else {
            try {
                float height = Float.parseFloat(heightField.getText());
                if (height <= 0) {
                    errorMessage += bundle.getString("ticket.edit.invalid.height.format") + "\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += bundle.getString("ticket.edit.invalid.height.format") + "\n";
            }
        }
        if (passportIDField.getText() == null || passportIDField.getText().length() == 0) {
            errorMessage += bundle.getString("ticket.edit.invalid.passportID") + "\n";
        }
        if (hairColorComboBox.getValue() == null) {
            errorMessage += bundle.getString("ticket.edit.invalid.hairColor") + "\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            MainApp.showAlert(bundle.getString("ticket.edit.invalid.title"),
                    bundle.getString("ticket.edit.invalid.header"),
                    errorMessage);
            return false;
        }
    }


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
