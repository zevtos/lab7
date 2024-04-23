package ru.itmo.client.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;

/**
 * Команда 'exit'. Завершает выполнение программы (без сохранения в файл).
 * @author zevtos
 */
public class Exit extends Command {
    private final Console console;

    /**
     * Конструктор для создания экземпляра команды Exit.
     *
     * @param console объект для взаимодействия с консолью
     */
    public Exit(Console console) {
        super(CommandName.EXIT, "завершить программу (без сохранения в файл)");
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
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        console.println("Завершение выполнения...");
        return new Request(getName(), null);
    }
}
