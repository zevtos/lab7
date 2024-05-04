package ru.itmo.general.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.general.utility.console.Console;

/**
 * Команда 'exit'. Завершает выполнение программы (без сохранения в файл).
 * @author zevtos
 */
public class Exit extends Command {
    private Console console;

    /**
     * Конструктор для создания экземпляра команды Exit.
     *
     */
    public Exit() {
        super(CommandName.EXIT, "завершить работу приложения");
    }
    /**
     * Конструктор для создания экземпляра команды Exit.
     *
     * @param console объект для взаимодействия с консолью
     */
    public Exit(Console console) {
        this();
        this.console = console;
    }
    /**
     * Выполняет команду.
     *
     * @param arguments аргументы команды (ожидается отсутствие аргументов)
     * @return Успешность выполнения команды
     */
    @Override
    public Response execute(Request arguments) {
        try {
            return new Response(true, null);
        } catch (Exception e){
            return new Response(false, e.toString());
        }
    }
    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        if (arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        return new Request(getName(), "Выполнение скрипта '" + arguments[1] + "'...");
    }
}
