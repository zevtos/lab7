package ru.itmo.general.models.forms;

import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidRangeException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.Color;
import ru.itmo.general.models.Person;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.utility.console.Console;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

/**
 * Form for creating a Person object.
 * Handles user input to create a Person object.
 *
 * @author zevtos
 */
public class PersonForm extends Form<Person> {
    private final Console console;
    private final float MIN_HEIGHT = 0;

    /**
     * Constructs a new form for creating a Person object.
     *
     * @param console The console for interacting with the user.
     */
    public PersonForm(Console console) {
        this.console = console;
    }

    /**
     * Builds a Person object based on the entered data.
     *
     * @return The created Person object.
     * @throws InvalidScriptInputException If an error occurs while executing the script.
     * @throws InvalidFormException        If the entered data is invalid.
     */
    public Person build() throws InvalidScriptInputException, InvalidFormException {
        console.println("Укажите человека на которого выписан билет, введите id=x, где id это passportID");
        console.prompt();

        var fileMode = Interrogator.fileMode();
        String input = Interrogator.getUserScanner().nextLine().trim();
        if (fileMode) console.println(input);
        if (input.equals("null")) return null;
        boolean flag = false;
        if (input.startsWith("id=") || input.startsWith("ID=")) {
            input = input.replaceFirst("^(id=|ID=)", "");
            flag = true;
        }

        if (flag) {
            console.println("! Добавление новой личности:");
            return new Person(
                    askBirthday(),
                    askHeight(),
                    input,
                    askHairColor()
            );
        }
        console.println("! Добавление новой личности:");
        var person = new Person(
                askBirthday(),
                askHeight(),
                askPassportID(),
                askHairColor()
        );
        if (!person.validate()) throw new InvalidFormException();
        return person;
    }

    /**
     * Requests hair color.
     *
     * @return Hair color.
     * @throws InvalidScriptInputException If an error occurs while executing the script.
     */
    private Color askHairColor() throws InvalidScriptInputException {
        return new ColorForm(console).build();
    }

    /**
     * Requests passportID.
     *
     * @return PassportID.
     * @throws InvalidScriptInputException If an error occurs while executing the script.
     */
    private String askPassportID() throws InvalidScriptInputException {
        String passportID;
        var fileMode = Interrogator.fileMode();
        while (true) {
            try {
                console.println("Введите passportID:");
                console.prompt();

                passportID = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(passportID);
                if (passportID.isEmpty()) throw new EmptyValueException();
                break;
            } catch (NoSuchElementException exception) {
                console.printError(getClass(), "PassportID не распознан!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (EmptyValueException exception) {
                console.printError(getClass(), "PassportID не может быть пустым!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalStateException exception) {
                console.printError(getClass(), "Непредвиденная ошибка!");
                System.exit(0);
            }
        }

        return passportID;
    }

    /**
     * Requests height.
     *
     * @return Height.
     * @throws InvalidScriptInputException If an error occurs while executing the script.
     */
    private Float askHeight() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();
        float height;
        while (true) {
            try {
                console.println("Введите рост:");
                console.prompt();

                var strHeight = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strHeight);

                height = Float.parseFloat(strHeight);
                if (height <= MIN_HEIGHT) throw new InvalidRangeException();
                break;
            } catch (NoSuchElementException exception) {
                console.printError(getClass(), "Рост не распознан!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (InvalidRangeException exception) {
                console.printError(getClass(), "Рост должен быть больше нуля!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NumberFormatException exception) {
                console.printError(getClass(), "Рост должен быть представлен числом!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError(getClass(), "Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return height;
    }

    /**
     * Requests the date of birth.
     *
     * @return Date of birth.
     * @throws InvalidScriptInputException If an error occurred while executing the script.
     */
    private LocalDateTime askBirthday() throws InvalidScriptInputException {
        LocalDateTime birthday;
        var fileMode = Interrogator.fileMode();
        try {
            while (true) {
                console.print("birthday-data-time (Exemple: " +
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " or 2020-02-20): ");
                var line = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(line);
                if (line.equals("exit")) throw new InvalidScriptInputException();
                if (line.isEmpty()) {
                    birthday = null;
                    break;
                }
                try {
                    birthday = LocalDateTime.parse(line, DateTimeFormatter.ISO_DATE_TIME);
                    if (birthday.isAfter(LocalDateTime.now())) throw new InvalidRangeException();
                    break;
                } catch (DateTimeParseException ignored) {
                    try {
                        birthday = LocalDateTime.parse(line + "T00:00:00.0000", DateTimeFormatter.ISO_DATE_TIME);
                        if (birthday.isAfter(LocalDateTime.now())) throw new InvalidRangeException();
                        break;
                    } catch (DateTimeParseException ignored1) {
                    } catch (InvalidRangeException e) {
                        console.printError(getClass(), "Дата рождения не может быть позже текущей");
                        continue;
                    }
                    console.printError(getClass(), "Ошибка чтения даты. Некорректный формат. Требуемый формат: " +
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) +
                            " или YYYY-MM-DD");
                } catch (InvalidRangeException e) {
                    console.printError(getClass(), "Дата рождения не может быть позже текущей");
                }

            }
            return birthday;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError(getClass(), "Ошибка чтения");
            return null;
        }
    }

}
