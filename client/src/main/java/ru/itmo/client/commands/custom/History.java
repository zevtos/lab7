package ru.itmo.client.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.client.managers.CommandManager;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;

/**
 * Команда 'history'. Выводит историю использованных команд.
 * @author zevtos
 */
public class History extends Command {
    private final Console console;
    private final CommandManager commandManager;

    /**
     * Конструктор для создания экземпляра команды History.
     *
     * @param console        объект для взаимодействия с консолью
     * @param commandManager менеджер команд
     */
    public History(Console console, CommandManager commandManager) {
        super(CommandName.HISTORY, "вывести список использованных команд");
        this.console = console;
        this.commandManager = commandManager;
    }

    /**
     * Выполняет команду.
     *
     * @param arguments аргументы команды (в данном случае ожидается отсутствие аргументов)
     * @return Успешность выполнения команды
     */
    @Override
    public Request execute(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        commandManager.getCommandHistory().forEach(console::println);
        return new Request(getName(), null);
    }
}
