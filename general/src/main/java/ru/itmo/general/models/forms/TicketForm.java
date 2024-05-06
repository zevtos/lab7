package ru.itmo.general.models.forms;

import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidRangeException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.Coordinates;
import ru.itmo.general.models.Person;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.TicketType;
import ru.itmo.general.utility.console.Console;

import java.util.NoSuchElementException;

/**
 * Form for creating a ticket.
 * Handles user input to create a ticket object.
 *
 * @author zevtos
 */
public class TicketForm extends Form<Ticket> {
    private static final int nextId = 0;
    private final Console console;

    /**
     * Constructs a new form for creating a ticket.
     *
     * @param console The console for interacting with the user.
     */
    public TicketForm(Console console) {
        this.console = console;
    }

    /**
     * Builds a ticket object based on the entered data.
     *
     * @return The created ticket.
     * @throws InvalidScriptInputException If an error occurs while executing the script.
     * @throws InvalidFormException        If the entered data is invalid.
     */
    @Override
    public Ticket build() throws InvalidScriptInputException, InvalidFormException {
        //todo:обработать ctrl d
        var ticket = new Ticket(
                null,
                askName(),
                askCoordinates(),
                askPrice(),
                askDiscount(),
                askComment(),
                askTicketType(),
                askPerson()
        );
        if (!ticket.validate()) throw new InvalidFormException();
        return ticket;
    }

    /**
     * Requests a discount on the ticket.
     *
     * @return discount on the ticket.
     * @throws InvalidScriptInputException if an error occurred while executing the script.
     */
    public Long askDiscount() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();
        Long discount;
        while (true) {
            try {
                console.println("Введите скидку на билет:");
                console.prompt();

                var strDiscount = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strDiscount);
                if (strDiscount.isEmpty()) {
                    discount = null;
                    break;
                }
                discount = Long.parseLong(strDiscount);
                if (discount <= 0 || discount > 100) throw new InvalidRangeException();
                break;
            } catch (NoSuchElementException exception) {
                console.printError(getClass(), "Скидка не распознана!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (InvalidRangeException exception) {
                console.printError(getClass(), "Процент скидки должен быть в диапазоне от 0 до 100!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NumberFormatException exception) {
                console.printError(getClass(), "Скидка должна быть представлена целым числом от 0 до 100!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError(getClass(), "Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return discount;
    }

    private String askName() throws InvalidScriptInputException {
        String name;
        var fileMode = Interrogator.fileMode();
        while (true) {
            try {
                console.println("Введите название билета:");
                console.prompt();

                name = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(name);
                if (name.isEmpty()) throw new EmptyValueException();
                return name;
            } catch (NoSuchElementException exception) {
                console.printError(getClass(), "Название не распознано!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (EmptyValueException exception) {
                console.printError(getClass(), "Название не может быть пустым!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalStateException exception) {
                console.printError(getClass(), "Непредвиденная ошибка!");
                System.exit(0);
            }
        }
    }

    private Coordinates askCoordinates() throws InvalidScriptInputException, InvalidFormException {
        return new CoordinatesForm(console).build();
    }

    private Double askPrice() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();
        double price;
        while (true) {
            try {
                console.println("Введите цену билета:");
                console.prompt();

                var strPrice = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strPrice);

                price = Double.parseDouble(strPrice);
                long MIN_PRICE = 0;
                if (price < MIN_PRICE) throw new InvalidRangeException();
                break;
            } catch (NoSuchElementException exception) {
                console.printError(getClass(), "Цена билета не распознана!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (InvalidRangeException exception) {
                console.printError(getClass(), "Цена билета должна быть больше нуля!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NumberFormatException exception) {
                console.printError(getClass(), "Цена билета должна быть представлена числом!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError(getClass(), "Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return price;
    }

    private String askComment() throws InvalidScriptInputException {
        String partNumber;
        var fileMode = Interrogator.fileMode();
        while (true) {
            try {
                console.println("Введите комментарий билета:");
                console.prompt();

                partNumber = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(partNumber);
                if (partNumber.isEmpty()) {
                    partNumber = null;
                }
                break;
            } catch (NoSuchElementException exception) {
                console.printError(getClass(), "Комментарий не распознан!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalStateException exception) {
                console.printError(getClass(), "Непредвиденная ошибка!");
                System.exit(0);
            }
        }

        return partNumber;
    }

    private TicketType askTicketType() throws InvalidScriptInputException {
        return new TicketTypeForm(console).build();
    }

    private Person askPerson() throws InvalidScriptInputException, InvalidFormException {
        return new PersonForm(console).build();
    }
}