package ru.itmo.server.managers;



import lombok.Getter;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.server.commands.core.*;
import ru.itmo.server.commands.custom.*;
import ru.itmo.server.commands.special.SumOfPrice;
import ru.itmo.server.commands.update.Update;
import ru.itmo.server.managers.collections.TicketCollectionManager;

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
    /**
     * -- GETTER --
     *  Получает словарь команд.
     *
     */
    @Getter
    private static Map<String, Command> commands;
    /**
     * -- GETTER --
     *  Получает историю команд.
     *
     */
    @Getter
    private static final List<String> commandHistory = new ArrayList<>();
    /**
     * Регистрирует команду.
     *
     * @param commandName Название команды.
     * @param command     Команда
     */
    public static void register(String commandName, Command command) {
        commands.put(commandName, command);
    }
    public static void init(TicketCollectionManager ticketCollectionManager){
        commands = new HashMap<>();
        register("save", new Save(ticketCollectionManager));
        register("info", new Info(ticketCollectionManager));
        register("show", new Show(ticketCollectionManager));
        register("add", new Add(ticketCollectionManager));
        register("update", new Update(ticketCollectionManager));
        register("remove_by_id", new Remove(ticketCollectionManager));
        register("clear", new Clear(ticketCollectionManager));
        register("exit", new Exit());
        register("remove_first", new RemoveFirst(ticketCollectionManager));
        register("remove_head", new RemoveHead(ticketCollectionManager));
        register("add_if_min", new AddIfMin(ticketCollectionManager));
        register("sum_of_price", new SumOfPrice(ticketCollectionManager));
        register("min_by_discount", new MinByDiscount(ticketCollectionManager));
        register("max_by_name", new MaxByName(ticketCollectionManager));
        register("add_person", new AddPerson(ticketCollectionManager));
    }
    public static Response handle(Request request) {
        var command = getCommands().get(request.getCommand());
        if (command == null) return new Response(false, request.getCommand(), "Команда не найдена!");
        if(!"exit".equals(request.getCommand()) && !"save".equals(request.getCommand())) {
            return command.execute(request);
        }
        return new Response(false, "Неизвестная команда");
    }
    public static void handleServer(Request request) {
        var command = getCommands().get(request.getCommand());
        if (command == null) return;
        if("exit".equals(request.getCommand()) || "save".equals(request.getCommand())) command.execute(request);
    }
}
