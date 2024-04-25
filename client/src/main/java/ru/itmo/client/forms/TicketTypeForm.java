package ru.itmo.client.forms;


import ru.itmo.client.utility.Interrogator;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.TicketType;

import java.util.NoSuchElementException;

/**
 * Форма для ввода типа билета.
 * @author zevtos
 */
public class TicketTypeForm extends Form<TicketType> {
    private final Console console;

    /**
     * Создает новый объект формы для ввода типа билета.
     * @param console Консоль для взаимодействия с пользователем.
     */
    public TicketTypeForm(Console console) {
        this.console = console;
    }

    /**
     * Строит объект типа билета на основе введенных данных.
     * @return Введенный тип билета или null, если ввод не был произведен.
     * @throws InvalidScriptInputException Если произошла ошибка при выполнении скрипта.
     */
    @Override
    public TicketType build() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();

        String strTicketType;
        TicketType ticketType;
        while (true) {
            try {
                console.println("Список типов билетов - " + TicketType.names());
                console.println("Введите тип билета (или 'null' для отмены):");
                console.prompt();

                strTicketType = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strTicketType);

                if (strTicketType.isEmpty() || strTicketType.equals("null")) return null;
                ticketType = TicketType.valueOf(strTicketType.toUpperCase());
                break;
            } catch (NoSuchElementException exception) {
                console.logError(getClass(), "Тип билета не распознан!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalArgumentException exception) {
                console.logError(getClass(), "Тип билета отсутствует в списке!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalStateException exception) {
                console.logError(getClass(), "Произошла непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return ticketType;
    }
}
