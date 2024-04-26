package ru.itmo.server.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;

/**
 * Команда 'exit'. Завершает выполнение программы (без сохранения в файл).
 * @author zevtos
 */
public class Exit extends Command {
    /**
     * Конструктор для создания экземпляра команды Exit.
     *
     */
    public Exit() {
        super(CommandName.EXIT, "завершить программу (без сохранения в файл)");
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
}
