package ru.itmo.server.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.models.Person;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.server.managers.collections.TicketCollectionManager;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 *
 * @author zevtos
 */
public class AddPerson extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    /**
     * Конструктор для создания экземпляра команды Add.
     *
     * @param ticketCollectionManager менеджер коллекции
     */
    public AddPerson(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.ADD_PERSON, "{element} добавить новый объект Person в коллекцию");
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду.
     *
     * @param request запрос на добавление
     * @return Успешность выполнения команды
     */
    @Override
    public Response execute(Request request) {
        try {
            Person person = (Person) request.getData();
            person.setId(ticketCollectionManager.getPersonManager().getFreeId());
            if (!person.validate()) return new Response(false, "Данные человека не валидны! Личность не добавлена!");
            if(!ticketCollectionManager.getPersonManager().add(person)) return new Response(false, "Человек уже существует!");
            return new Response(true, "Человек успешно добавлен!");
        } catch (Exception e){
            return new Response(false, e.toString());
        }
    }
}
