package ru.itmo.general.commands;

import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

/**
 * Интерфейс для всех выполняемых команд.
 *
 * @author zevtos
 */
public interface Executable {
    /**
     * Выполнить команду с заданными аргументами.
     *
     * @param arguments Аргументы команды.
     * @return true, если выполнение команды завершилось успешно, иначе false.
     */
    default Response execute(Request arguments) {
        return null;
    }

    ;

    default Request execute(String[] arguments) {
        return null;
    }

    ;
}
