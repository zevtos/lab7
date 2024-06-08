package ru.itmo.client.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.itmo.client.MainApp;
import ru.itmo.general.models.*;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

public class TicketEditDialogController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField coordinatesField;
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

    private NumberFormat numberFormat;
    private NumberFormat currencyFormat;

    @FXML
    private void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList(TicketType.values()));
        hairColorComboBox.setItems(FXCollections.observableArrayList(Color.values()));

        Locale locale = Locale.getDefault();
        numberFormat = NumberFormat.getNumberInstance(locale);
        currencyFormat = NumberFormat.getCurrencyInstance(locale);
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;

        nameField.setText(ticket.getName());
        coordinatesField.setText(ticket.getCoordinates().toString());
        priceField.setText(Double.toString(ticket.getPrice()));
        discountField.setText(ticket.getDiscount() == null ? "" : numberFormat.format(ticket.getDiscount()));
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
            ticket.setPrice(priceField.getText().isEmpty() ? 0 : Double.parseDouble(priceField.getText()));
            ticket.setDiscount((discountField.getText().isEmpty()) ? null : Long.parseLong(discountField.getText()));
            ticket.setComment(commentField.getText().isEmpty() ? null : commentField.getText());
            ticket.setType(typeComboBox.getValue());

            LocalDate birthday = birthdayField.getValue();
            Float height = heightField.getText().isEmpty() ? null : Float.parseFloat(heightField.getText());
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
                Float y = coords[1].isEmpty() ? null : Float.parseFloat(coords[1]);
            } catch (NumberFormatException e) {
                errorMessage += bundle.getString("ticket.edit.invalid.coordinates.format") + "\n";
            }
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
