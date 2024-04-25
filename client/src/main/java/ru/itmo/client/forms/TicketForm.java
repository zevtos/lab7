package ru.itmo.client.forms;

import ru.itmo.client.utility.Interrogator;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidRangeException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.Coordinates;
import ru.itmo.general.models.Person;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.TicketType;

import java.util.NoSuchElementException;

/**
 * Форма для создания билета.
 * @author zevtos
 */
public class TicketForm extends Form<Ticket> {
    private static final int nextId = 0;
    private final Console console;

    /**
     * Создает новую форму для создания билета.
     *
     * @param console Консоль для взаимодействия с пользователем.
     */
    public TicketForm(Console console) {
        this.console = console;
    }

    /**
     * Строит объект билета на основе введенных данных.
     *
     * @return Созданный билет.
     * @throws InvalidScriptInputException Если произошла ошибка при выполнении скрипта.
     * @throws InvalidFormException        Если введенные данные неверны.
     */
    @Override
    public Ticket build() throws InvalidScriptInputException, InvalidFormException {
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
        if (!ticket.validateClient()) throw new InvalidFormException();
        return ticket;
    }

    /**
     * Запрашивает скидку на билет.
     *
     * @return Скидка на билет.
     * @throws InvalidScriptInputException Если произошла ошибка при выполнении скрипта.
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
                console.logError(getClass(), "Скидка не распознана!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (InvalidRangeException exception) {
                console.logError(getClass(), "Процент скидки должен быть в диапазоне от 0 до 100!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NumberFormatException exception) {
                console.logError(getClass(), "Скидка должна быть представлена целым числом от 0 до 100!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.logError(getClass(), "Непредвиденная ошибка!");
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
                break;
            } catch (NoSuchElementException exception) {
                console.logError(getClass(), "Название не распознано!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (EmptyValueException exception) {
                console.logError(getClass(), "Название не может быть пустым!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalStateException exception) {
                console.logError(getClass(), "Непредвиденная ошибка!");
                System.exit(0);
            }
        }

        return name;
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
                console.logError(getClass(), "Цена билета не распознана!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (InvalidRangeException exception) {
                console.logError(getClass(), "Цена билета должна быть больше нуля!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NumberFormatException exception) {
                console.logError(getClass(), "Цена билета должна быть представлена числом!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.logError(getClass(), "Непредвиденная ошибка!");
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
                console.logError(getClass(), "Комментарий не распознан!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalStateException exception) {
                console.logError(getClass(), "Непредвиденная ошибка!");
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