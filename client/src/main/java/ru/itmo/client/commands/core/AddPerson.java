package ru.itmo.client.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.client.forms.PersonForm;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.network.Request;
import ru.itmo.general.commands.CommandName;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 *
 * @author zevtos
 */
public class AddPerson extends Command {
    private final Console console;

    /**
     * Конструктор для создания экземпляра команды Add.
     *
     * @param console              объект для взаимодействия с консолью
     */
    public AddPerson(Console console) {
        super(CommandName.ADD_PERSON, " {element} добавить новый объект Person в коллекцию");
        this.console = console;
    }

    /**
     * Выполняет команду.
     *
     * @param arguments аргументы команды (ожидается отсутствие аргументов)
     * @return Успешность выполнения команды
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments.length > 1 && !arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            console.println("* Создание нового пользователя:");
            var personForm = new PersonForm(console);
            var person = personForm.build();
            return new Request(getName(), person);
//            if(tcpClient.sendCommand(new Request(getName(), person)).isSuccess()) {
//                console.println("Пользователь успешно добавлен!");
//                return true;
//            } else {
//                console.println("Пользователь с таким PassportID уже существует!");
//                return new Request();
//            }
        } catch (InvalidNumberOfElementsException exception) {
            console.printError("Неправильное количество аргументов!");
            console.println(getUsingError());
        } catch (InvalidFormException exception) {
            console.printError("Поля пользователя не валидны! Пользователь не создан!");
        } catch (InvalidScriptInputException ignored) {
            // Ignored
        }
        return new Request();
    }
}
