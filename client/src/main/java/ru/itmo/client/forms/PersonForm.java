package ru.itmo.client.forms;

import ru.itmo.client.utility.Interrogator;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.exceptions.*;
import ru.itmo.general.models.Color;
import ru.itmo.general.models.Person;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

/**
 * Форма для создания объекта Person.
 */
public class PersonForm extends Form<Person> {
    private final Console console;
    private final float MIN_HEIGHT = 0;

    /**
     * Создает новую форму для создания объекта Person.
     *
     * @param console Консоль для взаимодействия с пользователем.
     */
    public PersonForm(Console console) {
        this.console = console;
    }

    /**
     * Строит объект Person на основе введенных данных.
     *
     * @return Созданный объект Person.
     * @throws InvalidScriptInputException Если произошла ошибка при выполнении скрипта.
     * @throws InvalidFormException        Если введенные данные неверны.
     */
    public Person build() throws InvalidScriptInputException, InvalidFormException {
        console.println("Введите id=x, где id это passportID, чтобы использовать данные человека. " +
                "Любой другой ввод приведет к добавлению нового человека");
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
     * Запрашивает цвет волос.
     *
     * @return Цвет волос.
     * @throws InvalidScriptInputException Если произошла ошибка при выполнении скрипта.
     */
    private Color askHairColor() throws InvalidScriptInputException {
        return new ColorForm(console).build();
    }

    /**
     * Запрашивает passportID.
     *
     * @return PassportID.
     * @throws InvalidScriptInputException Если произошла ошибка при выполнении скрипта.
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
     * Запрашивает рост.
     *
     * @return Рост.
     * @throws InvalidScriptInputException Если произошла ошибка при выполнении скрипта.
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
     * Запрашивает дату рождения.
     *
     * @return Дата рождения.
     * @throws InvalidScriptInputException Если произошла ошибка при выполнении скрипта.
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
