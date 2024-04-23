package ru.itmo.client.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.client.managers.CommandManager;
import ru.itmo.client.network.TCPClient;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;


/**
 * Команда 'help'. Выводит справку по доступным командам.
 * @author zevtos
 */
public class Help extends Command {
    private final Console console;
    private final CommandManager commandManager;

    /**
     * Конструктор для создания экземпляра команды Help.
     *
     * @param console        объект для взаимодействия с консолью
     * @param commandManager менеджер команд
     */
    public Help(Console console, CommandManager commandManager) {
        super(CommandName.HELP, "вывести справку по доступным командам");
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

        console.println("Справка по командам:");
        commandManager.getCommands().values().forEach(command -> {
            console.printTable(command.getName(), command.getDescription());
        });

        return new Request(getName(), null);
    }
}
