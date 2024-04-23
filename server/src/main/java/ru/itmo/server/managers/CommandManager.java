package ru.itmo.server.managers;



import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.server.commands.core.*;
import ru.itmo.server.commands.custom.*;
import ru.itmo.server.commands.special.SumOfPrice;
import ru.itmo.server.commands.update.Update;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.server.network.TCPServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Управляет командами.
 *
 * @author zevtos
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final List<String> commandHistory = new ArrayList<>();

    public CommandManager(TicketCollectionManager ticketCollectionManager){
        register("info", new Info(ticketCollectionManager));
        register("show", new Show(ticketCollectionManager));
        register("add", new Add(ticketCollectionManager));
        register("update", new Update(ticketCollectionManager));
        register("remove_by_id", new Remove(ticketCollectionManager));
        register("clear", new Clear(ticketCollectionManager));
        register("exit", new Exit(ticketCollectionManager));
        register("remove_first", new RemoveFirst(ticketCollectionManager));
        register("remove_head", new RemoveHead(ticketCollectionManager));
        register("add_if_min", new AddIfMin(ticketCollectionManager));
        register("sum_of_price", new SumOfPrice(ticketCollectionManager));
        register("min_by_discount", new MinByDiscount(ticketCollectionManager));
        register("max_by_name", new MaxByName(ticketCollectionManager));
        register("add_person", new AddPerson(ticketCollectionManager));
    }

    /**
     * Регистрирует команду.
     *
     * @param commandName Название команды.
     * @param command     Команда.
     */
    public void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    /**
     * Получает словарь команд.
     *
     * @return Словарь команд.
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    /**
     * Получает историю команд.
     *
     * @return История команд.
     */
    public List<String> getCommandHistory() {
        return commandHistory;
    }

    /**
     * Добавляет команду в историю.
     *
     * @param command Команда.
     */
    public void addToHistory(String command) {
        commandHistory.add(command);
    }

    public Response handle(Request request) {
        var command = this.getCommands().get(request.getCommand());
        if (command == null) return new Response(false, request.getCommand(), "Команда не найдена!");
        return command.execute(request);
    }
}
